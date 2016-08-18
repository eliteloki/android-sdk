package io.hasura.db;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InsertResult<R> {
    private int affectedRows;

    @SerializedName("returning")
    private ArrayList<R> record;

    public int getCount() {
        return affectedRows;
    }

    public ArrayList<R> getRecords() {
        return record;
    }
}
