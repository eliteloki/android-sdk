/* Several snippets of code from the jooq-codegen library have been used here */

package io.hasura.db.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

public class GenerationUtil {
    private static Gson gson =
            new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

    private static Set<String> JAVA_KEYWORDS = unmodifiableSet(new HashSet<String>(asList(
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "double",
            "do",
            "else",
            "enum",
            "extends",
            "false",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "interface",
            "int",
            "long",
            "native",
            "new",
            "null",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "true",
            "try",
            "void",
            "volatile",
            "while")));

    private static String escape(char c) {
        if (c == ' ' || c == '-' || c == '.')
            return "_";
        else
            return "_" + Integer.toHexString(c);
    }

    public static String convertToIdentifier(String literal) {
        if (JAVA_KEYWORDS.contains(literal))
            return literal + "_";

        StringBuilder sb = new StringBuilder();

        if ("".equals(literal)) {
            return "_";
        }

        for (int i = 0; i < literal.length(); i++) {
            char c = literal.charAt(i);

            if (!Character.isJavaIdentifierPart(c)) {
                sb.append(escape(c));
            } else if (i == 0 && !Character.isJavaIdentifierStart(literal.charAt(0))) {
                sb.append("_");
                sb.append(c);
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String toClassName(String tableName) {
        return toCamelCase(convertToIdentifier(tableName));
    }

    public static String toStaticVarName(String tableName) {
        return convertToIdentifier(tableName).toUpperCase();
    }

    public static String toMemberName(String columnName) {
        String result = toClassName(columnName);
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    public static String toCamelCase(String string) {
        StringBuilder result = new StringBuilder();

        // [#2515] - Keep trailing underscores
        for (String word : string.split("_", -1)) {

            // Uppercase first letter of a word
            if (word.length() > 0) {

                // [#82] - If a word starts with a digit, prevail the
                // underscore to prevent naming clashes
                if (Character.isDigit(word.charAt(0))) {
                    result.append("_");
                }

                result.append(word.substring(0, 1).toUpperCase());
                result.append(word.substring(1).toLowerCase());
            } else {
                // If no letter exists, prevail the underscore (e.g. leading
                // underscores)
                result.append("_");
            }
        }

        return result.toString();
    }

    public static void createBaseDirs() {
    }

    public static String getRecordPkgName(String pkgName) {
        return pkgName + ".records";
    }

    public static void generateRecord(
            File recordsDir, String pkgName, TableInfo tableInfo) throws IOException {

        String clsName = toClassName(tableInfo.getTableName());
        String recordFileName = clsName + "Record.java";

        PrintWriter writer = new PrintWriter(new File(recordsDir, recordFileName), "UTF-8");
        String recPkgName = pkgName + ".tables.records";
        writer.printf("package %s;%n", recPkgName);

        writer.println();
        writer.println("import com.google.gson.annotations.SerializedName;");
        writer.println("import java.util.ArrayList;");
        writer.println("import java.math.BigDecimal;");
        writer.println("import java.sql.Timestamp;");
        writer.println("import java.sql.Date;");
        writer.println("import java.sql.Time;");
        writer.println();

        writer.printf("public class %sRecord {%n", clsName);
        for (PGColInfo column : tableInfo.getColumns()) {
            String rawFldName = column.getColName();
            String fldName = toMemberName(rawFldName);
            String fldType = column.getColType().getJavaType();
            writer.printf("    @SerializedName(\"%s\")%n", rawFldName);
            writer.printf("    public %s %s;%n", fldType, fldName);
            writer.println();
        }

        for (RelInfo relInfo : tableInfo.getRelationships()) {
            String rawFldName = relInfo.getRelName();
            String fldName = toMemberName(rawFldName);
            writer.printf("    @SerializedName(\"%s\")%n", rawFldName);

            String remoteTableName = toClassName(relInfo.getRemoteTable());

            switch (relInfo.getRelType()) {
                case ARR_REL:
                    writer.printf("    public ArrayList<%sRecord> %s;%n", remoteTableName, fldName);
                    break;
                case OBJ_REL:
                    writer.printf("    public %sRecord %s;%n", remoteTableName, fldName);
                    break;
            }
            writer.println();
        }

        writer.println("}");
        writer.close();
    }

    public static void generateTable(
            File tablesDir, String pkgName, TableInfo tableInfo) throws IOException {
        String tableName = tableInfo.getTableName();
        String clsName = toClassName(tableName);

        String tableFileName = clsName + ".java";

        PrintWriter writer = new PrintWriter(new File(tablesDir, tableFileName), "UTF-8");
        String tablePkgName = pkgName + ".tables";
        writer.printf("package %s;%n", tablePkgName);

        writer.println();
        writer.println("import com.google.gson.reflect.*;");
        writer.println("import java.lang.reflect.Type;");
        writer.println("import java.util.ArrayList;");
        writer.println("import java.math.BigDecimal;");
        writer.println("import java.sql.Timestamp;");
        writer.println("import java.sql.Date;");
        writer.println("import java.sql.Time;");
        writer.println("import io.hasura.db.*;");

        writer.printf("import %s.records.*;", tablePkgName);
        writer.println();

        writer.printf("public class %s extends Table<%sRecord> {%n", clsName, clsName);

        // Static variable
        writer.printf("    public static final %s %s = new %s();%n", clsName, toStaticVarName(tableName), clsName);
        writer.println();

        // Constructor
        writer.printf("    public %s() {%n", clsName);
        writer.printf("        super(\"%s\");%n", tableName);
        writer.printf("    }%n");
        writer.println();

        // Insert return type
        writer.printf("    public Type getInsResType() {%n");
        writer.printf("        return new TypeToken<InsertResult<%sRecord>>() {}.getType();%n", clsName);
        writer.printf("    }%n");
        writer.println();

        // Select return type
        writer.printf("    public Type getSelResType() {%n");
        writer.printf("        return new TypeToken<ArrayList<%sRecord>>() {}.getType();%n", clsName);
        writer.printf("    }%n");
        writer.println();

        // Update return type
        writer.printf("    public Type getUpdResType() {%n");
        writer.printf("        return new TypeToken<UpdateResult<%sRecord>>() {}.getType();%n", clsName);
        writer.printf("    }%n");
        writer.println();

        // Delete return type
        writer.printf("    public Type getDelResType() {%n");
        writer.printf("        return new TypeToken<DeleteResult<%sRecord>>() {}.getType();%n", clsName);
        writer.printf("    }%n");
        writer.println();

        for (PGColInfo column : tableInfo.getColumns()) {
            String columnName = column.getColName();
            String fldType = column.getColType().getJavaType();
            writer.printf("    public final PGField<%sRecord, %s> %s = new PGField<>(\"%s\");%n", clsName, fldType, toStaticVarName(columnName), columnName);
        }

        writer.println();
        for (RelInfo relInfo : tableInfo.getRelationships()) {
            String relName = relInfo.getRelName();
            String remoteTableClsName = toClassName(relInfo.getRemoteTable());

            switch (relInfo.getRelType()) {
                case ARR_REL:
                    writer.printf("    public final ArrayRelationship<%sRecord, %sRecord> %s = new ArrayRelationship<>(\"%s\");%n", clsName, remoteTableClsName, toStaticVarName(relName), relName);
                    break;
                case OBJ_REL:
                    writer.printf("    public final ObjectRelationship<%sRecord, %sRecord> %s = new ObjectRelationship<>(\"%s\");%n", clsName, remoteTableClsName, toStaticVarName(relName), relName);
                    break;
            }
        }

        writer.println("}");
        writer.close();
    }

    public static DBInfo fetchDBInfo(
            String url, String dbPrefix, String adminAPIKey) throws IOException {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();
        Request request = new Request.Builder()
                .url(url + dbPrefix + "/table")
                .header("Authorization", "Hasura " + adminAPIKey)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        String respStr = response.body().string();
        Type tabInfoListType = new TypeToken<ArrayList<TableInfo>>() {}.getType();
        List<TableInfo> tables = gson.fromJson(respStr, tabInfoListType);
        return new DBInfo(tables);
    }

    public static void generateTablesJava(
            String dir, String pkgName, List<String> tableNames) throws IOException {

        String fileName = "Tables.java";

        PrintWriter writer = new PrintWriter(new File(dir, fileName), "UTF-8");
        writer.printf("package %s;%n", pkgName);

        writer.println();
        writer.printf("import %s.tables.*;%n", pkgName);
        writer.println();

        writer.printf("public class Tables {%n");

        for (String tableName : tableNames) {
            String clsName = toClassName(tableName);
            String staticVarName = toStaticVarName(tableName);
            writer.printf("    public static final %s %s = %s.tables.%s.%s;%n", clsName, staticVarName, pkgName, clsName, staticVarName);
            writer.println();
        }
        writer.println("}");
        writer.close();

    }

    private static void emptyDirectory(File dir) {
        for (File f : dir.listFiles()) {
            f.delete();
        }
    }

    public static void generate(Configuration cfg) throws IOException {
        DBInfo dbInfo = fetchDBInfo(cfg.getDBUrl(), cfg.getDBPrefix(), cfg.getAdminAPIKey());

        File dbDir = new File(cfg.getDir());
        File tablesDir = new File(dbDir, "tables");
        File recordsDir = new File(tablesDir, "records");

        if (dbDir.exists()) {
            if (tablesDir.exists()) {
                System.out.printf("Deleting %s/tables/*.java%n", cfg.getDir());
                emptyDirectory(tablesDir);
            } else {
                tablesDir.mkdir();
            }
            if (recordsDir.exists()) {
                System.out.printf("Deleting %s/tables/records/*.java%n", cfg.getDir());
                emptyDirectory(recordsDir);
            } else {
                recordsDir.mkdir();
            }
        } else {
            dbDir.mkdirs();
            /* Create dir/tables, dir/tables/records directories */
            tablesDir.mkdir();
            recordsDir.mkdir();
        }

        List<String> tableNames = new ArrayList<String>();
        for (TableInfo tableInfo : dbInfo.getTables()) {
            tableNames.add(tableInfo.getTableName());
            System.out.println(tableInfo.getTableName());
            generateTable(tablesDir, cfg.getPackageName(), tableInfo);
            generateRecord(recordsDir, cfg.getPackageName(), tableInfo);
        }
        generateTablesJava(cfg.getDir(), cfg.getPackageName(), tableNames);
    }
}
