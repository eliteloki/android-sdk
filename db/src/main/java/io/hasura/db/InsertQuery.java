package io.hasura.db;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import io.hasura.core.Call;
import io.hasura.core.Converter;

import java.lang.reflect.Type;
import java.util.HashSet;

public class InsertQuery<R> extends QueryWithReturning<InsertQuery<R>, R> {
    private JsonObject currentObj;
    private JsonArray insObjs;
    private DBService db;
    private Table<R> table;

    public InsertQuery(DBService db, Table<R> table) {
        super();
        this.currentObj = new JsonObject();
        this.insObjs = new JsonArray();
        this.table = table;
        this.db = db;
    }

    public InsertQuery<R> fromRetSet(HashSet<String> retSet) {
        this.retSet = retSet;
        return this;
    }

    public <T> InsertQuery<R> set(PGField<R, T> fld, T val) {
        Type valType = new TypeToken<T>() {}.getType();
        this.currentObj.add(fld.getColumnName(), db.gson.toJsonTree(val, valType));
        return this;
    }

    public <T> InsertQuery<R> newRecord() {
        this.insObjs.add(this.currentObj);
        this.currentObj = new JsonObject();
        return this;
    }

    public Call<InsertResult<R>, DBException> build() {
        /* Create the query object */
        this.insObjs.add(this.currentObj);
        JsonObject query = new JsonObject();
        query.add("objects", this.insObjs);
        JsonArray retArr = new JsonArray();
        for (String retCol : this.retSet)
            retArr.add(new JsonPrimitive(retCol));
        query.add("returning", retArr);

        String opUrl = "/table/" + table.getTableName() + "/insert";

        Converter<InsertResult<R>, DBException> converter
                = new DBResponseConverter<>(table.getInsResType());
        return db.mkCall(opUrl, db.gson.toJson(query), converter);
    }
}
