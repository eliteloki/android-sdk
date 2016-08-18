package io.hasura.db;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UpdateResult<R> {
    private int affectedRows;

    @SerializedName("returning")
    private ArrayList<R> records;

    public int getCount() {
        return affectedRows;
    }

    public ArrayList<R> getRecords() {
        return records;
    }
}
