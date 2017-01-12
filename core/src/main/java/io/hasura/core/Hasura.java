package io.hasura.core;

import android.content.Context;
import android.content.SharedPreferences;

import io.hasura.auth.AuthService;
import io.hasura.db.DBService;
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
    private static String userToken = "";
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder okHttpBuilder;
    private static SharedPreferences cookiePrefs;
    private static Environment mEnvironment;
    private static String hasuraSharedPref = "io.hasura.shared.pref";
    private static String hasuraSharedPrefUserId = "io.hasura.shared.pref.userId";
    private static String hasuraSharedPrefUserToken = "io.hasura.shared.pref.hasuraSharedPrefUserToken";
    private static String hasuraSharedPrefRequestType = "io.hasura.shared.pref.hasuraSharedPrefRequestType";
    private static String hasuraSharedPrefLoginCheck = "io.hasura.shared.pref.hasuraSharedPrefLoginCheck";

    private Hasura() {
    }

    public static Integer getUserId() {
        return cookiePrefs.getInt(hasuraSharedPrefUserId,-1);
    }

    public static void setUserId(Integer userId) {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putInt(hasuraSharedPrefUserId,userId);
        prefsWriter.apply();
    }

    public static void setRequestType(boolean requestType){
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putBoolean(hasuraSharedPrefRequestType,requestType);
        prefsWriter.apply();
    }

    public static void setUserToken(String userToken) {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(hasuraSharedPrefUserToken,userToken);
        prefsWriter.apply();
    }
    public static void setLogin() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putBoolean(hasuraSharedPrefLoginCheck,true);
        prefsWriter.apply();
    }

    public static void clearLogin() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putBoolean(hasuraSharedPrefLoginCheck,false);
        prefsWriter.apply();
    }

    public static boolean isLoggedIn() {
        return cookiePrefs.getBoolean(hasuraSharedPrefLoginCheck,false);
    }
    public static boolean getRequestType() {
        return cookiePrefs.getBoolean(hasuraSharedPrefRequestType,false);
    }

    public static String getUserToken() {
        return cookiePrefs.getString(hasuraSharedPrefUserToken,"");
    }

    public static String getUserRole() {
        return userRole;
    }

    public static void setUserRole(String userRole) {
        Hasura.userRole = userRole;
    }

    public static OkHttpClient.Builder buildOkHttpClientBuilder() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(new HasuraTokenInterceptor());
        if(Environment.DEV == mEnvironment){
            okHttpClientBuilder.addInterceptor(new LoggingInterceptor());
        } else if(Environment.PROD == mEnvironment){

        }
//                .cookieJar(new JavaNetCookieJar(new CookieManager(
//                        new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)))
        return okHttpClientBuilder;
    }

    public static DBService getDB() {
        if(db == null) {
            db = new DBService(sDBUrl, "", getAuth().getClient());
        }
        return db;
    }

    public static DBService getDBAsRole(String userRole) {
            okHttpClient = okHttpBuilder.addInterceptor(new HasuraTokenInterceptor())
                    .build();
            db = new DBService(sDBUrl, "", okHttpClient);
        return db;
    }

    public static void clearSession() {
        setUserToken("");
        clearLogin();
        new PersistentCookieStore(context).removeAll();
    }

    public static void removeAuthService() {
        auth = null;
    }

    public static void removeDBService() {
        db = null;
    }

    public static AuthService getAuth() {
        if(auth == null) {
            auth = new AuthService(sAuthUrl, okHttpClient);
        }
        return auth;
    }

    public static AuthService getAuthAsRole(String userRole) {
            okHttpClient = okHttpBuilder.addInterceptor(new HasuraTokenInterceptor())
                    .build();
            auth = new AuthService(sAuthUrl, okHttpClient);
        return auth;
    }

    public static IEnvironment context(Context context) {
        return new Hasura.Builder(context);
    }

    public interface IEnvironment  {
        IAuthUrl environment(Environment environment);
    }
    public interface IAuthUrl{
        IDBUrl authUrl(String authUrl);
    }
    public interface IDBUrl {
        IBuild dbUrl(String dbUrl);
    }
    public interface IBuild {
        Hasura build();
    }


    private static class Builder implements IEnvironment,IDBUrl,IAuthUrl,IBuild{
        private static Hasura instance = new Hasura();

        public Builder(Context context) {
            instance.context = context;
        }

        public Builder context(Context context) {
            instance.context = context;
            return this;
        }

        @Override
        public IAuthUrl environment(Environment environment) {
            instance.mEnvironment = environment;
            return this;
        }

        @Override
        public IDBUrl authUrl(String authUrl) {
            instance.sAuthUrl = authUrl;
            return this;
        }

        @Override
        public IBuild dbUrl(String dbUrl) {
            instance.sDBUrl = dbUrl;
            return this;
        }

        @Override
        public Hasura build() {
            instance.okHttpBuilder = buildOkHttpClientBuilder();
            instance.okHttpClient = instance.okHttpBuilder.build();
            instance.cookiePrefs = instance.context.getSharedPreferences(hasuraSharedPref, Context.MODE_PRIVATE);
            instance.auth = getAuth();
            instance.db = getDB();
            return instance;
        }

    }

}
