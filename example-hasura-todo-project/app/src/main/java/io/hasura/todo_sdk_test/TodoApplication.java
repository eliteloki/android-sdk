package io.hasura.todo_sdk_test;

import android.app.Application;

import io.hasura.core.Hasura;

/**
 * Created by loki on 18/08/16.
 */

public class TodoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * Hasura.init() will initialize the DB and Auth service for your application.
         * @argument1 Application context
         * @argument2 Auth url from hasura console
         * @argument3 DB url from hasura console
         */
        Hasura.init(getApplicationContext(),"https://auth.nonslip53.hasura-app.io","https://data.nonslip53.hasura-app.io/api/1");
    }
}
