package io.hasura.todo_sdk_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.hasura.auth.AuthException;
import io.hasura.auth.GetCredentialsResponse;
import io.hasura.core.Callback;
import io.hasura.core.Hasura;
import io.hasura.todo_sdk_test.LoginActivity;

import static io.hasura.auth.AuthError.INVALID_SESSION;
import static io.hasura.auth.AuthError.UNAUTHORIZED;

public class SplashScreenActivity extends AppCompatActivity {

    /**
     * To learn about Hasaura
     * @link(git-repo) https://github.com/hasura/android-sdk/
     * @link(Hasura) https://hasura.io/docs
     * @link(Auth) https://hasura.io/_docs/auth/3.0/
     * @link(DB) https://hasura.io/_docs/data/0.7/
     * @link(End-points) https://hasura.io/_docs/auth/3.0/swagger-ui/
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 * Hasura.isLoggedIn() - Will give you true if the user is logged in, false if not.
                 * return true then take him to Todo_page
                 * return false then take him to Login_page
                 */
                if(Hasura.isLoggedIn()){
                    startActivity(new Intent(SplashScreenActivity.this, TodoActivity.class));
                }else {
                    startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                }
                finish();
            }
        },1500);
        /**
         * I'm commenting this part to check the user info as
         * I would not like to make a request during app initialization to check user credentials
         * If you would like to uncomment the below line
         */
        // checkInfo();
    }

    private void checkInfo(){
        /**
         * If the user is logged in
         * On Success Get credentials will give the user session id, Hasura ID and Hasura role
         * On Failure SDK will parse the error-code with the following exception
         * INVALID_SESSION,UNAUTHORIZED,BAD_REQUEST,REQUEST_FAILED,INTERNAL_ERROR,UNEXPECTED_CODE,CONNECTION_ERROR
         */
        Hasura.getAuth().getCredentials().enqueueOnUIThread(new Callback<GetCredentialsResponse, AuthException>() {
            @Override
            public void onSuccess(GetCredentialsResponse getCredentialsResponse) {
                Log.i(getClass().getSimpleName(),getCredentialsResponse.getSessionId());
            }

            @Override
            public void onFailure(AuthException e) {
                Log.i(getClass().getSimpleName(), String.valueOf(e.getCode()));
                if(e.getCode().equals(UNAUTHORIZED)){  // UNAUTHORIZED means user is not logged in yet

                }
            }
        });
    }
}
