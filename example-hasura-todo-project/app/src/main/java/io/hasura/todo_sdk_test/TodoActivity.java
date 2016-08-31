package io.hasura.todo_sdk_test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.hasura.auth.AuthError;
import io.hasura.auth.AuthException;
import io.hasura.auth.LogoutResponse;
import io.hasura.core.Callback;
import io.hasura.core.Hasura;
import io.hasura.core.PersistentCookieStore;
import io.hasura.db.DBError;
import io.hasura.db.DBException;
import io.hasura.db.InsertQuery;
import io.hasura.db.InsertResult;
import io.hasura.db.SelectQuery;
import io.hasura.db.UpdateQuery;
import io.hasura.db.UpdateResult;
import io.hasura.nonslip53.data.Tables;
import io.hasura.nonslip53.data.tables.records.TaskRecord;
import io.hasura.nonslip53.data.Tables.*;
import io.hasura.todo_sdk_test.LoginActivity;
import io.hasura.todo_sdk_test.TaskAdapter;

public class TodoActivity extends Activity implements OnItemClickListener {

	private EditText mTaskInput;
	private ListView mListView;
	private TaskAdapter mAdapter;
    Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
		Integer currentUser = Hasura.getUserId();

		if (currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
            mAdapter = new TaskAdapter(this, new ArrayList<TaskRecord>());
            mTaskInput = (EditText) findViewById(R.id.task_input);
            mListView = (ListView) findViewById(R.id.task_list);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);

            updateData();
        }
        findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

	}

    /**
     * UpdateData will fetch the list of columns provided in columns() method and set the data in the Task adapter.
     */
	public void updateData(){
        Log.i(getClass().getSimpleName(),"Todo before"+String.valueOf(new PersistentCookieStore(TodoActivity.this).getCookies()));
        SelectQuery<TaskRecord> q
                = Hasura.getDB().select(Tables.TASK)
                .columns(Tables.TASK.ID, Tables.TASK.DESCRIPTION, Tables.TASK.IS_COMPLETED, Tables.TASK.TITLE);
        q.build().enqueue(new Callback<List<TaskRecord>, DBException>() {
            @Override
            public void onSuccess(final List<TaskRecord> taskRecords) {
                Log.i(getClass().getSimpleName(),"Todo after success"+String.valueOf(new PersistentCookieStore(TodoActivity.this).getCookies()));
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.clear();
                        for (TaskRecord tr : taskRecords) {
                            mAdapter.add(tr);
                        }
                    }
                });
            }

            @Override
            public void onFailure(DBException e) {
                Log.i(getClass().getSimpleName(),"Todo after failure"+String.valueOf(new PersistentCookieStore(TodoActivity.this).getCookies()));
            }
        });
	}

    /**
     * createTask() will add a new task in the user's TASK table.
     * TITLE, DESCRIPTION, IS_COMPLETED, USER_ID are the Task table column name.
     * returning() will return the ID, TITLE, IS_COMPLETED from the TASK table.
     * @link https://hasura.io/_docs/data/0.7/quickstart.html#inserting-data.
     * @param v view passed form xml.
     */
	public void createTask(View v) {
		if (mTaskInput.getText().length() > 0){
            Log.d("user_id_insert", Hasura.getUserId().toString());
            InsertQuery<TaskRecord> query
                    = Hasura.getDB().insert(Tables.TASK)
                    .set(Tables.TASK.TITLE, mTaskInput.getText().toString())
                    .set(Tables.TASK.DESCRIPTION, "")
                    .set(Tables.TASK.IS_COMPLETED, false)
                    .set(Tables.TASK.USER_ID, Hasura.getUserId())
                    .returning(Tables.TASK.ID, Tables.TASK.TITLE, Tables.TASK.IS_COMPLETED);

            query.build().enqueue(new Callback<InsertResult<TaskRecord>, DBException>() {
                @Override
                public void onSuccess(final InsertResult<TaskRecord> taskRecordInsertResult) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.insert(taskRecordInsertResult.getRecords().get(0), 0);
                            mTaskInput.setText("");
                        }
                    });
                }

                @Override
                public void onFailure(DBException e) {
                    if(e.getCode().equals(DBError.INVALID_SESSION)){
                        HasuraUtils.Relogin(TodoActivity.this);
                    }
                }
            });
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_logout:
            logout();
		}
		return false;
	}

    /**
     * logout will make a request to log out a currently logged in user.
     * Redirected to Login page On Success
     */
    private void logout(){
        Hasura.getAuth().logout().enqueue(new Callback<LogoutResponse, AuthException>() {
            @Override
            public void onSuccess(LogoutResponse response) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(TodoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(AuthException e) {
                /**
                 * If the session is invalid then clear the User token and restart the activity task.
                 */
                if(e.getCode().equals(AuthError.INVALID_SESSION)){
                    HasuraUtils.Relogin(TodoActivity.this);
                }
            }
        });
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TaskRecord task = mAdapter.getItem(position);
		TextView taskDescription = (TextView) view.findViewById(R.id.task_description);

		task.isCompleted = !task.isCompleted;

		if (task.isCompleted) {
			taskDescription.setPaintFlags(taskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			taskDescription.setPaintFlags(taskDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}

        /**
         * Will update the task to completed from incomplete or vice versa based on the available data.
         */
        UpdateQuery<TaskRecord> q =
                Hasura.getDB().update(Tables.TASK)
                .set(Tables.TASK.IS_COMPLETED, task.isCompleted)
                .where(Tables.TASK.ID.eq(task.id));

        q.build().enqueue(new Callback<UpdateResult<TaskRecord>, DBException>() {
            @Override
            public void onSuccess(UpdateResult<TaskRecord> taskRecordUpdateResult) {
                // Toast?
            }

            @Override
            public void onFailure(DBException e) {
                if(e.getCode().equals(DBError.INVALID_SESSION)){
                    HasuraUtils.Relogin(TodoActivity.this);
                }
            }
        });
	}

}
