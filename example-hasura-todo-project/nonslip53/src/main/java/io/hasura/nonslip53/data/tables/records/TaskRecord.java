package io.hasura.nonslip53.data.tables.records;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;

public class TaskRecord {
    @SerializedName("id")
    public Integer id;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("is_completed")
    public Boolean isCompleted;

    @SerializedName("user_id")
    public Integer userId;

    @SerializedName("user")
    public UserRecord user;

}
