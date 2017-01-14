package com.glimpse.lecretsi;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.conversationLayout);
        TextView userText = new TextView(this);
        TextView assistantText = new TextView(this);
        LinearLayout.LayoutParams convParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setLayoutParams(convParams);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/assistantfont.ttf");

        userText.setTypeface(custom_font);
        assistantText.setTypeface(custom_font);
    }

}
