package io.hasura.db;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class PGField<R, T> implements SelectField<R> {
    private static Gson gson = new Gson();

    private String columnName;

    public PGField(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public JsonElement toQCol() {
        return new JsonPrimitive(this.columnName);
    }

    private Condition<R> op(String opRepr, T val) {
        Type valType = new TypeToken<T>() {
        }.getType();
        JsonObject opExp = new JsonObject();
        opExp.add(opRepr, gson.toJsonTree(val, valType));
        JsonObject colExp = new JsonObject();
        colExp.add(this.columnName, opExp);
        return new Condition<R>(colExp);
    }

    public Condition<R> eq(T val) {
        return this.op("$eq", val);
    }

    public Condition<R> neq(T val) {
        return this.op("$neq", val);
    }

    public Condition<R> gt(T val) {
        return this.op("$gt", val);
    }

    public Condition<R> gte(T val) {
        return this.op("$gte", val);
    }

    public Condition<R> lt(T val) {
        return this.op("$lt", val);
    }

    public Condition<R> lte(T val) {
        return this.op("$lte", val);
    }

    public SortField<R> asc() {
        return new SortFieldImpl<>(this.columnName, SortOrder.ASC, NullsOrder.LAST);
    }

    public SortField<R> desc() {
        return new SortFieldImpl<>(this.columnName, SortOrder.DESC, NullsOrder.FIRST);
    }
}
