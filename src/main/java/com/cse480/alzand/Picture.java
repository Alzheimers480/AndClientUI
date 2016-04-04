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
import android.widget.RadioButton;
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
    ArrayList<ImageView> iv = new ArrayList();
    TextToSpeech tts;
    String resString;
    String acqName;
    String gender;
    ArrayList<String> ivPath = new ArrayList<String>();
    String relation;
    String message;
    String distance;
    Bitmap bp;
    TextView txt;
    String username;
	FaceDetector.Face[] face = new FaceDetector.Face[3];
	FaceDetector.Face[] newFace;
	String rel = "";
	String mes = "";

    
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
	txt = (TextView) findViewById(R.id.infout);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String speach = "";
				if(ivPath.isEmpty()){
					speach  = "Face not detected";
				}
				if(ivPath.size()==1){
					speach  = "This is "+predictPic(ivPath.get(0));
				}
				if(ivPath.size()==2){
					for(String i : ivPath) {

						if (ivPath.indexOf(i) == 0) {
							speach = "This is "+predictPic(i);
						} else {
							speach = speach + " and " + predictPic(i);
						}
					}
				}
				else{
					for(String i : ivPath){
						if (ivPath.indexOf(i) == 0) {
							speach = "This is "+predictPic(i);
						}
						else if(ivPath.indexOf(i) == 1){
							speach = speach + "followed by " + predictPic(i);
						}
						else {
							speach = speach + " and " + predictPic(i);
						}
					}

				}
				txt.setText(speach);
				tts.speak(speach, TextToSpeech.QUEUE_ADD, null);
			}
		});
        b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
			}
		});
    }

	public void onRadioButtonClicked(View v) {

		boolean checked = ((RadioButton) v).isChecked();

		switch(v.getId()) {
			case R.id.ryes:
				if(checked)
					rel = "True";
				break;
			case R.id.rno:
				if(checked)
					rel = "False";
				break;
			case R.id.myes:
				if(checked)
					mes = "True";
				break;
			case R.id.mno:
				if(checked)
					mes = "False";
				break;
		}
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
        ivPath.clear();
        for(int x = 0;x<iv.size();x++){
            if(iv.get(x)!=null){
                iv.get(x).setImageResource(0);
            }
        }
        iv.add((ImageView) findViewById(R.id.imageView));
        iv.add((ImageView) findViewById(R.id.imageView2));
        iv.add((ImageView) findViewById(R.id.imageView3));
		for(int x=0;x<face.length;x++){
			face[x]=null;
		}
		int count1=0;
		for (FaceDetector.Face i : face){
			if(i != null){
				count1++;
			}
		}
		Log.w("alzand", String.valueOf(count1)+ " is it clear");
		// TODO Auto-generated method stub
		try {
			super.onActivityResult(requestCode, resultCode, data);


			//	    Log.w("alzand","line 126 "+data.getData().getPath());
			Uri orgUri = data.getData();
			Bitmap bp = convert(BitmapFactory.decodeStream(getContentResolver().openInputStream(orgUri)), Bitmap.Config.RGB_565);
			FaceDetector fd = new FaceDetector(bp.getWidth(), bp.getHeight(), 3);
			int test = fd.findFaces(bp, face);
			int count = 0;
			Log.w("alzand", String.valueOf(test)+ " number of faces0");
			for (FaceDetector.Face i : face){
				if(i != null){
					count++;
				}
			}
			int index = 0;
			newFace = new FaceDetector.Face[count];
			for (FaceDetector.Face i : face) {
				if (i != null) {
					Log.w("alzand", String.valueOf(index)+ " Indexes");
					newFace[index++] = i;
				}
			}
			Log.w("alzand", String.valueOf(newFace.length)+ " number of faces");
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
				int bottom1 = Math.round(midPoint1.y + (float) (1.8 * eyeDistance));
                if(left1<0){
                    left1=0;
                }
                if(right1>bp.getWidth()){
                    right1=bp.getWidth();
                }
                if(top1<0){
                    top1=0;
                }
                if(bottom1>bp.getHeight()){
                    bottom1 = bp.getHeight();
                }
				Bitmap colorCropBm = Bitmap.createBitmap(bp, left1, top1, right1-left1, bottom1-top1);
				Bitmap testPic1 = toGrayscale(colorCropBm);
				// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
				Uri tempUri = getImageUri(getApplicationContext(), testPic1);

				// CALL THIS METHOD TO GET THE ACTUAL PATH
				String finalPath = "";
				finalPath = getRealPathFromURI(tempUri);
				Log.w("alzand", finalPath + " filepath");
				ivPath.add(finalPath);
				iv.get(i).setImageBitmap(testPic1);
//				Map temp = new HashMap();
//				temp.put(finalPath, testPic1);
//				cropedFaces.add(temp);
			}
		} catch (Exception e) {
			Log.w("alzand","onactivityresult threw error"+e.toString());
		}
	}

    protected String predictPic(String filePath) {
	try{
	    MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
	    OkHttpClient client = new OkHttpClient();
	    File picture = new File(filePath);
	    Log.w("alzand", "File...::::" + picture + " : " + picture.exists());

	    RequestBody requestBody = new MultipartBuilder()
		.type(MultipartBuilder.FORM)
		.addFormDataPart("USERNAME", username)
		.addFormDataPart("pic", filePath, RequestBody.create(MEDIA_TYPE_PGM, picture))
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
		String speach = "";
		speach =  acqName + ". ";
		if(rel.equals("True")){
			speach = speach + gender + " is your " + relation + ". ";
		}
		if(mes.equals("True")){
			speach = speach + message;
		}
	return speach;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
