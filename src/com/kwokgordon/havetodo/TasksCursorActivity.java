package com.kwokgordon.havetodo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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

import com.kwokgordon.havetodo.sqlite.model.Tasks;

public class TasksCursorActivity extends Activity {

	private static final String TASKS_URL = HaveTodo.HOST_URL + "/users/tasks.json";
	
	private String LOG_TAG = "TASK_CURSOR_ACTIVITY";
	
	private HaveTodoDatabase db;
	
	private SharedPreferences mPreferences;

	private ExpandableListView listContent;
	private TasksCursorAdapter listAdapter;
	private ArrayList<String> groups;
	private ArrayList<Cursor> childs;
	
	private RelativeLayout rl;
	
	private String mTaskName;
	private EditText mETTaskName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks_cursor);
		
		rl = (RelativeLayout)findViewById(R.id.add_task_field);
		rl.setVisibility(View.GONE);

		mETTaskName = (EditText)findViewById(R.id.add_task_name);
		mETTaskName.setOnKeyListener(enterKeyListener);
		
		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
		
        listContent = (ExpandableListView)findViewById(android.R.id.list);

		db = new HaveTodoDatabase(this);
		db.open_read();
		
        groups = new ArrayList<String>();
        groups.add("Tasks");

        childs = db.getAllTasks();
        
		listAdapter = new TasksCursorAdapter(this, groups, childs);
		
		listContent.setAdapter(listAdapter);
		for(int i = 0; i < groups.size(); i++)
			listContent.expandGroup(i);
        
		db.close();
		
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
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();

    	new GetTasks().execute((Object[]) null);
	}
	
	@Override
	public void onDestroy() {
		db.close();
		
		super.onDestroy();
	}
	
	public void onAddTaskButtonClick(View view) {
		// TODO
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
	

	private class GetTasks extends AsyncTask<Object, Object, ArrayList<Cursor>> {
		
		// perform the database access
		@Override
		protected ArrayList<Cursor> doInBackground(Object... params) {
			
			db.open_read();
			
			// get the cursor containing tasks
			return db.getAllTasks();
		} // end method doInBackground
		
		@Override
		protected void onPostExecute(ArrayList<Cursor> result) {
			listAdapter.changeCursor(result);
			db.close();
		}
	}

}
