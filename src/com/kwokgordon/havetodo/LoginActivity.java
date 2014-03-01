package com.kwokgordon.havetodo;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class LoginActivity extends Activity {
	
	private final static String LOGIN_API_ENDPOINT_URL = HaveTodo.HOST_URL + "/api/sessions";
	
	private SharedPreferences mPreferences;
	
	private String mUserEmail;
	private String mUserPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

	    mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}
	
	
	public void onLoginButtonClick(View view) {
	    EditText userEmailField = (EditText) findViewById(R.id.login_email);
	    EditText userPasswordField = (EditText) findViewById(R.id.login_password);

	    mUserEmail = userEmailField.getText().toString();
	    mUserPassword = userPasswordField.getText().toString();
	    
        LoginTask loginTask = new LoginTask(LoginActivity.this);
        loginTask.setMessageLoading("Logging in...");
        loginTask.execute(LOGIN_API_ENDPOINT_URL);
	}
	
	
	private class LoginTask extends UrlJsonAsyncTask {
		
	    public LoginTask(Context context) {
	        super(context);
	    }

	    @Override
	    protected JSONObject doInBackground(String... urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject holder = new JSONObject();
	        JSONObject userObj = new JSONObject();
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
	                userObj.put("email", mUserEmail);
	                userObj.put("password", mUserPassword);
	                holder.put("user", userObj);
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
	            if (json.getBoolean("success")) {
	                // everything is ok
	                SharedPreferences.Editor editor = mPreferences.edit();
	                // save the returned auth_token into
	                // the SharedPreferences
	                editor.putString("AuthToken", json.getJSONObject("data").getString("auth_token"));
	                editor.commit();

		            Toast.makeText(context, json.getJSONObject("data").getString("auth_token"), Toast.LENGTH_LONG).show();
	                
	                // launch the HomeActivity and close this one
//	                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//	                startActivity(intent);
//	                finish();
	            }
	            Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
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
