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

public class Picture extends Activity{
    Button b1;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

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
	    Log.w("alzand",response.body().string());
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
