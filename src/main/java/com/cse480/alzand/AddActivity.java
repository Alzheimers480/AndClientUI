package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.squareup.okhttp.*;
import java.io.*;

public class AddActivity extends Activity implements View.OnClickListener{

    Button bAdd, btnP1, btnP2, btnP3;
    int iv = 0;
    Uri IFP1,IFP2,IFP3;
    EditText etFirstName, etLastName, etAcqID, etRelation, etMessage;
    TextView tvCancel;
    private HttpURLConnection urlConnection, webConnection;
    String result = "False";
    String result1 = "False";
    ImageView IVP1, IVP2, IVP3;
    String username= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	try {
	    Bundle bundle = getIntent().getExtras();
	    username = bundle.getString("USER_UID");
	    Log.w("alzand", username+" is the current username");
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_add);

	    btnP1 = (Button) findViewById(R.id.btnP1);
	    btnP1.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			iv = 1;
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
		    }
		});
	    btnP2 = (Button) findViewById(R.id.btnP2);
	    btnP2.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			iv = 2;
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
		    }
		});
	    btnP3 = (Button) findViewById(R.id.btnP3);
	    btnP3.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			iv = 3;
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
		    }
		});
	    IVP1 = (ImageView) findViewById(R.id.IVP1);
	    IVP2 = (ImageView) findViewById(R.id.IVP2);
	    IVP3 = (ImageView) findViewById(R.id.IVP3);
	    bAdd = (Button) findViewById(R.id.bAdd);
	    etFirstName = (EditText) findViewById(R.id.etFirstName);
	    etLastName = (EditText) findViewById(R.id.etLastName);
	    etAcqID = (EditText) findViewById(R.id.etAcqid);
	    etRelation = (EditText) findViewById(R.id.etRelation);
	    etMessage = (EditText) findViewById(R.id.etMessage);
	    tvCancel = (TextView) findViewById(R.id.tvCancel);

	    checkValidation();
	    // etFirstName.addTextChangedListener(tWatcher);
	    // etLastName.addTextChangedListener(tWatcher);
	    // etAcqID.addTextChangedListener(tWatcher);
	    // etRelation.addTextChangedListener(tWatcher);

	    bAdd.setOnClickListener(this);
	    tvCancel.setOnClickListener(this);
	} catch (Exception e) {
	    Log.w("alzand","oncreate threw error"+e.toString());
	}
    }

    private void checkValidation() {
	try {
	    if (TextUtils.isEmpty(etFirstName.getText())
		|| TextUtils.isEmpty(etLastName.getText())
		|| TextUtils.isEmpty(etAcqID.getText())
		|| TextUtils.isEmpty(etRelation.getText()))
		bAdd.setEnabled(false);
	    else
		bAdd.setEnabled(true);
	} catch (Exception e) {
	    Log.w("alzand","checkValidation threw error"+e.toString());
	}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	try {
	    super.onActivityResult(requestCode, resultCode, data);

	    //	    Log.w("alzand","line 126 "+data.getData().getPath());
	    Bitmap bp = (Bitmap) data.getExtras().get("data");

	    switch(iv) {
	    case 0:
		Log.w("alzand","error");
		break;
	    case 1:

		IVP1.setImageBitmap(bp);
		IFP1 = data.getData();
		break;

	    case 2:

		IVP2.setImageBitmap(bp);
		IFP2 = data.getData();
		break;
	    case 3:

		IVP3.setImageBitmap(bp);
		IFP3 = data.getData();
		break;
	    }
	    checkValidation();
	} catch (Exception e) {
	    Log.w("alzand","onactivityresult threw error"+e.toString());
	}
    }

    @Override
    public void onClick(View v) {
	try {
	    switch (v.getId()) {
	    case R.id.bAdd:
		String fName = etFirstName.getText().toString();
		String lName = etLastName.getText().toString();
		String aqID = etAcqID.getText().toString();
		String relation = etRelation.getText().toString();
		String message = etMessage.getText().toString();


		String urlParameters = "USERNAME=" + aqID + "&FNAME=" + fName + "&LNAME=" + lName;
		URL website = new URL(LoginPage.serverUrl+"newacqu.php");
		urlConnection = (HttpURLConnection) website.openConnection();

		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		OutputStream output = urlConnection.getOutputStream();
		output.write(urlParameters.getBytes("UTF-8"));

		InputStream response = urlConnection.getInputStream();
		//converts InputStream -> String
		String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();
		result = inputStreamString.substring(inputStreamString.length() - 4, inputStreamString.length());
		Log.w("alzand", inputStreamString+" newacqu");
		Log.w("alzand", result);

		// NEW CODE FOR RELATE

		MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
		OkHttpClient client = new OkHttpClient();
		File picture1 = new File("/sdcard/8.bmp");
		Log.w("alzand", "File...::::" + picture1 + " : " + picture1.exists());
		File picture2 = new File("/sdcard/9.bmp");
		Log.w("alzand", "File...::::" + picture2 + " : " + picture2.exists());
		File picture3 = new File("/sdcard/10.bmp");
		Log.w("alzand", "File...::::" + picture3 + " : " + picture3.exists());
		
		RequestBody requestBody = new MultipartBuilder()
		    .type(MultipartBuilder.FORM)
		    .addFormDataPart("USERNAME", username)
		    .addFormDataPart("ACQUNAME", aqID)
		    .addFormDataPart("RELATION", relation)
		    .addFormDataPart("MESSAGE", message)
		    .addFormDataPart("pics[]", "8.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture1))
		    .addFormDataPart("pics[]", "9.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture2))
		    .addFormDataPart("pics[]", "10.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture3))
		    .build();

		Request request = new Request.Builder()
		    .url(LoginPage.serverUrl+"relate.php")
		    .post(requestBody)
		    .build();

		Response realresponse = client.newCall(request).execute();
		if (!realresponse.isSuccessful()) throw new IOException("Unexpected code " + realresponse);
		String resString = realresponse.body().string();
		Log.w("alzand","Response string :"+resString);
		
		// String urlParams = "USERNAME=" + username + "&ACQUNAME=" + aqID + "&RELATION=" + relation + "&MESSAGE=" + message + "&pics[]=" + IFP1 + "&pics[]=" + IFP2 + "&pics[]=" + IFP3;
		// URL web = new URL(LoginPage.serverUrl+"relate.php");
		// webConnection = (HttpURLConnection) web.openConnection();
		// webConnection.setDoOutput(true);
		// webConnection.setRequestProperty("Accept-Charset", "UTF-8");
		// webConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		// output = webConnection.getOutputStream();
		// output.write(urlParams.getBytes("UTF-8"));
		// response = webConnection.getInputStream();
		// //converts InputStream -> String
		// inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();
		// result1 = inputStreamString.substring(inputStreamString.length() - 4, inputStreamString.length());
		// Log.w("alzand", inputStreamString+" relate");
		// Log.w("alzand", result1);
		finish();
		startActivity(new Intent(this, UserActivity.class).putExtra("USER_UID", username));
		break;
	    case R.id.tvCancel:
		finish();
		startActivity(new Intent(this, UserActivity.class).putExtra("USER_UID", username));
		break;
	    }
	} catch (Exception e) {
	    Log.w("alzand","onClick threw error"+e.toString());
	}
    }
}
