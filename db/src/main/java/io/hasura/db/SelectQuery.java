package io.hasura.db;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.hasura.core.Call;
import io.hasura.core.Converter;

import java.util.List;

public class SelectQuery<R> extends QueryWithOrder<SelectQuery<R>, R> {

    private JsonObject whereExp;
    private int limit;
    private int offset;
    private DBService db;

    private Table<R> table;

    public SelectQuery(DBService db, Table<R> table) {
        super();
        this.whereExp = null;
        this.limit = -1;
        this.offset = -1;
        this.table = table;
        this.db = db;
    }

    public SelectQuery<R> fromColumns(JsonArray columns) {
        this.columns = columns;
        return this;
    }

    public SelectQuery<R> fromOrderByCols(JsonArray orderByCols) {
        this.orderByCols = orderByCols;
        return this;
    }

    public SelectQuery<R> where(Condition<R> c) {
        this.whereExp = c.getBoolExp();
        return this;
    }

    public SelectQuery<R> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SelectQuery<R> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public Call<List<R>, DBException> build() {
        /* Create the query object */
        JsonObject query = new JsonObject();
        query.add("columns", this.columns);
        if (this.whereExp != null)
            query.add("where", this.whereExp);
        if (this.limit != -1)
            query.add("limit", new JsonPrimitive(this.limit));
        if (this.offset != -1)
            query.add("offset", new JsonPrimitive(this.offset));
        if (this.orderByCols.size() > 0)
            query.add("order_by", this.orderByCols);

        String opUrl = "/table/" + table.getTableName() + "/select";
        Converter<List<R>, DBException> converter
                = new DBResponseConverter<>(table.getSelResType());
        return db.mkCall(opUrl, db.gson.toJson(query), converter);
    }
}
