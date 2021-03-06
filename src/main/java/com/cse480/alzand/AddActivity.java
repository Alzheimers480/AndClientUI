package com.cse480.alzand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.net.Uri;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import com.squareup.okhttp.*;

import java.io.*;

public class AddActivity extends Activity implements View.OnClickListener {

    Button bAdd, btnP1, btnP2, btnP3;
    int iv = 0;
    String IFP1, IFP2, IFP3;
    EditText etFirstName, etLastName, etAcqID, etRelation, etMessage;
    TextView tvCancel;
    String gender;
    private HttpURLConnection urlConnection, webConnection;
    String result = "False";
    String result1 = "False";
    ImageView IVP1, IVP2, IVP3;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Bundle bundle = getIntent().getExtras();
            username = bundle.getString("USER_UID");
            Log.w("alzand", username + " is the current username");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add);

            btnP1 = (Button) findViewById(R.id.btnP1);
            btnP2 = (Button) findViewById(R.id.btnP2);
            btnP3 = (Button) findViewById(R.id.btnP3);
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
            Log.w("alzand", "oncreate threw error" + e.toString());
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.male:
                if (checked)
                    gender = "male";
                break;
            case R.id.female:
                if (checked)
                    gender = "female";
                break;
        }
    }

    private void checkValidation() {
        try {
            if (TextUtils.isEmpty(etFirstName.getText())
                    || TextUtils.isEmpty(etLastName.getText())
                    || TextUtils.isEmpty(etAcqID.getText())
                    || TextUtils.isEmpty(etRelation.getText())
                    || gender.isEmpty())
                bAdd.setEnabled(false);
            else
                bAdd.setEnabled(true);
        } catch (Exception e) {
            Log.w("alzand", "checkValidation threw error" + e.toString());
        }
    }

    private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
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
            Bitmap bp2 = Bitmap.createScaledBitmap(bp, 800, Math.round(800*ratio), false);
            FaceDetector fd = new FaceDetector(bp2.getWidth(), bp2.getHeight(), 1);
            FaceDetector.Face[] face = new FaceDetector.Face[1];

            fd.findFaces(bp2, face);
            if (face[0].confidence() > .51) {
                Log.w("alzand", "Face detected");
                float eyeDistance = face[0].eyesDistance();
                Log.w("alzand", String.valueOf(eyeDistance));
                PointF midPoint1 = new PointF();
                face[0].getMidPoint(midPoint1);
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
                Log.w("alzand", String.valueOf(bp.getWidth()) + " bp width");
                Log.w("alzand", String.valueOf(bp.getHeight()) + " bp Height");
                Log.w("alzand", String.valueOf(left1) + " left");
                Log.w("alzand", String.valueOf(right1) + " right");
                Log.w("alzand", String.valueOf(top1) + " top");
                Log.w("alzand", String.valueOf(bottom1) + " bottom");
                Double bp2Width = (double) bp2.getWidth();
                Double bp2Height = (double) bp2.getHeight();
                float widthRatio = (float) (bpWidth/bp2Width);
                float heightRatio = (float) (bpHeight/bp2Height);
                Bitmap colorCropBm = Bitmap.createBitmap(bp, Math.round(left1*widthRatio), Math.round(top1*heightRatio), Math.round((right1 - left1)*widthRatio), Math.round((bottom1 - top1)*heightRatio));
                Bitmap testPic1 = toGrayscale(colorCropBm);
                Bitmap testPic2 = histogramEqualization(testPic1);
                Bitmap testPic3 = Bitmap.createScaledBitmap(testPic2, 400, 500, false);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), testPic3);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                String finalPath = getRealPathFromURI(tempUri);
                Log.w("alzand", finalPath + " filepath");


                switch (iv) {
                    case 0:
                        Log.w("alzand", "error");
                        break;
                    case 1:

                        IVP1.setImageBitmap(testPic3);
                        IFP1 = finalPath;
                        break;

                    case 2:

                        IVP2.setImageBitmap(testPic3);
                        IFP2 = finalPath;
                        break;
                    case 3:

                        IVP3.setImageBitmap(testPic3);
                        IFP3 = finalPath;
                        break;
                }
            } else {
                Log.w("alzand", "Face Not detected");
            }
            checkValidation();
        } catch (Exception e) {
            Log.w("alzand", "onactivityresult threw error" + e.toString());
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


                    String urlParameters = "USERNAME=" + aqID + "&FNAME=" + fName + "&LNAME=" + lName + "&GENDER=" + gender;
                    URL website = new URL(LoginPage.serverUrl + "newacqu.php");
                    urlConnection = (HttpURLConnection) website.openConnection();

                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                    OutputStream output = urlConnection.getOutputStream();
                    output.write(urlParameters.getBytes("UTF-8"));

                    InputStream response = urlConnection.getInputStream();
                    //converts InputStream -> String
                    String inputStreamString = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
                    result = inputStreamString.substring(inputStreamString.length() - 4, inputStreamString.length());
                    Log.w("alzand", inputStreamString + " newacqu");
                    Log.w("alzand", result);

                    // NEW CODE FOR RELATE

                    MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
                    OkHttpClient client = new OkHttpClient();
                    File picture1 = new File(IFP1);
                    Log.w("alzand", "File...::::" + picture1 + " : " + picture1.exists());
                    File picture2 = new File(IFP2);
                    Log.w("alzand", "File...::::" + picture2 + " : " + picture2.exists());
                    File picture3 = new File(IFP3);
                    Log.w("alzand", "File...::::" + picture3 + " : " + picture3.exists());

                    RequestBody requestBody = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addFormDataPart("USERNAME", username)
                            .addFormDataPart("ACQUNAME", aqID)
                            .addFormDataPart("RELATION", relation)
                            .addFormDataPart("MESSAGE", message)
                            .addFormDataPart("pics[]", IFP1, RequestBody.create(MEDIA_TYPE_PGM, picture1))
                            .addFormDataPart("pics[]", IFP2, RequestBody.create(MEDIA_TYPE_PGM, picture2))
                            .addFormDataPart("pics[]", IFP3, RequestBody.create(MEDIA_TYPE_PGM, picture3))
                            .build();

                    Request request = new Request.Builder()
                            .url(LoginPage.serverUrl + "relate.php")
                            .post(requestBody)
                            .build();

                    Response realresponse = client.newCall(request).execute();
                    if (!realresponse.isSuccessful())
                        throw new IOException("Unexpected code " + realresponse);
                    String resString = realresponse.body().string();
                    Log.w("alzand", "Response string :" + resString);
                    finish();
                    startActivity(new Intent(this, UserActivity.class).putExtra("USER_UID", username));
                    break;
                case R.id.tvCancel:
                    finish();
                    startActivity(new Intent(this, UserActivity.class).putExtra("USER_UID", username));
                    break;
            }
        } catch (Exception e) {
            Log.w("alzand", "onClick threw error" + e.toString());
        }
    }

    public void takePicture(View v) {
        switch (v.getId()) {
            case R.id.btnP1:
                iv = 1;
                break;
            case R.id.btnP2:
                iv = 2;
                break;
            case R.id.btnP3:
                iv = 3;
                break;
        }
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }
}
