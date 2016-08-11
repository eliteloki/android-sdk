package io.hasura.db.util;

import java.util.List;

public class DBInfo {
    private List<TableInfo> tables;

    public DBInfo(List<TableInfo> tables) {
        this.tables = tables;
    }

    public List<TableInfo> getTables() {
        return this.tables;
    }
}
