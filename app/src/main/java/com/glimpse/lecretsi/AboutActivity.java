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
        developerTitle.setText(R.string.about_dev_title);
        developerTitle.setTextSize(30);
        developerTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView developerText = (TextView)findViewById(R.id.developers_text);
        developerText.setText(R.string.about_dev_text);
        developerText.setTextSize(20);
        developerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView testerTitle = (TextView)findViewById(R.id.testers_title);
        testerTitle.setText(R.string.about_testers_title);
        testerTitle.setTextSize(30);
        testerTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView testerText = (TextView)findViewById(R.id.testers_text);
        testerText.setText(R.string.about_testers_text);
        testerText.setTextSize(20);
        testerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    }
}
