package io.hasura.db;

public enum SortOrder {

    /**
     * Ascending sort order
     */
    ASC("asc"),

    /**
     * Descending sort order
     */
    DESC("desc");

    private final String orderType;

    private SortOrder(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }
}
