package com.kwokgordon.havetodo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class TasksActivity extends Activity {
	
	private static final String TASKS_URL = HaveTodo.HOST_URL + "/users/tasks.json";
	
//	private String LOG_TAG = "TASK_ACTIVITY";
	
	private SharedPreferences mPreferences;

	private ExpandableListView listContent;
	private TasksAdapter listAdapter;
	private List<String> groups;
	private List<List<Map<String, String>>> childs;
	
	private RelativeLayout rl;
	
	private String KEY_ID = "id";
	private String KEY_NAME = "name";
	
	private String mTaskName;
	private EditText mETTaskName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks);
		
		rl = (RelativeLayout)findViewById(R.id.add_task_field);
		rl.setVisibility(View.GONE);

		mETTaskName = (EditText)findViewById(R.id.add_task_name);
		mETTaskName.setOnKeyListener(enterKeyListener);
		
		
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		
        listContent = (ExpandableListView)findViewById(android.R.id.list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tasks, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.add_task:
	    		rl.setVisibility(View.VISIBLE);
	    		return true;
	    	case R.id.refresh_tasks:
	    		loadTasksFromAPI(TASKS_URL);
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();

		loadTasksFromAPI(TASKS_URL);
	}
	
	public void onAddTaskButtonClick(View view) {
		mTaskName = mETTaskName.getText().toString();
		
		if(mTaskName.length() == 0) {
			return;
		} else {
			addTasksFromAPI(mTaskName);
		}
	}
	
	OnKeyListener enterKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				switch(keyCode) {
				case 66:
					onAddTaskButtonClick(v);
					return true;
				}
			}

			return false;
		}
	}; // end method
	

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
                childs = new ArrayList<List<Map<String,String>>>();
                
                pull_task("overdue", "Overdue", json, groups, childs);

                pull_task("today", "Today", json, groups, childs);

                pull_task("tomorrow", "Tomorrow", json, groups, childs);

                pull_task("this_week", "This Week", json, groups, childs);

                pull_task("future", "Future", json, groups, childs);

                pull_task("no_duedate", "No DueDate", json, groups, childs);

                pull_task("completed", "Completed", json, groups, childs);

                listAdapter = new TasksAdapter(TasksActivity.this, groups, childs);

                listContent.setAdapter(listAdapter);

                for(int i = 0; i < listAdapter.getGroupCount(); i++)
        			listContent.expandGroup(i);

                listAdapter.changeCursor(groups, childs);
        		
	            } catch (Exception e) {
	            Toast.makeText(context, e.getMessage(),
	                Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	    
	    private void pull_task(String task_type_key, String task_type_name, 
	    		JSONObject json, List<String> groups, List<List<Map<String, String>>> childs) {
            try {
	        	jsonTasks = json.getJSONObject("data").getJSONArray(task_type_key);
	            length = jsonTasks.length();
	            
	            if (length > 0) {
	            	groups.add(task_type_name + " (" + length + ")");
	
	            	List<Map<String,String>> task_list = new ArrayList<Map<String,String>>(length);
	
	                for (int i = 0; i < length; i++) {
	                	Map<String,String> task_map = new HashMap<String,String>();
	                	task_map.put(KEY_ID, jsonTasks.getJSONObject(i).getString(KEY_ID));
	                	task_map.put(KEY_NAME, jsonTasks.getJSONObject(i).getString(KEY_NAME));
	                	task_list.add(task_map);
	                }
	                
	                childs.add(task_list);
	            }
	        } catch (Exception e) {
		        Toast.makeText(context, e.getMessage(),
		            Toast.LENGTH_LONG).show();
		    }
        }
	}

	
	private void addTasksFromAPI(String tasks) {
		AddTask addTask = new AddTask(TasksActivity.this);
		addTask.setMessageLoading("Loading tasks...");
		addTask.execute(TASKS_URL);
	}

	private class AddTask extends UrlJsonAsyncTask {

		public AddTask(Context context) {
	        super(context);
	    }

		@Override
		protected void onPreExecute() {}
		
	    @Override
	    protected JSONObject doInBackground(String... urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject holder = new JSONObject();
	        JSONObject taskObj = new JSONObject();
	        String response = null;
	        JSONObject json = new JSONObject();

	        try {
	            try {
	                // setup the returned values in case
	                // something goes wrong
	                json.put("success", false);
	                json.put("info", "Something went wrong. Retry!");
	                // add the user email and password to
	                // the params
	                taskObj.put("name", mTaskName);
	                holder.put("auth_token", mPreferences.getString("AuthToken", ""));
	                holder.put("task", taskObj);
	                StringEntity se = new StringEntity(holder.toString());
	                post.setEntity(se);

	                // setup the request headers
	                post.setHeader("Accept", "application/json");
	                post.setHeader("Content-Type", "application/json");

	                ResponseHandler<String> responseHandler = new BasicResponseHandler();
	                response = client.execute(post, responseHandler);
	                json = new JSONObject(response);

	            } catch (HttpResponseException e) {
	                e.printStackTrace();
	                Log.e("ClientProtocol", "" + e);
	                json.put("info", "Email and/or password are invalid. Retry!");
	            } catch (IOException e) {
	                e.printStackTrace();
	                Log.e("IO", "" + e);
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	            Log.e("JSON", "" + e);
	        }

	        return json;
	    }

	    @Override
	    protected void onPostExecute(JSONObject json) {
	        try {
//	            if (json.getBoolean("success")) {
	                // everything is ok

//		            Toast.makeText(context, json.getJSONObject("data").getString("auth_token"), Toast.LENGTH_LONG).show();
	                
	                // launch the HomeActivity and close this one
//	                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//	                startActivity(intent);
//	                finish();
//	            }
//	            Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
	            mETTaskName.setText(null);
	        } catch (Exception e) {
	            // something went wrong: show a Toast
	            // with the exception message
	            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }

	}
	
}
