package io.hasura.todo_sdk_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.hasura.auth.AuthException;
import io.hasura.auth.RegisterRequest;
import io.hasura.auth.RegisterResponse;
import io.hasura.core.Callback;
import io.hasura.core.Hasura;
import io.hasura.db.DBException;
import io.hasura.db.InsertQuery;
import io.hasura.nonslip53.data.Tables;
import io.hasura.nonslip53.data.tables.User;
import io.hasura.nonslip53.data.tables.records.UserRecord;

public class RegisterActivity extends Activity {

    private EditText mUsernameField;
    private EditText mPasswordField;
    private TextView mErrorField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameField = (EditText) findViewById(R.id.register_username);
        mPasswordField = (EditText) findViewById(R.id.register_password);
        mErrorField = (TextView) findViewById(R.id.error_messages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    /**
     * Method used to make register call.
     * @param v view passed fro xml.
     * @link https://hasura.io/_docs/auth/3.0/basics.html#registration
     */
    public void register(final View v) {
        if (mUsernameField.getText().length() == 0 || mPasswordField.getText().length() == 0) {
            mErrorField.setText("username/password can't be empty");
            return;
        }
        v.setEnabled(false);

        final String userName = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        mErrorField.setText("");
        Hasura.clearSession();
        // RegisterRequest is the request model for register call.
        RegisterRequest rr = new RegisterRequest();
        rr.setUsername(userName);
        rr.setPassword(password);
        /**
         * Registration On Success an additional db query is being made to add the User is and user name in the USER table.
         */
        Hasura.getAuth().register(rr).enqueue(new Callback<RegisterResponse, AuthException>() {
            @Override
            public void onSuccess(final RegisterResponse registerResponse) {
                /**
                 * Hasura.getDB() will get the DB Service for your application.
                 * Session for DB and Auth are maintained by the SDK.
                 * DB Service supports select,insert,update and delete.
                 * query.build().execute() will build your query and run it in background thread.
                 * @link https://hasura.io/_docs/data/0.7/quickstart.html#inserting-data
                 */
                InsertQuery<UserRecord> query
                        = Hasura.getDB().insert(Tables.USER)
                        .set(Tables.USER.ID, registerResponse.getHasuraId())
                        .set(Tables.USER.USERNAME, userName);
                try {
                    query.build().execute();
                    Intent intent = new Intent(RegisterActivity.this, TodoActivity.class);
                    startActivity(intent);
                    finish();

                } catch (final DBException e) {
                    /**
                     * runOnUiThread() can be used in the event that are using the enqueue() or execute() to make a request.
                     */
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String errMsg = e.getCode().toString() + " : " + e.getLocalizedMessage();
                            mErrorField.setText(errMsg);
                        }
                    });

                }
            }

            @Override
            public void onFailure(final AuthException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String errMsg = e.getCode().toString() + " : " + e.getLocalizedMessage();
                        mErrorField.setText(errMsg);
                        v.setEnabled(true);
                    }
                });
            }
        });
    }

    /**
     * Method will start Login page
     * @param v view passed from xml
     */
    public void showLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
