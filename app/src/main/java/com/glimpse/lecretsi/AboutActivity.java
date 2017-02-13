package com.glimpse.lecretsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView developerTitle = (TextView)findViewById(R.id.developers_title);
        developerTitle.setText("Les auteurs");
        developerTitle.setTextSize(30);
        developerTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


    }
}
