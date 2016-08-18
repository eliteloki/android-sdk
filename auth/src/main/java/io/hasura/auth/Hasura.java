package io.hasura.auth;

import android.content.Context;

import java.net.CookieManager;
import java.net.CookiePolicy;

import io.hasura.core.LoggingInterceptor;
import io.hasura.core.PersistentCookieStore;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

/**
 * Created by loki on 18/08/16.
 */

public class Hasura {
    static AuthService authService = null;
    static Context context;
    static String authUrl;
    static void init(Context mContext){
        context = mContext;
        authService = initAuthService(context);
    }

    static AuthService getAuthService(){
        return initAuthService(context);
    }

    static void clearCookies(){
        new PersistentCookieStore(context).removeAll();
    }

    public static void removeAuthService(){
        authService = null;
    }

    public static AuthService initAuthService(Context context) {
        if(authService == null){
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)))
                    .addInterceptor(new LoggingInterceptor())
                    .build();
            authService = new AuthService(authUrl,okHttpClient);
        }
        return authService;
    }

}
