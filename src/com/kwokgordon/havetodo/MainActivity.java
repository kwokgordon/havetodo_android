package com.kwokgordon.havetodo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private SharedPreferences mPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onRegisterButtonClick(View view) {
		startActivity(new Intent(MainActivity.this, RegisterActivity.class));
	}
	
	public void onLoginButtonClick(View view) {
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
	}
	
	public void onLogoutButtonClick(View view) {
        SharedPreferences.Editor editor = mPreferences.edit();
        // save the returned auth_token into
        // the SharedPreferences
        editor.putString("AuthToken", "");
        editor.commit();
		
	}
	
	public void onTasksButtonClick(View view) {
		startActivity(new Intent(MainActivity.this, TasksActivity.class));
	}
	
}
