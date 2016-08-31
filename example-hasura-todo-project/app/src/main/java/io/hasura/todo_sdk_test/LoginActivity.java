package io.hasura.todo_sdk_test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.hasura.auth.AuthException;
import io.hasura.auth.LoginResponse;
import io.hasura.core.Callback;
import io.hasura.core.Hasura;
import io.hasura.core.LoginCall;
import io.hasura.core.PersistentCookieStore;

public class LoginActivity extends Activity {

	private EditText mUsernameField;
	private EditText mPasswordField;
	private TextView mErrorField;
	SharedPreferences.Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		editor  = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
		mUsernameField = (EditText) findViewById(R.id.login_username);
		mPasswordField = (EditText) findViewById(R.id.login_password);
		mErrorField = (TextView) findViewById(R.id.error_messages);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 *
	 * @param v view passed from xml.
     * Method will make a request to Hasura Login service.
     */
	public void signIn(final View v) {
        v.setEnabled(false);
        String userName = mUsernameField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        Hasura.clearSession(); // clearSession() will clear any previous sessions from mobile
        /**
         * Hasura.getAuth() will get the Authservice for your application.
         * Hasura.getAuth().login() will get the login call available inside the sdk.
         * By default Hasura sdk handles the session for you and maintains a state to check if the user is logged in.
         * loginCall.enqueueOnUIThread() is used to make the request and make UI changes in UI thread.
         * loginCall.enqueue() is used in case you would like to make additional request in success or
         * failure case depending on your scenario
         * @link https://hasura.io/_docs/auth/3.0/basics.html#login
         */
        LoginCall<LoginResponse, AuthException> loginCall = Hasura.getAuth().login(userName, password);
        loginCall.enqueueOnUIThread(new Callback<LoginResponse, AuthException>() {
            @Override
            public void onSuccess(final LoginResponse response) {
                Intent intent = new Intent(LoginActivity.this, TodoActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(final AuthException e) {
                        String errMsg = e.getCode().toString() + " : " + e.getLocalizedMessage();
                        mErrorField.setText(errMsg);
                        v.setEnabled(true);
            }
        });
    }

    /**
     * Method used to show Registration page.
     * @param v view passed from xml.
     */
	public void showRegistration(View v) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}
}
