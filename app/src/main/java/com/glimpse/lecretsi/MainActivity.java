package com.glimpse.lecretsi;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView userText, assistantText;
    EditText messageText;
    ImageButton sendButton;
    LinearLayout convLayout;
    RelativeLayout mainLayout;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        convLayout = (LinearLayout)findViewById(R.id.convLayout);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.requestFocus();

        userText = (TextView)findViewById(R.id.userText);
        assistantText = (TextView)findViewById(R.id.assistantText);
        messageText = (EditText)findViewById(R.id.messageText);
        sendButton = (ImageButton)findViewById(R.id.sendButton);

        userText.setVisibility(View.INVISIBLE);
        assistantText.setVisibility(View.INVISIBLE);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/assistantfont.ttf");

        userText.setTypeface(custom_font);
        assistantText.setTypeface(custom_font);



    }

    public void onSend(View view){
        if(!messageText.getText().toString().isEmpty()) {
            userText.setText(messageText.getText().toString());
            userText.setVisibility(View.VISIBLE);
            final String text = Largonji.algorithmWrapper(messageText.getText().toString());
            messageText.setText("");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    assistantText.setText(text);
                    assistantText.setVisibility(View.VISIBLE);
                }
            }, 500);
        }
    }

}
