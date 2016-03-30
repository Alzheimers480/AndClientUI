package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.net.*;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.util.*;
import com.squareup.okhttp.*;
import android.os.AsyncTask;

public class LoginPage extends Activity {
    private Button bLogin;
    private EditText etUsername, etPassword;
    private TextView tvRegisterLink, tvForgotPassword, output;
    private OkHttpClient client;
    public static String serverUrl = "http://141.210.25.46";
    private String username, password;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	try {
	    super.onCreate(savedInstanceState);
	    Log.w("alzand", "Begin");
	    client = new OkHttpClient();

	    setContentView(R.layout.main);

	    etUsername = (EditText) findViewById(R.id.etUsername);
	    etPassword = (EditText) findViewById(R.id.etPassword);
	    bLogin = (Button) findViewById(R.id.bLogin);
	    output = (TextView) findViewById(R.id.output);
	    tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
	    tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);

	    username="";
	    password="";
	
	    etUsername.addTextChangedListener(tWatcher);
	    etPassword.addTextChangedListener(tWatcher);
	} catch (Exception e) {
	    Log.w("alzand","on create: "+e.toString());
	}
    }

    private void checkValidation() {
	if (TextUtils.isEmpty(etUsername.getText())
	    || TextUtils.isEmpty(etPassword.getText()))
	    bLogin.setEnabled(false);
	else
	    bLogin.setEnabled(true);
    }

    TextWatcher tWatcher = new TextWatcher() {
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
		checkValidation();
	    }

	    @Override
	    public void afterTextChanged(Editable s) {}
	};

    public void Register(View v) {
	startActivity(new Intent(this, CreateUser.class));
    }

    public void ForgotPassword(View v) {
	startActivity(new Intent(this, ForgotPassword.class));
    }

    public void Login(View v) {
	username = etUsername.getText().toString();
	password = etPassword.getText().toString();
	new authTask().execute(username, password);
    }

    private String auth(String username, String password) {
	RequestBody requestBody = new MultipartBuilder()
	    .type(MultipartBuilder.FORM)
	    .addFormDataPart("USERNAME", username)
	    .addFormDataPart("PASSWORD", password)
	    .build();
	Request request = new Request.Builder()
	    .url(serverUrl+"/auth.php")
	    .post(requestBody)
	    .build();
	try {
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    return response.body().string();
	} catch (Exception e) {Log.w("alzand","auth error: "+e.toString());}
	return "";
    }
    
    private class authTask extends AsyncTask<String,Void,String> {
	@Override
     	protected String doInBackground(String... creds) {
     	    return auth(creds[0], creds[1]);
     	}
	@Override
     	protected void onPostExecute(String result) {
	    Log.w("alzand","result was "+result);
	    if (result.equals("True")) {
		goToMain();
	    } else if(result.equals("user wasn't found False")) {
		output.setText("Invalid username");
	    } else {
		output.setText("Invalid password");
	    }
	}
    }

    protected void goToMain() {
	startActivity(new Intent(this, UserActivity.class).putExtra("USER_UID", username));
    }
}
