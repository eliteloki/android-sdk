package io.hasura.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ArrayRelationship<R1, R2>
        extends QueryWithOrder<ArrayRelationship<R1, R2>, R2>
        implements SelectField<R1> {

    private String columnName;
    private JsonObject whereExp;
    private int limit;
    private int offset;

    public ArrayRelationship(String columnName) {
        super();
        this.columnName = columnName;
        this.whereExp = null;
        this.limit = -1;
        this.offset = -1;
    }

    public ArrayRelationship<R1, R2> fromColumns(JsonArray columns) {
        this.columns = columns;
        return this;
    }

    public ArrayRelationship<R1, R2> fromOrderByCols(JsonArray orderByCols) {
        this.orderByCols = orderByCols;
        return this;
    }

    public JsonElement toQCol() {
        JsonObject col = new JsonObject();
        col.add("name", new JsonPrimitive(this.columnName));
        col.add("columns", this.columns);
        if (this.whereExp != null)
            col.add("where", this.whereExp);
        if (this.limit != -1)
            col.add("limit", new JsonPrimitive(this.limit));
        if (this.offset != -1)
            col.add("offset", new JsonPrimitive(this.offset));
        if (this.orderByCols.size() > 0)
            col.add("order_by", this.orderByCols);
        return col;
    }

    public ArrayRelationship<R1, R2> where(Condition<R2> c) {
        this.whereExp = c.getBoolExp();
        return this;
    }

    public Condition<R1> hasAnyWith(Condition<R2> c) {
        JsonObject boolExp = new JsonObject();
        boolExp.add(this.columnName, c.getBoolExp());
        return new Condition<R1>(boolExp);
    }

    public ArrayRelationship<R1, R2> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public ArrayRelationship<R1, R2> offset(int offset) {
        this.offset = offset;
        return this;
    }
}
