package io.hasura.core;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Arrays;

import io.hasura.auth.AuthException;
import io.hasura.auth.AuthService;
import io.hasura.auth.SocialLoginRequest;
import io.hasura.auth.SocialLoginResponse;
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
    private static String userToken = "";
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder okHttpBuilder;
    private static SharedPreferences cookiePrefs;
    private static CallbackManager callbackManager;
    private static String hasuraSharedPref = "io.hasura.shared.pref";
    private static String hasuraSharedPrefUserId = "io.hasura.shared.pref.userId";
    private static String hasuraSharedPrefUserToken = "io.hasura.shared.pref.hasuraSharedPrefUserToken";
    public static String FACEBOOK_NAME = "io.hasura.FACEBOOK_NAME";
    public static String TAG = "HASURA";
    public static String FACEBOOK_ACCESS_TOKEN = "io.hasura.FACEBOOK_ACCESS_TOKEN";
    public static void init(Context mContext,String authUrl,String dbUrl) {
        context = mContext;
        okHttpBuilder = buildOkHttpClientBuilder();
        okHttpClient = okHttpBuilder.build();
        cookiePrefs = context.getSharedPreferences(hasuraSharedPref, Context.MODE_PRIVATE);
        sAuthUrl = authUrl;
        sDBUrl = dbUrl;
        auth = getAuth();
        db = getDB();
        FacebookSdk.sdkInitialize(context);
    }

    public static Integer getUserId() {
        return cookiePrefs.getInt(hasuraSharedPrefUserId,-1);
    }

    public static void setUserId(Integer userId) {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putInt(hasuraSharedPrefUserId,userId);
        prefsWriter.commit();
    }

    public static CallbackManager getFacebookCallBackManager(){
        return callbackManager;
    }

    public static void setUserToken(String userToken) {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(hasuraSharedPrefUserToken,userToken);
        prefsWriter.commit();
    }

    public static void setFBToken(String userToken) {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(FACEBOOK_ACCESS_TOKEN,userToken);
        prefsWriter.commit();
    }

    public static String getFBToken() {
        return cookiePrefs.getString(FACEBOOK_ACCESS_TOKEN,"");
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
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
//                .cookieJar(new JavaNetCookieJar(new CookieManager(
//                        new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)))
                .addInterceptor(new HasuraTokenInterceptor())
                .addInterceptor(new LoggingInterceptor());
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

    public static void clearCookies() {
        setUserToken("");
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


    private static void fbLogin(Activity context, final Callback<SocialLoginResponse, AuthException>  socialCall) {
        callbackManager = CallbackManager.Factory.create();
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("email","user_photos","public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getAuth().socialAuth(new SocialLoginRequest("Facebook",
                        loginResult.getAccessToken().getToken())).enqueue(socialCall);
            }

            @Override
            public void onCancel() {
                Log.d(getClass()
                        .getSimpleName(),"On cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(getClass()
                        .getSimpleName(),error.toString());
            }
        });
    }


}
