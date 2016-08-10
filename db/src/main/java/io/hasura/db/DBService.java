package io.hasura.db;

import io.hasura.core.Call;
import io.hasura.core.Converter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class DBService {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(Date.class, GsonTypeConverters.dateJsonSerializer)
                    .registerTypeAdapter(Date.class, GsonTypeConverters.dateJsonDeserializer)
                    .registerTypeAdapter(Time.class, GsonTypeConverters.timeJsonSerializer)
                    .registerTypeAdapter(Time.class, GsonTypeConverters.timeJsonDeserializer)
                    .registerTypeAdapter(Timestamp.class, GsonTypeConverters.tsJsonSerializer)
                    .registerTypeAdapter(Timestamp.class, GsonTypeConverters.tsJsonDeserializer)
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

    private OkHttpClient client;

    private String dbUrl;
    private String dbPrefix;

    public DBService(String dbUrl, String dbPrefix, OkHttpClient client) {
        this.dbUrl = dbUrl;
        this.dbPrefix = dbPrefix;
        this.client = client;
    }

    public <T, E extends Exception> Call<T, E> mkCall(
            String url, String jsonBody, Converter<T, E> converter) {
        RequestBody reqBody = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(this.dbUrl + this.dbPrefix + url)
                .post(reqBody)
                .build();
        return new Call<>(client.newCall(request), converter);
    }

    public <R> SelectQuery<R> select(Table<R> table) {
        return new SelectQuery<R>(this, table);
    }

    public <R> InsertQuery<R> insert(Table<R> table) {
        return new InsertQuery<R>(this, table);
    }

    public <R> UpdateQuery<R> update(Table<R> table) {
        return new UpdateQuery<R>(this, table);
    }

    public <R> DeleteQuery<R> delete(Table<R> table) {
        return new DeleteQuery<R>(this, table);
    }
}
