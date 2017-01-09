package com.glimpse.lecretsi;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
//import com.glimpse.lecretsi.Largonji;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView userText = (TextView)findViewById(R.id.userText);
        TextView assistantText = (TextView)findViewById(R.id.assistantText);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/assistantfont.ttf");

        userText.setTypeface(custom_font);
        assistantText.setTypeface(custom_font);
    }

}
