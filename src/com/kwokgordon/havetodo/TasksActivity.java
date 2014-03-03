package com.kwokgordon.havetodo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
	
	private SharedPreferences mPreferences;

	private ExpandableListView listContent;
	private TodoAdapter listAdapter;
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

	            	// Overdue Tasks
	            	jsonTasks = json.getJSONObject("data").getJSONArray("overdue");
	                length = jsonTasks.length();
	                
	                if (length > 0) {
	                	groups.add("Overdue (" + length + ")");

	                	List<String> overdue_list = new ArrayList<String>(length);

		                for (int i = 0; i < length; i++) {
		                	overdue_list.add(jsonTasks.getJSONObject(i).getString("name"));
		                }
		                
		                childs.add(overdue_list);
	                }

	            	// Today Tasks
	            	jsonTasks = json.getJSONObject("data").getJSONArray("today");
	                length = jsonTasks.length();
	                
	                if (length > 0) {
	                	groups.add("Today (" + length + ")");

	                	List<String> today_list = new ArrayList<String>(length);

		                for (int i = 0; i < length; i++) {
		                	today_list.add(jsonTasks.getJSONObject(i).getString("name"));
		                }
		                
		                childs.add(today_list);
	                }	                

	            	// Tomorrow Tasks
	            	jsonTasks = json.getJSONObject("data").getJSONArray("tomorrow");
	                length = jsonTasks.length();
	                
	                if (length > 0) {
	                	groups.add("Tomorrow (" + length + ")");

	                	List<String> tomorrow_list = new ArrayList<String>(length);

		                for (int i = 0; i < length; i++) {
		                	tomorrow_list.add(jsonTasks.getJSONObject(i).getString("name"));
		                }
		                
		                childs.add(tomorrow_list);
	                }

	            	// This Week Tasks
	            	jsonTasks = json.getJSONObject("data").getJSONArray("this_week");
	                length = jsonTasks.length();
	                
	                if (length > 0) {
	                	groups.add("This Week (" + length + ")");

	                	List<String> this_week_list = new ArrayList<String>(length);

		                for (int i = 0; i < length; i++) {
		                	this_week_list.add(jsonTasks.getJSONObject(i).getString("name"));
		                }
		                
		                childs.add(this_week_list);
	                }	                

	            	// Future Tasks
	            	jsonTasks = json.getJSONObject("data").getJSONArray("future");
	                length = jsonTasks.length();
	                
	                if (length > 0) {
	                	groups.add("Future (" + length + ")");

	                	List<String> future_list = new ArrayList<String>(length);

		                for (int i = 0; i < length; i++) {
		                	future_list.add(jsonTasks.getJSONObject(i).getString("name"));
		                }
		                
		                childs.add(future_list);
	                }	  	                

	            	// No DueDate Tasks
	            	jsonTasks = json.getJSONObject("data").getJSONArray("no_duedate");
	                length = jsonTasks.length();
	                
	                if (length > 0) {
	                	groups.add("No DueDate (" + length + ")");

	                	List<String> no_duedate_list = new ArrayList<String>(length);

		                for (int i = 0; i < length; i++) {
		                	no_duedate_list.add(jsonTasks.getJSONObject(i).getString("name"));
		                }
		                
		                childs.add(no_duedate_list);
	                }	  	                

	            	// Completed Tasks
	            	jsonTasks = json.getJSONObject("data").getJSONArray("completed");
	                length = jsonTasks.length();
	                
	                if (length > 0) {
	                	groups.add("Completed (" + length + ")");

	                	List<String> completed_list = new ArrayList<String>(length);

		                for (int i = 0; i < length; i++) {
		                	completed_list.add(jsonTasks.getJSONObject(i).getString("name"));
		                }
		                
		                childs.add(completed_list);
	                }	                
	                
	                listAdapter = new TodoAdapter(TasksActivity.this, groups, childs);
	        		listContent.setAdapter(listAdapter);
	        		for(int i = 0; i < groups.size(); i++)
	        			listContent.expandGroup(i);

	        		
	            } catch (Exception e) {
	            Toast.makeText(context, e.getMessage(),
	                Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}	

	public class TodoAdapter extends BaseExpandableListAdapter {

		private Context context;
		private List<String> g;
		private List<List<String>> c;
		private LayoutInflater inflater;

		public TodoAdapter(Context context, List<String> g, List<List<String>> c) {
			this.context = context;
			this.g = g;
			this.c = c;
			
			inflater = LayoutInflater.from(context);
		}		
		
	    public Object getGroup(int groupPosition) {
	    	return g.get(groupPosition);
	    }
	    
	    public long getGroupId(int groupPosition) {
	    	return (long)groupPosition;
	    }

	    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	        View v = null;
        	v = inflater.inflate(R.layout.layout_expandable_group, parent, false); 

	        TextView tv = (TextView)v.findViewById(R.id.groupText);
	        tv.setText((String) getGroup(groupPosition));

			return v;
	    }

	    public int getGroupCount() {
	        return g.size();
	    }

	    public Object getChild(int groupPosition, int childPosition) {
	    	return c.get(groupPosition).get(childPosition);
	    }

		public long getChildId(int groupPosition, int childPosition) {
	        return (long)childPosition;
	    }
		
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	        View v = null;

            v = inflater.inflate(R.layout.layout_expandable_child, parent, false); 

            TextView tv = (TextView)v.findViewById(R.id.childText);
	        tv.setText(c.get(groupPosition).get(childPosition));
	        
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
