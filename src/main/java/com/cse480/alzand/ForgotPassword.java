package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import android.util.*;
import com.squareup.okhttp.*;

public class ForgotPassword extends Activity implements View.OnClickListener{

    private EditText username;
    private TextView output;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_forgot_password);
	username = (EditText) findViewById(R.id.username);
	output = (TextView) findViewById(R.id.output);
    }

    @Override
    public void onClick(View v) {}

    public void sendEmail(View view) {
	try {
	    OkHttpClient client = new OkHttpClient();
	    RequestBody requestBody = new MultipartBuilder()
		.type(MultipartBuilder.FORM)
		.addFormDataPart("USERNAME", username.getText().toString())
		.build();
	    Request request = new Request.Builder()
		.url(LoginPage.serverUrl+"passemail.php")
		.post(requestBody)
		.build();
	    Response response = client.newCall(request).execute();
	    String stringResponse = response.body().string();
	    Log.w("alzand","Response string: "+stringResponse+".");
	    if(stringResponse.equals("True")) {
		output.setText("Email Sent!");
	    }
	    else {
		output.setText("Error! Email NOT Sent!");
	    }
	} catch (Exception e) {
	    Log.w("alzand","forgotpassword sendEmail threw error"+e.toString());
	}
    }
    
};
