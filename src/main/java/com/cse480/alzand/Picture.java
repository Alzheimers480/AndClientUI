package com.cse480.alzand;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.*;

public class Picture extends Activity{
    Button b1;
    Button sendButton;
    ImageView iv;
    TextToSpeech tts;
    String resString;
    String acqName;
    String gender;
    String ivPath;
    String relation;
    String message;
    String distance;
    Bitmap bp;
    TextView txt;
    String username;
	ArrayList cropedFaces;
	FaceDetector.Face[] face = new FaceDetector.Face[3];
	FaceDetector.Face[] newFace;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	Bundle bundle = getIntent().getExtras();
	username = bundle.getString("USER_UID");
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
		sendButton = (Button) findViewById(R.id.sendButton);
        iv = (ImageView) findViewById(R.id.imageView);
	txt = (TextView) findViewById(R.id.infout);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				predictPic();
			}
		});
        b1.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
		}
	    });

	try {
	    //bp = BitmapFactory.decodeFile("/sdcard/7.bmp");
	    //iv.setImageBitmap(bp);
	} catch (Exception e) {
	    Log.w("alzand","tried to read file "+e.toString());
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    Log.w("alzand",sw.toString()); // stack trace as a string
	}
	Log.w("alzand"," png block done ");
    }

	public Bitmap toGrayscale(Bitmap bmpOriginal)
	{
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
		Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
		Canvas canvas = new Canvas(convertedBitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return convertedBitmap;
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try {
			super.onActivityResult(requestCode, resultCode, data);


			//	    Log.w("alzand","line 126 "+data.getData().getPath());
			Bitmap bp = convert((Bitmap) data.getExtras().get("data"), Bitmap.Config.RGB_565);
			FaceDetector fd = new FaceDetector(bp.getWidth(), bp.getHeight(), 1);
			fd.findFaces(bp, face);
			int count = 0;
			for (FaceDetector.Face i : face){
				if(i != null){
					count++;
				}
			}
			int index = 0;
			newFace = new FaceDetector.Face[count];
			for (FaceDetector.Face i : face) {
				if (i != null) {
					newFace[index++] = i;
				}
			}
			for(int i = 0; i<newFace.length; i++){
				if(newFace[i].confidence()>.4){
					Log.w("alzand","Face detected");
				}
				else{
					Log.w("alzand","Face Not detected");
				}
				float eyeDistance = newFace[i].eyesDistance();
				Log.w("alzand", String.valueOf(eyeDistance));
				PointF midPoint1=new PointF();
				newFace[i].getMidPoint(midPoint1);
				Log.w("alzand", String.valueOf(midPoint1.x));
				int left1 = Math.round(midPoint1.x - (float)(1.8 * eyeDistance));
				int right1 = Math.round(midPoint1.x + (float)(1.4 * eyeDistance));
				int top1 = Math.round(midPoint1.y - (float)(1.4 * eyeDistance));
				int bottom1 = Math.round(midPoint1.y + (float)(1.8 * eyeDistance));
				Bitmap colorCropBm = Bitmap.createBitmap(bp, left1, top1, right1-left1, bottom1-top1);
				Bitmap testPic1 = toGrayscale(colorCropBm);
				// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
				Uri tempUri = getImageUri(getApplicationContext(), testPic1);

				// CALL THIS METHOD TO GET THE ACTUAL PATH
				String finalPath = getRealPathFromURI(tempUri);
				Log.w("alzand", finalPath + " filepath");

				iv.setImageBitmap(testPic1);
				ivPath = finalPath;
				Map temp = new HashMap();
				temp.put(finalPath, testPic1);
				cropedFaces.add(temp);
			}
			if(face[0].confidence()>.4){
				Log.w("alzand","Face detected");
			}
			else{
				Log.w("alzand","Face Not detected");
			}
			float eyeDistance = face[0].eyesDistance();
			Log.w("alzand", String.valueOf(eyeDistance));
			PointF midPoint1=new PointF();
			face[0].getMidPoint(midPoint1);
			Log.w("alzand", String.valueOf(midPoint1.x));
			int left1 = Math.round(midPoint1.x - (float)(1.8 * eyeDistance));
			int right1 = Math.round(midPoint1.x + (float)(1.4 * eyeDistance));
			int top1 = Math.round(midPoint1.y - (float)(1.4 * eyeDistance));
			int bottom1 = Math.round(midPoint1.y + (float)(1.8 * eyeDistance));
			Bitmap colorCropBm = Bitmap.createBitmap(bp, left1, top1, right1-left1, bottom1-top1);
			Bitmap testPic1 = toGrayscale(colorCropBm);
			// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
			Uri tempUri = getImageUri(getApplicationContext(), testPic1);

			// CALL THIS METHOD TO GET THE ACTUAL PATH
			String finalPath = getRealPathFromURI(tempUri);
			Log.w("alzand", finalPath + " filepath");

			iv.setImageBitmap(testPic1);
			ivPath = finalPath;
		} catch (Exception e) {
			Log.w("alzand","onactivityresult threw error"+e.toString());
		}
	}

    protected void predictPic() {
	try{
	    MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
	    OkHttpClient client = new OkHttpClient();
	    File picture = new File(ivPath);
	    Log.w("alzand", "File...::::" + picture + " : " + picture.exists());

	    RequestBody requestBody = new MultipartBuilder()
		.type(MultipartBuilder.FORM)
		.addFormDataPart("USERNAME", username)
		.addFormDataPart("pic", ivPath, RequestBody.create(MEDIA_TYPE_PGM, picture))
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
	    gender = jObject.getString("GENDER").equals("male") ? "He" : "She";
	    distance = jObject.getString("DISTANCE");
	} catch (Exception e) {
	    Log.w("alzand",e.toString());
	}
	String speach = "This is "+acqName+". "+gender+" is your "+relation+". "+message;

	txt.setText(speach+"The distance is "+distance);
	tts.speak(speach,TextToSpeech.QUEUE_ADD,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
