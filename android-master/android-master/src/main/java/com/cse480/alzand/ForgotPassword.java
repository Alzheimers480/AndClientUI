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

public class ForgotPassword extends Activity implements View.OnClickListener{

    Button bContinue;
    EditText etEmail, etNewPassword, etRepeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etNewPassword = (EditText)findViewById(R.id.etNewPassword);
        etRepeatPassword = (EditText)findViewById(R.id.etRepeatPassword);
        bContinue = (Button)findViewById(R.id.bContinue);

        checkValidation();
        etEmail.addTextChangedListener(tWatcher);
        etNewPassword.addTextChangedListener(tWatcher);
        etRepeatPassword.addTextChangedListener(tWatcher);

        bContinue.setOnClickListener(this);
    }

    private void checkValidation() {
        if (TextUtils.isEmpty(etEmail.getText())
                || TextUtils.isEmpty(etNewPassword.getText())
                || TextUtils.isEmpty(etRepeatPassword.getText()))
            bContinue.setEnabled(false);
        else
            bContinue.setEnabled(true);
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
            case R.id.bContinue:
                String email = etEmail.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String repeatPassword = etRepeatPassword.getText().toString();

                startActivity(new Intent(this, UserActivity.class));
                break;
        }
    }
}
