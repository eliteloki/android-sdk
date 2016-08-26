package io.hasura.nonslip53.data.tables.records;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;

public class UserRecord {
    @SerializedName("id")
    public Integer id;

    @SerializedName("username")
    public String username;

    @SerializedName("tasks")
    public ArrayList<TaskRecord> tasks;

}
