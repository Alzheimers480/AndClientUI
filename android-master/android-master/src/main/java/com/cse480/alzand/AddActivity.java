package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AddActivity extends Activity implements View.OnClickListener{

    Button bAdd;
    EditText etFirstName, etLastName, etRelation, etMessage;
    TextView tvCancel;
    private HttpURLConnection urlConnection;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        bAdd = (Button) findViewById(R.id.bAdd);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etRelation = (EditText) findViewById(R.id.etRelation);
        etMessage = (EditText) findViewById(R.id.etMessage);
        tvCancel = (TextView) findViewById(R.id.tvCancel);

        checkValidation();
        etFirstName.addTextChangedListener(tWatcher);
        etLastName.addTextChangedListener(tWatcher);

        bAdd.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    private void checkValidation() {
        if (TextUtils.isEmpty(etFirstName.getText())
                || TextUtils.isEmpty(etLastName.getText()))
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bAdd:
                String fName = etFirstName.getText().toString();
                String lName = etLastName.getText().toString();
                String relation = etRelation.getText().toString();
                String message = etMessage.getText().toString();
                try
                {
                    String urlParameters = "FNAME=" + fName + "&LNAME=" + lName
                            + "&RELATION=" + relation + "&MESSAGE=" + message;
                    URL website = new URL("http://www.secs.oakland.edu/~scnolton/newacqu.php");
                    urlConnection = (HttpURLConnection) website.openConnection();

                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    try {
                        OutputStream output = urlConnection.getOutputStream();
                        output.write(urlParameters.getBytes("UTF-8"));
                    }
                    catch(Exception ex){}

                    InputStream response = urlConnection.getInputStream();
                    //converts InputStream -> String
                    String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();

                    try {
                        result = inputStreamString.substring(inputStreamString.length() - 5, inputStreamString.length());
                    }catch(Throwable e){}
                }
                catch (Exception e){}

                if(result.equals("False")){
                    startActivity(new Intent(this, AddActivity.class));
                }
                else{
                    //startActivity(new Intent(this, TakePicture.class)); Josh's cam classname needed for 5 pics
                }
                break;
            case R.id.tvCancel:
                startActivity(new Intent(this, UserActivity.class));
                break;
        }
    }
}
