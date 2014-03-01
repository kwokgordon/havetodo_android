package com.kwokgordon.havetodo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class TasksActivity extends Activity {
	
	private static final String TASKS_URL = HaveTodo.HOST_URL + "/api/tasks.json";
	
	private SharedPreferences mPreferences;

	private ExpandableListView listContent;
	private List<String> groups;
	private List<List<String>> childs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks);
		
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        listContent = (ExpandableListView)findViewById(android.R.id.list);
        
		loadTasksFromAPI(TASKS_URL);
	}

	private void loadTasksFromAPI(String url) {
	    GetTasksTask getTasksTask = new GetTasksTask(TasksActivity.this);
	    getTasksTask.setMessageLoading("Loading tasks...");
	    getTasksTask.execute(url + "?auth_token=" + mPreferences.getString("AuthToken", ""));
	}

	private class GetTasksTask extends UrlJsonAsyncTask {

		JSONArray jsonTasks;
		int length;
		
		public GetTasksTask(Context context) {
	        super(context);
	    }

	    @Override
	        protected void onPostExecute(JSONObject json) {
	            try {
	            	
	                groups = new ArrayList<String>();
	                childs = new ArrayList<List<String>>();
	                
               
	            } catch (Exception e) {
	            Toast.makeText(context, e.getMessage(),
	                Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}	
}
