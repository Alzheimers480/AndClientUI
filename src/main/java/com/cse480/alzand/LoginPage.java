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

public class LoginPage extends Activity implements View.OnClickListener
{
	Button bLogin;
	private TextView output;

	EditText etUsername, etPassword;
	TextView tvRegisterLink, tvForgotPassword;

    //private HttpClient httpclient;
    private HttpURLConnection httpClient;
	static String serverUrl = "http://141.210.25.46/";
	String result="False";
	String result2="0";
	String username="";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		Log.w("alzand", "Begin");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		bLogin = (Button) findViewById(R.id.bLogin);
		output = (TextView) findViewById(R.id.output);
		tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);


		checkValidation();
		etUsername.addTextChangedListener(tWatcher);
		etPassword.addTextChangedListener(tWatcher);

		bLogin.setOnClickListener(this);
		tvRegisterLink.setOnClickListener(this);
		tvForgotPassword.setOnClickListener(this);

		username = etUsername.getText().toString();
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
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			checkValidation();
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bLogin:
				username = etUsername.getText().toString();
				String password = etPassword.getText().toString();

				try
				{
					String urlParameters = "USERNAME=" + username+ "&PASSWORD=" + password;
					URL website = new URL(serverUrl+"auth.php");
					httpClient = (HttpURLConnection) website.openConnection();
					httpClient.setRequestProperty("connection", "close");
					httpClient.setDoOutput(true);
					httpClient.setRequestProperty("Accept-Charset", "UTF-8");
					httpClient.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
					try {
						OutputStream output = httpClient.getOutputStream();
						output.write(urlParameters.getBytes("UTF-8"));
					}
					catch(Exception ex){
						Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
					}

					InputStream response = httpClient.getInputStream();
					//converts InputStream -> String
					String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();

					try {
						result = inputStreamString.substring(inputStreamString.length() - 4, inputStreamString.length());
						if(inputStreamString == "False"){
							output.setText("Incorrect Password");
						}
						else if(inputStreamString.length()!=5){
							output.setText(inputStreamString.substring(0, inputStreamString.length()-6));
						}
						Log.w("alzand", inputStreamString+" auth.php");
						Log.w("alzand", result+" auth.php");
					}catch(Exception ex){
						Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
					}

				}
				catch(Exception ex){
					Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
				}


				try
				{
					String param = "USERNAME=" + username;
					URL url = new URL(serverUrl+"var.php");
					httpClient = (HttpURLConnection) url.openConnection();
					httpClient.setRequestProperty("connection", "close");
					httpClient.setDoOutput(true);
					httpClient.setRequestProperty("Accept-Charset", "UTF-8");
					httpClient.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
					try {
						OutputStream output = httpClient.getOutputStream();
						output.write(param.getBytes("UTF-8"));
					}
					catch(Exception ex){
						Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
					}

					InputStream response = httpClient.getInputStream();
					//converts InputStream -> String
					String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();

					try {
						result2 = inputStreamString.toString();
						Log.w("alzand", result2+" var.php");
					}catch(Exception ex){
						Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
					}

				}
				catch(Exception ex){
					Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
				}

				if(result.equals("True") && result2.equals("1")){
					startActivity(new Intent(this, UserActivity.class).putExtra("USER_UID", username));
				}
				else{
					//finish();
					//startActivity(new Intent(this, LoginPage.class));
				}
				break;
			case R.id.tvRegisterLink:
				startActivity(new Intent(this, CreateUser.class));
				break;
			case R.id.tvForgotPassword:
				startActivity(new Intent(this, ForgotPassword.class));
				break;
		}
	}
}

//byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
		//int postDataLength = postData.length;
