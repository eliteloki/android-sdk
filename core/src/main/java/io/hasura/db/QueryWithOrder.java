package io.hasura.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class QueryWithOrder<Q, R> extends QueryWithProjection<Q, R> {
    protected JsonArray orderByCols;

    public QueryWithOrder() {
        super();
        this.orderByCols = new JsonArray();
    }

    public abstract Q fromOrderByCols(JsonArray orderByCols);

    private JsonObject toOrderByElem(SortField<R> f) {
        JsonObject obe = new JsonObject();
        obe.add("column", new JsonPrimitive(f.getColumnName()));
        obe.add("type",  new JsonPrimitive(f.getOrder().getOrderType()));
        obe.add("nulls",  new JsonPrimitive(f.getNullsOrder().getNullsOrderType()));
        return obe;
    }

    public Q orderBy(SortField<R> f1) {
        this.orderByCols.add(toOrderByElem(f1));
        return fromOrderByCols(this.orderByCols);
    }

    public Q orderBy(SortField<R> f1, SortField<R> f2) {
        this.orderByCols.add(toOrderByElem(f1));
        this.orderByCols.add(toOrderByElem(f2));
        return fromOrderByCols(this.orderByCols);
    }

    public Q orderBy(SortField<R> f1, SortField<R> f2, SortField<R> f3) {
        this.orderByCols.add(toOrderByElem(f1));
        this.orderByCols.add(toOrderByElem(f2));
        this.orderByCols.add(toOrderByElem(f3));
        return fromOrderByCols(this.orderByCols);
    }
}
