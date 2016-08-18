package io.hasura.db;

final class SortFieldImpl<R> implements SortField<R> {

    private final String columnName;
    private final SortOrder  order;
    private       NullsOrder nullsOrder;

    SortFieldImpl(String columnName, SortOrder order, NullsOrder nullsOrder) {
        this.columnName = columnName;
        this.order = order;
        this.nullsOrder = nullsOrder;
    }

    @Override
    public final String getColumnName() {
        return columnName;
    }

    @Override
    public final SortOrder getOrder() {
        return order;
    }

    @Override
    public final NullsOrder getNullsOrder() {
        return nullsOrder;
    }

    @Override
    public final SortField<R> nullsFirst() {
        nullsOrder = NullsOrder.FIRST;
        return this;
    }

    @Override
    public final SortField<R> nullsLast() {
        nullsOrder = NullsOrder.LAST;
        return this;
    }
}
