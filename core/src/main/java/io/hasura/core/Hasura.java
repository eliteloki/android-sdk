package io.hasura.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.net.CookieManager;
import java.net.CookiePolicy;

import io.hasura.auth.AuthService;
import io.hasura.core.LoggingInterceptor;
import io.hasura.core.PersistentCookieStore;
import io.hasura.db.DBService;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

/**
 * Created by loki on 18/08/16.
 */

public class Hasura {
    private static AuthService auth = null;
    private static DBService db = null;
    private static Context context;
    private static String sAuthUrl,sDBUrl;
    private static String userRole = "";
    private static OkHttpClient okHttpClient;
    private static SharedPreferences cookiePrefs;
    private static String hasuraSharedPref = "io.hasura.shared.pref";
    private static String hasuraSharedPrefUserId = "io.hasura.shared.pref.userId";
    public static void init(Context mContext,String authUrl,String dbUrl) {
        context = mContext;
        cookiePrefs = context.getSharedPreferences(hasuraSharedPref, Context.MODE_PRIVATE);
        auth = getAuth();
        db = getDB();
        sAuthUrl = authUrl;
        sDBUrl = dbUrl;
    }

    public static Integer getUserId() {
        return cookiePrefs.getInt(hasuraSharedPrefUserId,-1);
    }

    public static void setUserId(Integer userId) {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putInt(hasuraSharedPrefUserId,userId);
        prefsWriter.commit();
    }

    public static String getUserRole() {
        return userRole;
    }

    public static void setUserRole(String userRole) {
        Hasura.userRole = userRole;
    }

    public static OkHttpClient.Builder buildOkHttpClientBuilder() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(new CookieManager(
                        new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)))
                .addInterceptor(new LoggingInterceptor());
        return okHttpClientBuilder;
    }

    public static DBService getDB() {
            okHttpClient = buildOkHttpClientBuilder().build();
            db = new DBService(sDBUrl, "", okHttpClient);
        return db;
    }

    public static DBService getDBAsRole(String userRole) {
            okHttpClient = buildOkHttpClientBuilder().addInterceptor(new HasuraTokenInterceptor(userRole))
                    .build();
            db = new DBService(sDBUrl, "", okHttpClient);
        return db;
    }

    public static void clearCookies() {
        new PersistentCookieStore(context).removeAll();
    }

    public static void removeAuthService() {
        auth = null;
    }

    public static void removeDBService() {
        db = null;
    }

    public static AuthService getAuth() {
            okHttpClient = buildOkHttpClientBuilder().build();
            auth = new AuthService(sAuthUrl, okHttpClient);
        return auth;
    }

    public static AuthService getAuthAsRole(String userRole) {
            okHttpClient = buildOkHttpClientBuilder().addInterceptor(new HasuraTokenInterceptor(userRole))
                    .build();
            auth = new AuthService(sAuthUrl, okHttpClient);
        return auth;
    }



}
