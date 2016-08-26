package io.hasura.db;

public interface SortField<R> {

    /**
     * The name of this sort field
     */
    String getColumnName();

    /**
     * Get the underlying sort order of this sort field
     */
    SortOrder getOrder();

    /**
     * Get the underlying nulls order of this sort field
     */
    NullsOrder getNullsOrder();

    /**
     * Add a <code>NULLS FIRST</code> clause to this sort field
     */
    SortField<R> nullsFirst();

    /**
     * Add a <code>NULLS LAST</code> clause to this sort field
     */
    SortField<R> nullsLast();

}
