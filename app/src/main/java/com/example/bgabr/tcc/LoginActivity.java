package com.example.bgabr.tcc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this,MainActivity.class);
        setContentView(R.layout.activity_login);
        final Button  btnlogin = (Button)findViewById(R.id.btnLogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // btnlogin.setBackgroundColor(getResources().getColor(R.color.cinzaazulclaro));
                startActivity(intent);

            }
        });
    }
}
