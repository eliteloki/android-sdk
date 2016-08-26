package io.hasura.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashSet;

import io.hasura.core.Call;
import io.hasura.core.Converter;

public class DeleteQuery<R> extends QueryWithReturning<DeleteQuery<R>, R> {
    private JsonObject whereExp;
    private DBService db;
    private Table<R> table;

    public DeleteQuery(DBService db, Table<R> table) {
        super();
        this.whereExp = null;
        this.table = table;
        this.db = db;
    }

    public DeleteQuery<R> fromRetSet(HashSet<String> retSet) {
        this.retSet = retSet;
        return this;
    }

    public DeleteQuery<R> where(Condition<R> c) {
        this.whereExp = c.getBoolExp();
        return this;
    }

    public Call<DeleteResult<R>, DBException> build() {
        /* Create the query object */
        JsonObject query = new JsonObject();
        if (this.whereExp != null)
            query.add("where", this.whereExp);

        if (this.retSet.size() != 0) {
            JsonArray retArr = new JsonArray();
            for (String retCol : this.retSet)
                retArr.add(new JsonPrimitive(retCol));
            query.add("returning", retArr);
        }

        String opUrl = "/table/" + table.getTableName() + "/delete";
        Converter<DeleteResult<R>, DBException> converter
                = new DBResponseConverter<>(table.getDelResType());
        return db.mkCall(opUrl, db.gson.toJson(query), converter);
    }
}
