package com.kwokgordon.havetodo;

import java.io.IOException;
import java.util.ArrayList;
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
import android.database.Cursor;
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

public class TasksCursorAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<String> g;
	private ArrayList<Cursor> c;
	private LayoutInflater inflater;
	private int mCol;
	
	private SharedPreferences mPreferences;
	
//	private String LOG_TAG = "TODO_ADAPTER";
	
	public TasksCursorAdapter(Context context, ArrayList<String> g, ArrayList<Cursor> c) {
		this.context = context;
		this.g = g;
		this.c = c;
		
		inflater = LayoutInflater.from(context);

		mPreferences = context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
	}		

	public boolean changeCursor(ArrayList<Cursor> c) {
		this.c = c;
		notifyDataSetChanged();
		return true;
	}
	
	public boolean changeCursor(ArrayList<String> g, ArrayList<Cursor> c) {
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

    public Object getChild(int groupPosition, int childPosition) {
    	Cursor cursor = c.get(groupPosition);
    	cursor.moveToPosition(childPosition);
    	return cursor;
    }


	public long getChildId(int groupPosition, int childPosition) {
    	Cursor cursor = (Cursor)getChild(groupPosition, childPosition);
    	mCol = cursor.getColumnIndex(HaveTodoDatabase.KEY_ID);
        return cursor.getLong(mCol);
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

        Cursor cursor = (Cursor) getChild(groupPosition, childPosition);
        mCol = cursor.getColumnIndex(HaveTodoDatabase.COLUMN_TASKS_NAME);
        tv.setText(cursor.getString(mCol));
        
        bn.setVisibility(View.GONE);
        
        iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView v_iv = (ImageView)v.findViewById(R.id.imageCheckBox);
				long id = getChildId(groupPosition, childPosition);
				
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
    	return c.get(groupPosition).getCount();
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
