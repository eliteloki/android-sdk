package io.hasura.db;

import java.lang.reflect.Type;

public abstract class Table<R> {

    private String tableName;

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public abstract Type getInsResType();

    public abstract Type getSelResType();

    public abstract Type getUpdResType();

    public abstract Type getDelResType();
}
