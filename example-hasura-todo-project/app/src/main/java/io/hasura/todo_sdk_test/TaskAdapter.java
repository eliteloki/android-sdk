package io.hasura.todo_sdk_test;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.hasura.nonslip53.data.tables.records.TaskRecord;


public class TaskAdapter extends ArrayAdapter<TaskRecord> {
	private Context mContext;
	private List<TaskRecord> mTasks;
    /**
     *
     * @param context to get the activity context
     * @param objects to get the TaskRecords
     */
	public TaskAdapter(Context context, List<TaskRecord> objects) {
		super(context, R.layout.task_row_item, objects);
		this.mContext = context;
		this.mTasks = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent){
		if(convertView == null){
			LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
			convertView = mLayoutInflater.inflate(R.layout.task_row_item, null);
		}
		
		TaskRecord task = mTasks.get(position);
		
		TextView descriptionView = (TextView) convertView.findViewById(R.id.task_description);

		descriptionView.setText(task.title); // set title from the taskrecord

        // if the task is completed then strike through
        // else don't strike through
		if(task.isCompleted){
			descriptionView.setPaintFlags(descriptionView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}else{
			descriptionView.setPaintFlags(descriptionView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}
		
		return convertView;
	}

}
