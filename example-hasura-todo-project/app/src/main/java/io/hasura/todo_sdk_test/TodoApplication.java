package io.hasura.todo_sdk_test;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import io.hasura.core.Hasura;
import io.hasura.core.PersistentCookieStore;

/**
 * Created by loki on 18/08/16.
 */

public class TodoApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getApplicationContext());
        Hasura.init(getApplicationContext(),"https://auth.nonslip53.hasura-app.io","https://data.nonslip53.hasura-app.io/api/1");
    }
}
