package com.kwokgordon.havetodo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class TasksActivity extends Activity {
	
	private static final String TASKS_URL = HaveTodo.HOST_URL + "/api/tasks.json";
	
	private String LOG_TAG = "TASK_ACTIVITY";
	
	private SharedPreferences mPreferences;

	private ExpandableListView listContent;
	private TodoAdapter listAdapter;
	private List<String> groups;
	private List<List<Map<String, String>>> childs;
	
	private String KEY_ID = "id";
	private String KEY_NAME = "name";
	
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
                childs = new ArrayList<List<Map<String,String>>>();
                
                Log.d(LOG_TAG, "before pull_task");

                pull_task("overdue", "Overdue", json, groups, childs);

                pull_task("today", "Today", json, groups, childs);

                pull_task("tomorrow", "Tomorrow", json, groups, childs);

                pull_task("this_week", "This Week", json, groups, childs);

                pull_task("future", "Future", json, groups, childs);

                pull_task("no_duedate", "No DueDate", json, groups, childs);

                pull_task("completed", "Completed", json, groups, childs);

                Log.d(LOG_TAG, "after pull_task");
                
                listAdapter = new TodoAdapter(TasksActivity.this, groups, childs);

                Log.d(LOG_TAG, "before setAdapter");
                
                listContent.setAdapter(listAdapter);

                Log.d(LOG_TAG, "after setAdapter");
                
                for(int i = 0; i < listAdapter.getGroupCount(); i++)
        			listContent.expandGroup(i);

                Log.d(LOG_TAG, "after expandGroup");
        		
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

	public class TodoAdapter extends BaseExpandableListAdapter {

		private Context context;
		private List<String> g;
		private List<List<Map<String, String>>> c;
		private LayoutInflater inflater;

		public TodoAdapter(Context context, List<String> g, List<List<Map<String, String>>> c) {
			this.context = context;
			this.g = g;
			this.c = c;
			
			inflater = LayoutInflater.from(context);
		}		
		
	    public String getGroup(int groupPosition) {
	    	return g.get(groupPosition);
	    }
	    
	    public long getGroupId(int groupPosition) {
	    	return (long)groupPosition;
	    }

	    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	        View v = null;
        	v = inflater.inflate(R.layout.layout_expandable_group, parent, false); 

	        TextView tv = (TextView)v.findViewById(R.id.groupText);
	        tv.setText(getGroup(groupPosition));

			return v;
	    }

	    public int getGroupCount() {
	        return g.size();
	    }

	    public Map<String,String> getChild(int groupPosition, int childPosition) {
	    	return c.get(groupPosition).get(childPosition);
	    }

		public long getChildId(int groupPosition, int childPosition) {
	        return (long)childPosition;
//	        return Long.valueOf(c.get(groupPosition).get(childPosition).get(KEY_ID));
	    }
		
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	        View v = null;

            v = inflater.inflate(R.layout.layout_expandable_child, parent, false); 

            TextView tv = (TextView)v.findViewById(R.id.childText);
	        tv.setText(getChild(groupPosition, childPosition).get(KEY_NAME));
//	        tv.setText(c.get(groupPosition).get(childPosition).get(KEY_NAME));
	        
	        return v;
	    }
		
		public int getChildrenCount(int groupPosition) {
	    	return c.get(groupPosition).size();
	    }

	    public boolean hasStableIds() {
			return true;
		}

	    public boolean isChildSelectable(int groupPosition, int childPosition) {
	        return true;
	    } 

	    public void onGroupCollapsed (int groupPosition) {} 
	    public void onGroupExpanded(int groupPosition) {}
	    
	}

}
