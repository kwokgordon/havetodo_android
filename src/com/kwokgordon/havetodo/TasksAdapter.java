package com.kwokgordon.havetodo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class TasksAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> g;
	private List<List<Map<String, String>>> c;
	private LayoutInflater inflater;

	private SharedPreferences mPreferences;
	
//	private String LOG_TAG = "TODO_ADAPTER";
	
	public TasksAdapter(Context context, List<String> g, List<List<Map<String, String>>> c) {
		this.context = context;
		this.g = g;
		this.c = c;
		
		inflater = LayoutInflater.from(context);

		mPreferences = context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
	}		
	
	public boolean changeCursor(List<String> g, List<List<Map<String, String>>> c) {
		this.g = g;
		this.c = c;
		notifyDataSetChanged();
		return true;
	}
	
	public String getGroup(int groupPosition) {
    	return g.get(groupPosition);
    }
    
    public long getGroupId(int groupPosition) {
    	return (long)groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
    	v = inflater.inflate(R.layout.completed_header, parent, false); 

        TextView tv = (TextView)v.findViewById(R.id.CompletedHeader);
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
        return Long.valueOf(c.get(groupPosition).get(childPosition).get(HaveTodo.KEY_ID));
    }
	
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        
        TextView tv;
        ImageView iv;
        Button bn;
        
        if(getGroup(groupPosition).startsWith("Completed")) {
            v = inflater.inflate(R.layout.completed, parent, false); 
            iv = (ImageView)v.findViewById(R.id.imageCheckBox);
            iv.setTag("check");
        } else {
            v = inflater.inflate(R.layout.incomplete, parent, false); 
            iv = (ImageView)v.findViewById(R.id.imageCheckBox);
            iv.setTag("uncheck");
        }

        tv = (TextView)v.findViewById(R.id.TaskText);
        bn = (Button)v.findViewById(R.id.DeleteButton);

        tv.setText(getChild(groupPosition, childPosition).get(HaveTodo.KEY_NAME));
        bn.setVisibility(View.GONE);
        
        iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView v_iv = (ImageView)v.findViewById(R.id.imageCheckBox);
				long id = getChildId(groupPosition, childPosition);
				toggleTask(Long.toString(id));
				
				if(v_iv.getTag() == "check") {
					v_iv.setTag("uncheck");
					v_iv.setImageResource(R.drawable.ic_uncheck);;
				} else {
					v_iv.setTag("check");
					v_iv.setImageResource(R.drawable.ic_check);;
				}
			}
        });
                
        
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

    

	public void toggleTask(String task_id) { 
		ToggleTaskAPI tt = new ToggleTaskAPI(context);
//		tt.setMessageLoading("Loading tasks...");
		tt.execute(HaveTodo.HOST_URL + "/users/tasks/" + task_id + "/toggleComplete.json");		
	}

	private class ToggleTaskAPI extends UrlJsonAsyncTask {

		public ToggleTaskAPI(Context context) {
			super(context);
		}
		
		@Override
		protected void onPreExecute() {}

	    @Override
	    protected JSONObject doInBackground(String... urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject holder = new JSONObject();
	        String response = null;
	        JSONObject json = new JSONObject();

	        try {
	            try {
	                // setup the returned values in case
	                // something goes wrong
	                json.put("success", false);
	                json.put("info", "Something went wrong. Retry!");

	                holder.put("auth_token", mPreferences.getString("AuthToken", ""));
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
	                
	                // launch the HomeActivity and close this one
//	                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//	                startActivity(intent);
//	                finish();
//	            }
//	            Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
//	            Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
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