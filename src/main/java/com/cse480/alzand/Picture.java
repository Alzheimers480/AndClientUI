package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;

import android.os.Bundle;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.io.File;

import android.util.Log;
import com.squareup.okhttp.*;
import java.io.*;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class Picture extends Activity{
    Button b1;
    ImageView iv;
    TextToSpeech tts;
    String resString;
    String acqName;
    String relation;
    String message;

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
			tts.speak("hello david",TextToSpeech.QUEUE_ADD,null);
		    }
		}
	    });
        b1 = (Button) findViewById(R.id.btnPicture);
        iv = (ImageView) findViewById(R.id.imageView);
	
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		predictPic();
            }
	});
    }

    protected void predictPic() {
	Bitmap bp = null;
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	
	try {
	    bp = BitmapFactory.decodeFile("/sdcard/7.bmp");
	    //bp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
	    //File imgOut = new File("/sdcard/myout.jpg");
	    //imgOut.createNewFile();
	    //FileOutputStream fo = new FileOutputStream(imgOut);
	    //fo.write(bytes.toByteArray());
	    //fo.close();
	    iv.setImageBitmap(bp);
	} catch (Exception e) {
	    Log.w("alzand","tried to png "+e.toString());
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    Log.w("alzand",sw.toString()); // stack trace as a string
	}
	Log.w("alzand"," png block done ");

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
	     .url("http://141.210.25.46/predict.php")
	     .post(requestBody)
	     .build();

	 Response response = client.newCall(request).execute();
	 if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	 resString = response.body().string();
	 String delims = "[=>|\n]";
	 String[] tokens = resString.split(delims);
	 for(int i=0;i<tokens.length;i++)
	     Log.w("alzand","token "+i+" = "+tokens[i]);
	 
	 acqName = tokens[4]+tokens[7];
	 relation = tokens[10];
	 message = tokens[13];
	 Log.w("alzand","Name: "+acqName);
	 Log.w("alzand","Relation: "+relation);
	 Log.w("alzand","Message: "+message);
	 
	} catch (Exception e) {
	    Log.w("alzand",e.toString());
	}
	
	tts.speak("This is "+acqName+".",TextToSpeech.QUEUE_ADD,null);
	tts.speak("They are your "+relation+".",TextToSpeech.QUEUE_ADD,null);
	tts.speak(message+".",TextToSpeech.QUEUE_ADD,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
