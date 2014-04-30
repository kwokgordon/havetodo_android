package com.kwokgordon.havetodo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.kwokgordon.havetodo.sqlite.model.Tasks;

public class HaveTodoDatabase {

	// Logcat tag
	private static final String LOG_TAG = "HaveTodoDatabase";
	
	// Database Version
	private static final int DATABASE_VERSION = 1;
	
	// Database Name
	private static final String DATABASE_NAME = "HaveTodoDatabase";
	
	// Date Time format
	private static final String date_format = "yyyy-MM-dd";
	private static final String time_format = "HH:mm:ss.sss";
	private static final String datetime_format = "yyyy-MM-dd HH:mm:ss.sss";

	private SQLiteDatabase database; // database object
	private HaveTodoDatabaseHelper databaseOpenHelper; //database helper

	private Context context;

////////////////////////////////////////////////////////////////////////////////
// Tables
	// Table names
	private static final String TABLE_TASKS = "tasks";
	private static final String TABLE_TASKLISTS = "tasklists";
	
	// Common column names
	public static final String KEY_ID = "_id";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_CREATED_AT = "created_at";
	public static final String COLUMN_UPDATED_AT = "updated_at";
	
	// Tasks table - column name
	public static final String COLUMN_TASKS_NAME = "name";
	public static final String COLUMN_TASKS_NOTE = "note";
	public static final String COLUMN_TASKS_PRIORITY = "priority";
	public static final String COLUMN_TASKS_DUE_DATE = "due_date";
	public static final String COLUMN_TASKS_DUE_TIME = "due_time";
	public static final String COLUMN_TASKS_COMPLETED = "completed";
	public static final String COLUMN_TASKS_COMPLETED_DATE = "completed_date";
	public static final String COLUMN_TASKS_COMPLETED_USER_ID = "completed_user_id";
	public static final String COLUMN_TASKS_COMPLETED_USER_NAME = "completed_user_name";
	
	// Tasklists table - column name
	private static final String COLUMN_TASKLISTS_NAME = "name";
	private static final String COLUMN_TASKLISTS_COLOR = "color";

	
////////////////////////////////////////////////////////////////////////////////
// Create table script
	private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_ID + " INTEGER,"
			+ COLUMN_TASKS_NAME + " TEXT,"
			+ COLUMN_TASKS_NOTE + " TEXT,"
			+ COLUMN_TASKS_PRIORITY + " INTEGER,"
			+ COLUMN_TASKS_DUE_DATE + " TEXT,"
			+ COLUMN_TASKS_DUE_TIME + " TEXT,"
			+ COLUMN_TASKS_COMPLETED + " INTEGER,"
			+ COLUMN_TASKS_COMPLETED_DATE + " TEXT,"
			+ COLUMN_TASKS_COMPLETED_USER_ID + " INTEGER,"
			+ COLUMN_TASKS_COMPLETED_USER_NAME + " TEXT,"
			+ COLUMN_CREATED_AT + " TEXT,"
			+ COLUMN_UPDATED_AT + " TEXT);";
	
	private static final String CREATE_TABLE_TASKLISTS = "CREATE TABLE " + TABLE_TASKLISTS + "("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_ID + " INTEGER,"
			+ COLUMN_TASKLISTS_NAME + " TEXT,"
			+ COLUMN_TASKLISTS_COLOR + " TEXT,"
			+ COLUMN_CREATED_AT + " TEXT,"
			+ COLUMN_UPDATED_AT + " TEXT);";


	// public constructor for TodoDatabase
	public HaveTodoDatabase(Context c) {
		context = c;
	} // end HaveTodoDatabase constructor

	// open the database connection to read
	public void open_read() throws SQLException {
		// create a new DatabaseOpenHelper
		databaseOpenHelper = new HaveTodoDatabaseHelper(context);
		// create or open a database for reading/writing
		database = databaseOpenHelper.getReadableDatabase();
	} // end method open_Read

	// open the database connection to write
	public void open_write() throws SQLException {
		// create a new DatabaseOpenHelper
		databaseOpenHelper = new HaveTodoDatabaseHelper(context);
		// create or open a database for reading/writing
		database = databaseOpenHelper.getWritableDatabase();
	} // end method open_Write
	
	// close the database connection
	public void close() {
		if(database != null && database.isOpen())
			database.close(); // close the database connection
	} // end method close
		
	
////////////////////////////////////////////////////////////////////////////////
// Database Methods
////////////////////////////////////////////////////////////////////////////////
	
	// tasks table
	public void insertTask(Tasks task) {
		ContentValues newTask = new ContentValues();
		newTask.put(COLUMN_ID, task.getId());
		newTask.put(COLUMN_TASKS_NAME, task.getName());
		newTask.put(COLUMN_TASKS_NOTE, task.getNote());
		newTask.put(COLUMN_TASKS_PRIORITY, task.getPriority());
		newTask.put(COLUMN_TASKS_DUE_DATE, task.getDueDate());
		newTask.put(COLUMN_TASKS_DUE_TIME, task.getDueTime());
		newTask.put(COLUMN_TASKS_COMPLETED, task.getCompleted());
		newTask.put(COLUMN_TASKS_COMPLETED_DATE, task.getCompletedDate());
		newTask.put(COLUMN_TASKS_COMPLETED_USER_ID, task.getCompletedUserId());
		newTask.put(COLUMN_TASKS_COMPLETED_USER_NAME, task.getCompletedUserName());
		newTask.put(COLUMN_CREATED_AT, task.getCreatedAt());
		newTask.put(COLUMN_UPDATED_AT, task.getUpdatedAt());

		database.insert(TABLE_TASKS, null, newTask);
		
	} // end method insertTask
	
	// get all tasks
	public ArrayList<Cursor> getAllTasks() {
		ArrayList<Cursor> cursor = new ArrayList<Cursor>();

		SimpleDateFormat dateFormat = new SimpleDateFormat(date_format, Locale.getDefault());
		Calendar temp_date = Calendar.getInstance();
		String date = dateFormat.format(temp_date.getTime());
		
		String[] columns = new String[] {KEY_ID, COLUMN_ID, COLUMN_TASKS_NAME};
		String order_str = COLUMN_TASKS_DUE_DATE + " desc," 
				+ COLUMN_TASKS_DUE_TIME + " desc,"
				+ COLUMN_TASKS_NAME + " asc";
		
		ArrayList<String> where = new ArrayList<String>();

//		cursor.add(database.query(TABLE_TASKS, columns, where.get(i), null, null, null, order_str));
		cursor.add(database.query(TABLE_TASKS, columns, null, null, null, null, null));

		return cursor;
	} // end method getAllTasks
	
	

////////////////////////////////////////////////////////////////////////////////
	
	private class HaveTodoDatabaseHelper extends SQLiteOpenHelper {
		
		public HaveTodoDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		} // end HaveTodoDatabaseHelper constructor

		public HaveTodoDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		} // end HaveTodoDatabaseHelper

		@Override
		public void onCreate(SQLiteDatabase db) {
			//execute the query
			db.execSQL(CREATE_TABLE_TASKS);
			db.execSQL(CREATE_TABLE_TASKLISTS);

		} // end onCreate

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    // on upgrade drop older tables
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKLISTS);
	 
	        // create new tables
	        onCreate(db);
	    } // end onUpgrade
	} // end HaveTodoDatabaseHelper
} // end HaveTodoDatabase
