package io.hasura.db.util;

import com.google.gson.annotations.SerializedName;

public class PGColInfo {
    @SerializedName("type")
    private PGColType colType;
    @SerializedName("name")
    private String colName;

    public PGColInfo(PGColType colType, String colName) {
        this.colType = colType;
        this.colName = colName;
    }

    public PGColType getColType() {
        return this.colType;
    }

    public String getColName() {
        return this.colName;
    }
}
