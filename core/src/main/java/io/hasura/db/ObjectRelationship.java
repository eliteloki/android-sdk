package io.hasura.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ObjectRelationship<R1, R2>
        extends QueryWithProjection<ObjectRelationship<R1, R2>, R2>
        implements SelectField<R1> {

    private String columnName;

    public ObjectRelationship(String columnName) {
        super();
        this.columnName = columnName;
    }

    public ObjectRelationship<R1, R2> fromColumns(JsonArray columns) {
        this.columns = columns;
        return this;
    }

    public JsonElement toQCol() {
        JsonObject col = new JsonObject();
        col.add("name", new JsonPrimitive(this.columnName));
        col.add("columns", this.columns);
        return col;
    }

    public Condition<R1> has(Condition<R2> c) {
        JsonObject boolExp = new JsonObject();
        boolExp.add(this.columnName, c.getBoolExp());
        return new Condition<R1>(boolExp);
    }
}
