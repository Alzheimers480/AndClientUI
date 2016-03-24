package com.cse480.alzand;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.widget.ImageView;

import java.io.File;

import android.util.Log;
import com.squareup.okhttp.*;
import java.io.*;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

import org.json.*;

public class Picture extends Activity{
    Button b1;
    ImageView iv;
    TextToSpeech tts;
    String resString;
    String acqName;
    String relation;
    String message;
    String distance;
    Bitmap bp;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

	tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
		@Override
		public void onInit(int status) {
		    if(status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.US);
			Log.w("alzand","tts init");
		    }
		}
	    });
        b1 = (Button) findViewById(R.id.btnPicture);
        iv = (ImageView) findViewById(R.id.imageView);
	txt = (TextView) findViewById(R.id.infout);
	
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		predictPic();
            }
	});

		try {
	    bp = BitmapFactory.decodeFile("/sdcard/7.bmp");
	    iv.setImageBitmap(bp);
	} catch (Exception e) {
	    Log.w("alzand","tried to read file "+e.toString());
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    Log.w("alzand",sw.toString()); // stack trace as a string
	}
	Log.w("alzand"," png block done ");
    }

    protected void predictPic() {

	try{
	 MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
	 OkHttpClient client = new OkHttpClient();
	 File picture = new File("/sdcard/7.bmp");
	 Log.w("alzand", "File...::::" + picture + " : " + picture.exists());

	 RequestBody requestBody = new MultipartBuilder()
	     .type(MultipartBuilder.FORM)
	     .addFormDataPart("USERNAME", "switch202")
	     .addFormDataPart("pic", "7.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture))
	     .build();

	 Request request = new Request.Builder()
	     .url(LoginPage.serverUrl+"predict.php")
	     .post(requestBody)
	     .build();

	 Response response = client.newCall(request).execute();
	 if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	 resString = response.body().string();

	 
	 
	 Log.w("alzand","Response string :"+resString);
	 JSONObject jObject = new JSONObject(resString);
	 acqName = jObject.getString("ACQUAINTANCE_FNAME")+" "+jObject.getString("ACQUAINTANCE_LNAME");
	 relation = jObject.getString("RELATION");
	 message = jObject.getString("DESCRIPTION");
	 distance = jObject.getString("DISTANCE");
	} catch (Exception e) {
	    Log.w("alzand",e.toString());
	}

	String speach = "This is "+acqName+". They are your "+relation+"."+message;

	txt.setText(speach+"The distance is "+distance);
	tts.speak(speach,TextToSpeech.QUEUE_ADD,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
