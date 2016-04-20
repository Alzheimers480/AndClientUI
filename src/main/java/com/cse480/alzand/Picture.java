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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import java.io.File;

import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.*;

import java.io.*;

import android.speech.tts.TextToSpeech;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.*;

public class Picture extends Activity {
    ArrayList<ImageView> iv = new ArrayList();
    ArrayList<Button> modBtns = new ArrayList<Button>();
    TextToSpeech tts;
    String acqName;
    String gender;
    ArrayList<String> ivPath = new ArrayList<String>();
    String relation;
    String message;
    String distance;
    ArrayList<String> acqID = new ArrayList<String>();
    TextView txt;
    String username;
    FaceDetector.Face[] face = new FaceDetector.Face[3];
    FaceDetector.Face[] newFace;
    String rel = "";
    String mes = "";
    SeekBar maxDistance;
    TextView distanceText;
    int distanceValue;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("USER_UID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    Log.w("alzand", "tts init");
                }
            }
        });
        txt = (TextView) findViewById(R.id.infout);
        distanceText = (TextView) findViewById(R.id.distanceText);
        maxDistance = (SeekBar) findViewById(R.id.distanceSlider);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        distanceText.setText("Max Dinstance: " + maxDistance.getProgress());
        distanceValue = 0;
        maxDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                distanceText.setText("Max Dinstance: "+ maxDistance.getProgress());
                distanceValue = maxDistance.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                distanceText.setText("Max Dinstance: "+ maxDistance.getProgress());
                distanceValue = maxDistance.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceText.setText("Max Dinstance: "+ maxDistance.getProgress());
                distanceValue = maxDistance.getProgress();
            }
        });
    }


    public void onRadioButtonClicked(View v) {

        boolean checked = ((RadioButton) v).isChecked();
        switch (v.getId()) {
            case R.id.ryes:
                if (checked)
                    rel = "True";
                break;
            case R.id.rno:
                if (checked)
                    rel = "False";
                break;
            case R.id.myes:
                if (checked)
                    mes = "True";
                break;
            case R.id.mno:
                if (checked)
                    mes = "False";
                break;
        }
    }

    public void takePic(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    public void modAcqu(View v) {
        switch(v.getId()) {
            case R.id.mod1:
                modAcquAPI(username, acqID.get(0), ivPath.get(0));
                modBtns.get(0).setVisibility(View.GONE);
                break;
            case R.id.mod2:
                modAcquAPI(username, acqID.get(1), ivPath.get(1));
                modBtns.get(1).setVisibility(View.GONE);
                break;
            case R.id.mod3:
                modAcquAPI(username, acqID.get(2), ivPath.get(2));
                modBtns.get(2).setVisibility(View.GONE);
                break;
        }
    }

    public void modAcquAPI(String userID, String acquID, String filePath) {
        try {
            MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
            OkHttpClient client = new OkHttpClient();
            File picture = new File(filePath);
            Log.w("alzand", "File...::::" + picture + " : " + picture.exists());

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("USERNAME", username)
                    .addFormDataPart("ACQUNAME", acquID)
                    .addFormDataPart("pics[]", filePath, RequestBody.create(MEDIA_TYPE_PGM, picture))
                    .build();

            Request request = new Request.Builder()
                    .url(LoginPage.serverUrl + "modacqu.php")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resString = response.body().string();
            Log.w("alzand", "Response string :" + resString);
        } catch (Throwable throwme) {

        }
    }

    public void sendPics(View v) {
        String speach = "";
        Log.w("alzand", "ivPath: " + ivPath.size());
        if (ivPath.isEmpty()) {
            speach = "Face not detected";
        } else if (ivPath.size() == 1) {
            speach = "This is " + predictPic(ivPath.get(0));

            if(speach.equals("This is someone you don't know")){
                Log.w("alzand", speach+" 1");
            }
            else{
                Log.w("alzand", speach+" 2");
                modBtns.get(0).setVisibility(View.VISIBLE);
            }
        } else if (ivPath.size() == 2) {
            for (String i : ivPath) {

                if (ivPath.indexOf(i) == 0) {
                    speach = "This is " + predictPic(i);
                    if(speach.equals("This is someone you don't know")){

                    }
                    else{
                        modBtns.get(0).setVisibility(View.VISIBLE);
                    }
                } else {
                    speach = speach + " and " + predictPic(i);
                    if(speach.equals("This is someone you don't know")){

                    }
                    else{
                        modBtns.get(1).setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            for (String i : ivPath) {
                if (ivPath.indexOf(i) == 0) {
                    speach = "This is " + predictPic(i);
                    if(speach == "This is someone you don't know"){

                    }
                    else{
                        modBtns.get(0).setVisibility(View.VISIBLE);
                    }
                } else if (ivPath.indexOf(i) == 1) {
                    speach = speach + "followed by " + predictPic(i);
                    if(speach == "This is someone you don't know"){

                    }
                    else{
                        modBtns.get(1).setVisibility(View.VISIBLE);
                    }
                } else {
                    speach = speach + " and " + predictPic(i);
                    if(speach == "This is someone you don't know"){

                    }
                    else{
                        modBtns.get(2).setVisibility(View.VISIBLE);
                    }
                }
            }

        }
        txt.setText(speach+" Distance = "+distance);
        tts.speak(speach, TextToSpeech.QUEUE_ADD, null);
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
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

    private static ArrayList<int[]> imageHistogram(Bitmap input) {
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];

        for (int i = 0; i < rhistogram.length; i++) rhistogram[i] = 0;
        for (int i = 0; i < ghistogram.length; i++) ghistogram[i] = 0;
        for (int i = 0; i < bhistogram.length; i++) bhistogram[i] = 0;

        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {
                int color = input.getPixel(i, j);
                int red, green, blue;
//                red = new Color(input.getPixel(i, j)).getRed();
//                green = new Color(input.getRGB (i, j)).getGreen();
//                blue = new Color(input.getRGB (i, j)).getBlue();
                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);

                // Increase the values of colors
                rhistogram[red]++;
                ghistogram[green]++;
                bhistogram[blue]++;

            }
        }

        ArrayList<int[]> hist = new ArrayList<int[]>();
        hist.add(rhistogram);
        hist.add(ghistogram);
        hist.add(bhistogram);

        return hist;
    }

    // Get the histogram equalization lookup table for separate R, G, B channels
    private static ArrayList<int[]> histogramEqualizationLUT(Bitmap input) {
        // Get an image histogram - calculated values by R, G, B channels
        ArrayList<int[]> imageHist = imageHistogram(input);

        // Create the lookup table
        ArrayList<int[]> imageLUT = new ArrayList<int[]>();

        // Fill the lookup table
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];

        for (int i = 0; i < rhistogram.length; i++) rhistogram[i] = 0;
        for (int i = 0; i < ghistogram.length; i++) ghistogram[i] = 0;
        for (int i = 0; i < bhistogram.length; i++) bhistogram[i] = 0;

        long sumr = 0;
        long sumg = 0;
        long sumb = 0;

        float scale_factor = (float) (255.0 / (input.getWidth() * input.getHeight()));

        for (int i = 0; i < rhistogram.length; i++) {
            sumr += imageHist.get(0)[i];
            int valr = (int) (sumr * scale_factor);
            if (valr > 255) {
                rhistogram[i] = 255;
            } else rhistogram[i] = valr;

            sumg += imageHist.get(1)[i];
            int valg = (int) (sumg * scale_factor);
            if (valg > 255) {
                ghistogram[i] = 255;
            } else ghistogram[i] = valg;

            sumb += imageHist.get(2)[i];
            int valb = (int) (sumb * scale_factor);
            if (valb > 255) {
                bhistogram[i] = 255;
            } else bhistogram[i] = valb;
        }

        imageLUT.add(rhistogram);
        imageLUT.add(ghistogram);
        imageLUT.add(bhistogram);

        return imageLUT;
    }

    private static Bitmap histogramEqualization(Bitmap original) {
        int red, green, blue, alpha;
        int newPixel = 0;

        // Get the Lookup table for histogram equalization
        ArrayList<int[]> histLUT = histogramEqualizationLUT(original);
        int WIDTH = original.getWidth();
        int HEIGHT = original.getHeight();
        Bitmap histogramEQ = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                int color = original.getPixel(i, j);
                alpha = Color.alpha(color);
                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);

                // Set new pixel values using the histogram lookup table
                red = histLUT.get(0)[red];
                green = histLUT.get(1)[green];
                blue = histLUT.get(2)[blue];

                newPixel = Color.argb(alpha, red, green, blue);

                histogramEQ.setPixel(i, j, newPixel);
            }
        }

        return histogramEQ;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ivPath.clear();
        acqID.clear();
        for (Button b : modBtns) {
            b.setVisibility(View.GONE);
        }
        for (int x = 0; x < iv.size(); x++) {
            if (iv.get(x) != null) {
                iv.get(x).setImageResource(0);
            }
        }
        iv.add((ImageView) findViewById(R.id.imageView));
        modBtns.add((Button) findViewById(R.id.mod1));
        iv.add((ImageView) findViewById(R.id.imageView2));
        modBtns.add((Button) findViewById(R.id.mod2));
        iv.add((ImageView) findViewById(R.id.imageView3));
        modBtns.add((Button) findViewById(R.id.mod3));

        for (int x = 0; x < face.length; x++) {
            face[x] = null;
        }
        int count1 = 0;
        for (FaceDetector.Face i : face) {
            if (i != null) {
                count1++;
            }
        }
        Log.w("alzand", String.valueOf(count1) + " is it clear");
        // TODO Auto-generated method stub
        try {
            super.onActivityResult(requestCode, resultCode, data);


            //	    Log.w("alzand","line 126 "+data.getData().getPath());
            Uri orgUri = data.getData();
            Bitmap bp = convert(BitmapFactory.decodeStream(getContentResolver().openInputStream(orgUri)), Bitmap.Config.RGB_565);
            Log.w("alzand", String.valueOf(bp.getHeight()) + "<-This is the height2 " + String.valueOf(bp.getWidth()) + "<-This is the width2");
            Double bpHeight =(double) bp.getHeight();
            Double bpWidth = (double) bp.getWidth();
            float ratio =  (float) (bpHeight/bpWidth);
            Log.w("alzand", String.valueOf(ratio));
            Log.w("alzand", String.valueOf(bpWidth));
            Bitmap bp2 = Bitmap.createScaledBitmap(bp, 800, Math.round(800*ratio), false);
            Log.w("alzand", String.valueOf(bp.getHeight()) + "<-This is the height"+String.valueOf(bp.getWidth())+"<-This is the width");
            FaceDetector fd = new FaceDetector(bp2.getWidth(), bp2.getHeight(), 3);
            fd.findFaces(bp2, face);
            int count = 0;
            //Log.w("alzand", String.valueOf(test) + " number of faces0");
            for (FaceDetector.Face i : face) {
                if (i != null) {
                    count++;
                }
            }
            int index = 0;
            newFace = new FaceDetector.Face[count];
            for (FaceDetector.Face i : face) {
                if (i != null) {
                    Log.w("alzand", String.valueOf(index) + " Indexes");
                    newFace[index++] = i;
                }
            }
            Log.w("alzand", String.valueOf(newFace.length) + " number of faces");
            for (int i = 0; i < newFace.length; i++) {
                Log.w("alzand", String.valueOf(newFace[i].confidence()) + " face confidence");
                if (newFace[i].confidence() > .51) {
                    Log.w("alzand", "Face detected");
                    float eyeDistance = newFace[i].eyesDistance();
                    Log.w("alzand", String.valueOf(eyeDistance));
                    PointF midPoint1 = new PointF();
                    newFace[i].getMidPoint(midPoint1);
                    Log.w("alzand", String.valueOf(midPoint1.x));
                    int left1 = Math.round(midPoint1.x - (float) (1.0 * eyeDistance));
                    int right1 = Math.round(midPoint1.x + (float) (1.0 * eyeDistance));
                    int top1 = Math.round(midPoint1.y - (float) (.95 * eyeDistance));
                    int bottom1 = Math.round(midPoint1.y + (float) (1.8 * eyeDistance));
                    if (left1 < 0) {
                        left1 = 0;
                    }
                    if (right1 > bp2.getWidth()) {
                        right1 = bp2.getWidth();
                    }
                    if (top1 < 0) {
                        top1 = 0;
                    }
                    if (bottom1 > bp2.getHeight()) {
                        bottom1 = bp2.getHeight();
                    }
                    Double bp2Width = (double) bp2.getWidth();
                    Double bp2Height = (double) bp2.getHeight();
                    float widthRatio = (float) (bpWidth/bp2Width);
                    float heightRatio = (float) (bpHeight/bp2Height);
                    Bitmap colorCropBm = Bitmap.createBitmap(bp, Math.round(left1*widthRatio), Math.round(top1*heightRatio), Math.round((right1 - left1)*widthRatio), Math.round((bottom1 - top1)*heightRatio));
                    Bitmap testPic1 = toGrayscale(colorCropBm);
                    Bitmap testPic2 = histogramEqualization(testPic1);
                    Bitmap testpic3 = Bitmap.createScaledBitmap(testPic2, 400, 500, false);
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = getImageUri(getApplicationContext(), testpic3);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    String finalPath = "";
                    finalPath = getRealPathFromURI(tempUri);
                    Log.w("alzand", finalPath + " filepath");
                    ivPath.add(finalPath);
                    iv.get(i).setImageBitmap(testpic3);
                    //				Map temp = new HashMap();
                    //				temp.put(finalPath, testPic1);
                    //				cropedFaces.add(temp);
                } else {
                    Log.w("alzand", "Face Not detected");
                }

            }
        } catch (Exception e) {
            Log.w("alzand", "onactivityresult threw error" + e.toString());
        }
    }

    protected String predictPic(String filePath) {
        try {
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
                    .url(LoginPage.serverUrl + "predict.php")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resString = response.body().string();
            Log.w("alzand", "Response string :" + resString);

            JSONObject jObject = new JSONObject(resString);
            acqName = jObject.getString("ACQUAINTANCE_FNAME") + " " + jObject.getString("ACQUAINTANCE_LNAME");
            relation = jObject.getString("RELATION");
            message = jObject.getString("DESCRIPTION");
            gender = jObject.getString("GENDER").equals("male") ? "He" : "She";
            distance = jObject.getString("DISTANCE");
            acqID.add(jObject.getString("ACQUAINTANCE_UID"));
        } catch (Exception e) {
            Log.w("alzand", e.toString());
        }
        String speach = "";
        if (acqName == null || Double.parseDouble(distance) > distanceValue) {
            speach = "someone you don't know";
        } else {
            speach = acqName + ". ";
            if (rel.equals("True")) {
                speach = speach + gender + " is your " + relation + ". ";
            }
            if (mes.equals("True")) {
                speach = speach + message;
            }
        }
        return speach;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Picture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cse480.alzand/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Picture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.cse480.alzand/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }
}
