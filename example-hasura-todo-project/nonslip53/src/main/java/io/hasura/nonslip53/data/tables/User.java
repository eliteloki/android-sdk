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
public class User extends Table<UserRecord> {
    public static final User USER = new User();

    public User() {
        super("user");
    }

    public Type getInsResType() {
        return new TypeToken<InsertResult<UserRecord>>() {}.getType();
    }

    public Type getSelResType() {
        return new TypeToken<ArrayList<UserRecord>>() {}.getType();
    }

    public Type getUpdResType() {
        return new TypeToken<UpdateResult<UserRecord>>() {}.getType();
    }

    public Type getDelResType() {
        return new TypeToken<DeleteResult<UserRecord>>() {}.getType();
    }

    public final PGField<UserRecord, Integer> ID = new PGField<>("id");
    public final PGField<UserRecord, String> USERNAME = new PGField<>("username");

    public final ArrayRelationship<UserRecord, TaskRecord> TASKS = new ArrayRelationship<>("tasks");
}
