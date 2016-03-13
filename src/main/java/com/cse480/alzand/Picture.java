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
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
	});


	try{
	 MediaType MEDIA_TYPE_PGM = MediaType.parse("image/png");
	 OkHttpClient client = new OkHttpClient();
	 File picture = new File("/sdcard/my.png");
	 Log.w("alzand", "File...::::" + picture + " : " + picture.exists());

	 RequestBody requestBody = new MultipartBuilder()
	     .type(MultipartBuilder.FORM)
	     .addFormDataPart("USERNAME", "switch202")
	     .addFormDataPart("pic", "my.png", RequestBody.create(MEDIA_TYPE_PGM, picture))
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
	 /*for(int i=0;i<tokens.length;i++)
	     Log.w("alzand","token "+i+" = "+tokens[i]);
	 */
	 
	 acqName = tokens[4]+tokens[7];
	 relation = tokens[10];
	 message = tokens[13];
	 Log.w("alzand","Name: "+acqName);
	 Log.w("alzand","Relation: "+relation);
	 Log.w("alzand","Message: "+message);
	 
	} catch (Exception e) {
	    Log.w("alzand",e.toString());
	}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        iv.setImageBitmap(bp);

	FileOutputStream out = null;
	try {
	    out = new FileOutputStream("/sdcard/out.png");
	    bp.compress(Bitmap.CompressFormat.PNG, 100, out);
	} catch (Exception e) {
	    Log.w("alzand",e.toString());
	} finally {
	    try {
		if (out != null) {
		    out.close();
		}
	    } catch (IOException e) {
		Log.w("alzand",e.toString());
	    }
	}

	tts.speak(acqName,TextToSpeech.QUEUE_ADD,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
