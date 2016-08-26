package io.hasura.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;

import io.hasura.core.Call;
import io.hasura.core.Converter;

public class UpdateQuery<R> extends QueryWithReturning<UpdateQuery<R>, R> {
    private JsonObject setObj;
    private JsonObject whereExp;
    private DBService db;
    private Table<R> table;

    public UpdateQuery(DBService db, Table<R> table) {
        super();
        this.setObj = new JsonObject();
        this.whereExp = null;
        this.table = table;
        this.db = db;
    }

    public UpdateQuery<R> fromRetSet(HashSet<String> retSet) {
        this.retSet = retSet;
        return this;
    }

    public <T> UpdateQuery<R> set(PGField<R, T> fld, T val) {
        Type valType = new TypeToken<T>() {}.getType();
        this.setObj.add(fld.getColumnName(), db.gson.toJsonTree(val, valType));
        return this;
    }

    public <T> UpdateQuery<R> setAndReturn(PGField<R, T> fld, T value) {
        this.set(fld, value);
        this.returning(fld);
        return this;
    }

    public UpdateQuery<R> where(Condition<R> c) {
        this.whereExp = c.getBoolExp();
        return this;
    }

    public Call<UpdateResult<R>, DBException> build() {
        /* Create the query object */
        JsonObject query = new JsonObject();
        query.add("$set", this.setObj);
        if (this.whereExp != null)
            query.add("where", this.whereExp);

        if (this.retSet.size() != 0) {
            JsonArray retArr = new JsonArray();
            for (String retCol : this.retSet)
                retArr.add(new JsonPrimitive(retCol));
            query.add("returning", retArr);
        }

        String opUrl = "/table/" + table.getTableName() + "/update";

        Converter<UpdateResult<R>, DBException> converter
                = new DBResponseConverter<>(table.getUpdResType());
        return db.mkCall(opUrl, db.gson.toJson(query), converter);
    }
}
