package io.hasura.db.util;

import com.google.gson.annotations.SerializedName;

public class RelInfo {
    @SerializedName("type")
    private RelType relType;

    @SerializedName("rtable")
    private String remoteTable;
    @SerializedName("name")
    private String relName;

    public RelType getRelType() {
        return this.relType;
    }

    public String getRemoteTable() {
        return this.remoteTable;
    }

    public String getRelName() {
        return this.relName;
    }
}
