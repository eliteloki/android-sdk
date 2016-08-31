package io.hasura.todo_sdk_test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;

import io.hasura.core.Hasura;

/**
 * Created by loki on 31/08/16.
 */

public class HasuraUtils {
    /**
     * @param context Activity context.
     * Hasura.clearSession() will clear the user token & Loggedin status from the sdk.
     * ReLogin will restart the activity task.
     */
    public static void Relogin(Context context){
        Hasura.clearSession();
        Intent intent = new Intent(context, SplashScreenActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);
    }
}
