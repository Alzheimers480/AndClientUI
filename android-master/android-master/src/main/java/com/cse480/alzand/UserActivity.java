package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserActivity extends Activity implements View.OnClickListener{

    Button bLogout;
    TextView tvTakePic, tvRecord, tvAcquaintance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        bLogout = (Button) findViewById(R.id.bLogout);
        tvTakePic = (TextView) findViewById(R.id.tvTakePic);
        tvRecord = (TextView) findViewById(R.id.tvRecord);
        tvAcquaintance = (TextView) findViewById(R.id.tvAcquaintance);

        bLogout.setOnClickListener(this);
        tvAcquaintance.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogout:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.tvTakePic:
                //startActivity(new Intent(this, TakePicture.class)); Josh's cam classname needed
                break;
            case R.id.tvRecord:
                //startActivity(new Intent(this, Record.class)); Josh's cam classname needed
                break;
            case R.id.tvAcquaintance:
                    startActivity(new Intent(this, AddActivity.class));
                break;
        }
    }
}
