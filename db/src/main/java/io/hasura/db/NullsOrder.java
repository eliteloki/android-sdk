package io.hasura.db;

public enum NullsOrder {

    /**
     * Ascending sort order
     */
    FIRST("first"),

    /**
     * Descending sort order
     */
    LAST("last");

    private final String nullsOrderType;

    private NullsOrder(String nullsOrderType) {
        this.nullsOrderType = nullsOrderType;
    }

    public String getNullsOrderType() {
        return nullsOrderType;
    }
}
