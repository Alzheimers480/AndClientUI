package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        etFirstName.addTextChangedListener(tWatcher);
        etLastName.addTextChangedListener(tWatcher);
        etAcqID.addTextChangedListener(tWatcher);
        etRelation.addTextChangedListener(tWatcher);

        bAdd.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    private void checkValidation() {
        if (TextUtils.isEmpty(etFirstName.getText())
                || TextUtils.isEmpty(etLastName.getText())
                || TextUtils.isEmpty(etAcqID.getText())
                || TextUtils.isEmpty(etRelation.getText()))
            bAdd.setEnabled(false);
        else
            bAdd.setEnabled(true);
    }

    TextWatcher tWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkValidation();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println(data.getData().getPath());
        Bitmap bp = (Bitmap) data.getExtras().get("data");

        switch(iv) {
            case 0:
                System.out.println("error");
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bAdd:
                String fName = etFirstName.getText().toString();
                String lName = etLastName.getText().toString();
                String aqID = etAcqID.getText().toString();
                String relation = etRelation.getText().toString();
                String message = etMessage.getText().toString();

                MainActivity mainActivity = new MainActivity();
                String username = mainActivity.getUserName();

                try
                {
                    String urlParameters = "USERNAME=" + aqID + "&FNAME=" + fName + "&LNAME=" + lName;
                    URL website = new URL(MainActivity.serverUrl+"newacqu.php");
                    urlConnection = (HttpURLConnection) website.openConnection();

                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    try {
                        OutputStream output = urlConnection.getOutputStream();
                        output.write(urlParameters.getBytes("UTF-8"));
                    }
                    catch(Exception ex){
                        Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
                    }

                    InputStream response = urlConnection.getInputStream();
                    //converts InputStream -> String
                    String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();

                    try {
                        result = inputStreamString.substring(inputStreamString.length() - 5, inputStreamString.length());
                    }catch(Throwable ex){
                        Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
                    }
                }
                catch (Exception ex){
                    Log.w("alzand", ex.toString() + " " + Thread.currentThread().getStackTrace().toString());
                }


                try
                {
                    String urlParams = "USERNAME=" + username + "&ACQUNAME=" + aqID + "&RELATION=" + relation + "&MESSAGE=" + message
                            + "&pics[]=" + IFP1 + "&pics[]=" + IFP2 + "&pics[]=" + IFP3;
                    URL web = new URL(MainActivity.serverUrl+"relate.php");
                    webConnection = (HttpURLConnection) web.openConnection();

                    webConnection.setDoOutput(true);
                    webConnection.setRequestProperty("Accept-Charset", "UTF-8");
                    webConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    try {
                        OutputStream output = webConnection.getOutputStream();
                        output.write(urlParams.getBytes("UTF-8"));
                    }
                    catch(Exception ex){
                        Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
                    }

                    InputStream response = webConnection.getInputStream();
                    //converts InputStream -> String
                    String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();

                    try {
                        result1 = inputStreamString.substring(inputStreamString.length() - 4, inputStreamString.length());
                    }catch(Throwable ex){
                        Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
                    }
                }
                catch (Exception ex){
                    Log.w("alzand", ex.toString()+" "+Thread.currentThread().getStackTrace().toString());
                }


                if(result.equals("False") || result1.equals("False")){
                    startActivity(new Intent(this, AddActivity.class));
                }
                else{
                    startActivity(new Intent(this, UserActivity.class));
                }
                break;
            case R.id.tvCancel:
                startActivity(new Intent(this, UserActivity.class));
                break;
        }
    }
}
