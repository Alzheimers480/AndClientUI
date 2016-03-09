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
	 String IMGUR_CLIENT_ID = "...";
	 MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
	 OkHttpClient client = new OkHttpClient();

	    RequestBody requestBody = new MultipartBuilder()
		.type(MultipartBuilder.FORM)
		.addPart(
			 Headers.of("Content-Disposition", "form-data; name=\"title\""),
			 RequestBody.create(null, "Square Logo"))
		.addPart(
			 Headers.of("Content-Disposition", "form-data; name=\"image\""),
			 RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png")))
		.build();
	    Request request = new Request.Builder()
		.header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
		.url("https://api.imgur.com/3/image")
		.post(requestBody)
		.build();
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    System.out.println(response.body().string());
	} catch (Exception e) {}}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bp = (Bitmap) data.getExtras().get("data");
        iv.setImageBitmap(bp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
