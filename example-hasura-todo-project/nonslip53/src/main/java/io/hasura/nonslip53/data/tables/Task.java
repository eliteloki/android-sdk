package io.hasura.nonslip53.data.tables;

import com.google.gson.reflect.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import io.hasura.db.*;
import io.hasura.nonslip53.data.tables.records.*;
public class Task extends Table<TaskRecord> {
    public static final Task TASK = new Task();

    public Task() {
        super("task");
    }

    public Type getInsResType() {
        return new TypeToken<InsertResult<TaskRecord>>() {}.getType();
    }

    public Type getSelResType() {
        return new TypeToken<ArrayList<TaskRecord>>() {}.getType();
    }

    public Type getUpdResType() {
        return new TypeToken<UpdateResult<TaskRecord>>() {}.getType();
    }

    public Type getDelResType() {
        return new TypeToken<DeleteResult<TaskRecord>>() {}.getType();
    }

    public final PGField<TaskRecord, Integer> ID = new PGField<>("id");
    public final PGField<TaskRecord, String> TITLE = new PGField<>("title");
    public final PGField<TaskRecord, String> DESCRIPTION = new PGField<>("description");
    public final PGField<TaskRecord, Boolean> IS_COMPLETED = new PGField<>("is_completed");
    public final PGField<TaskRecord, Integer> USER_ID = new PGField<>("user_id");

    public final ObjectRelationship<TaskRecord, UserRecord> USER = new ObjectRelationship<>("user");
}
