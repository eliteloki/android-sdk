package io.hasura.db.util;

import com.google.gson.annotations.SerializedName;

public enum PGColType {
    @SerializedName("integer")
    PG_INT("Integer"),

    @SerializedName("bigint")
    PG_BIGINT("Long"),

    @SerializedName("serial")
    PG_SERIAL("Integer"),

    @SerializedName("real")
    PG_FLOAT("Float"),

    @SerializedName("float8")
    PG_DOUBLE("Double"),

    @SerializedName("numeric")
    PG_NUMERIC("BigDecimal"),

    @SerializedName("boolean")
    PG_BOOLEAN("Boolean"),

    @SerializedName("varchar")
    PG_VARCHAR("String"),

    @SerializedName("text")
    PG_TEXT("String"),

    @SerializedName("date")
    PG_DATE("Date"),

    @SerializedName("timestamptz")
    PG_TIMESTAMPTZ("Timestamp"),

    @SerializedName("timetz")
    PG_TIMETZ("Time");

    private final String javaType;

    PGColType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaType() {
        return this.javaType;
    }
}
