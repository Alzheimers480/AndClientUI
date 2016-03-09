package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import org.apache.http.client.*;
//import org.apache.http.*;
import java.net.*;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity implements View.OnClickListener
{
	Button bLogin;
	EditText etUsername, etPassword;
	TextView tvRegisterLink, tvForgotPassword;

    //private HttpClient httpclient;
    private HttpURLConnection httpClient;
	String result="";
	String result2="";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		bLogin = (Button) findViewById(R.id.bLogin);
		tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);

		checkValidation();
		etUsername.addTextChangedListener(tWatcher);
		etPassword.addTextChangedListener(tWatcher);

		bLogin.setOnClickListener(this);
		tvRegisterLink.setOnClickListener(this);
		tvForgotPassword.setOnClickListener(this);

	
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
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();

				try
				{
					String urlParameters = "USERNAME=" + username+ "&PASSWORD=" + password;
					URL website = new URL("http://www.secs.oakland.edu/~scnolton/auth.php");
					httpClient = (HttpURLConnection) website.openConnection();

					httpClient.setDoOutput(true);
					httpClient.setRequestProperty("Accept-Charset", "UTF-8");
					httpClient.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
					try {
						OutputStream output = httpClient.getOutputStream();
						output.write(urlParameters.getBytes("UTF-8"));
					}
					catch(Exception ex){}

					InputStream response = httpClient.getInputStream();
					//converts InputStream -> String
					String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();

					try {
						result = inputStreamString.substring(inputStreamString.length() - 5, inputStreamString.length());
					}catch(Throwable e){}

				}
				catch(Exception ex){}


				try
				{
					String param = "USERNAME=" + username;
					URL url = new URL("http://www.secs.oakland.edu/~scnolton/var.php");
					httpClient = (HttpURLConnection) url.openConnection();

					httpClient.setDoOutput(true);
					httpClient.setRequestProperty("Accept-Charset", "UTF-8");
					httpClient.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
					try {
						OutputStream output = httpClient.getOutputStream();
						output.write(param.getBytes("UTF-8"));
					}
					catch(Exception ex){}

					InputStream response = httpClient.getInputStream();
					//converts InputStream -> String
					String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();

					try {
						result2 = inputStreamString.toString();
					}catch(Throwable e){}

				}
				catch(Exception ex){}

				if(result.equals("False") || result2.equals("0")){
					startActivity(new Intent(this, MainActivity.class));
				}
				else{
					startActivity(new Intent(this, UserActivity.class));
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
