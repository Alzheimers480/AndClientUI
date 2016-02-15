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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CreateUser extends Activity implements View.OnClickListener{

    Button bRegister;
    EditText etName, etLname, etEmail, etUsername, etPassword, etConfirmPassword;

    private HttpURLConnection httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        etName = (EditText)findViewById(R.id.etName);
        etLname = (EditText)findViewById(R.id.etLname);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
        bRegister = (Button)findViewById(R.id.bRegister);

        checkValidation();
        etName.addTextChangedListener(tWatcher);
        etLname.addTextChangedListener(tWatcher);
        etEmail.addTextChangedListener(tWatcher);
        etUsername.addTextChangedListener(tWatcher);
        etPassword.addTextChangedListener(tWatcher);
        etConfirmPassword.addTextChangedListener(tWatcher);

        bRegister.setOnClickListener(this);
    }

    private void checkValidation(){
        if(TextUtils.isEmpty(etUsername.getText())
                ||TextUtils.isEmpty(etPassword.getText())
                ||TextUtils.isEmpty(etName.getText())
                ||TextUtils.isEmpty(etLname.getText())
                ||TextUtils.isEmpty(etEmail.getText())
                ||TextUtils.isEmpty(etConfirmPassword.getText()))
            bRegister.setEnabled(false);
        else
            bRegister.setEnabled(true);
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
        switch (v.getId()){
            case R.id.bRegister:
                String name = etName.getText().toString();
                String lname = etLname.getText().toString();
                String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                try
                {
                    String urlParameters = "USERNAME=" + username + "&PASSWORD=" + password
                            + "&PASSWORD2=" + confirmPassword + "&FNAME=" + name
                            + "&LNAME=" + lname + "&EMAIL=" + email;
                    URL website = new URL("http://www.secs.oakland.edu/~scnolton/newuser.php");
                    httpClient = (HttpURLConnection) website.openConnection();

                    httpClient.setDoOutput(true);
                    httpClient.setRequestProperty("Accept-Charset", "UTF-8");
                    httpClient.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    try {
                        OutputStream output = httpClient.getOutputStream();
                        output.write(urlParameters.getBytes("UTF-8"));
                    }
                    catch(Exception ex){}

                    //modify the lines dealing with InputStream
                    InputStream response = httpClient.getInputStream();
                    //converts InputStream -> String
                    String inputStreamString = new Scanner(response,"UTF-8").useDelimiter("\\A").next();
                    if(inputStreamString.equals("False")){
                        startActivity(new Intent(this, MainActivity.class));
                    }
                    else{
                        startActivity(new Intent(this, UserActivity.class));
                    }
                }
                catch(Exception ex){}

                startActivity(new Intent(this, UserActivity.class));
                break;
        }
    }
}
