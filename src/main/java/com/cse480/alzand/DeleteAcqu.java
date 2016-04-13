package com.cse480.alzand;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class DeleteAcqu extends Activity implements View.OnClickListener{

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
		.addFormDataPart("ACQUID", username.getText().toString())
		.build();
	    Request request = new Request.Builder()
		.url(LoginPage.serverUrl+"deleteacqu.php")
		.post(requestBody)
		.build();
	    Response response = client.newCall(request).execute();
	    String stringResponse = response.body().string();
	    Log.w("alzand","Response string: "+stringResponse+".");
	    if(stringResponse.equals("True")) {
		output.setText("Acquaintance Deleted");
	    }
	    else {
		output.setText("Error!");
	    }//button scheme
	} catch (Exception e) {
	    Log.w("alzand","forgotpassword sendEmail threw error"+e.toString());
	}
    }
    
};
