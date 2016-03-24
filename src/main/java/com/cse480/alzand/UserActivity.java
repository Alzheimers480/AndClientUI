package com.cse480.alzand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserActivity extends Activity implements View.OnClickListener{

    Button bLogout, b1, bAddAcquaintance;
    String username ="";
    //TextView tvAcquaintance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("USER_UID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        bLogout = (Button) findViewById(R.id.bLogout);
        b1 = (Button) findViewById(R.id.b1);
        bAddAcquaintance = (Button) findViewById(R.id.bAddAcquaintance);
        //tvAcquaintance = (TextView) findViewById(R.id.tvAcquaintance);

        bLogout.setOnClickListener(this);
        b1.setOnClickListener(this);
        bAddAcquaintance.setOnClickListener(this);
        //tvAcquaintance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogout:
                finish();
                startActivity(new Intent(this, LoginPage.class));
                break;
            case R.id.b1:
                finish();
                startActivity(new Intent(this, Picture.class)); //Josh's cam classname needed
                break;
            case R.id.bAddAcquaintance:
                finish();
                startActivity(new Intent(this, AddActivity.class).putExtra("USER_UID", username));
                break;
        }
    }
}
