package com.glimpse.lecretsi;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    // This is the activity that's gonna enlist all the different conversations

    TextView userText, assistantText;
    EditText messageText;
    ImageButton sendButton, expandButton;
    LinearLayout messagesLayout;
    RelativeLayout chatLayout;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatLayout = (RelativeLayout) findViewById(R.id.chatLayout);
        //messagesLayout = (LinearLayout)findViewById(R.id.messagesLayout);

        messageText = (EditText) findViewById(R.id.messageText);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        expandButton = (ImageButton) findViewById(R.id.expandButton);

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = messageText.getText().toString();
                if(text.isEmpty()){
                    expandButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                    expandButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setPeekHeight(0);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/assistantfont.ttf");

    }

    public void onSend(View view){
        if(!messageText.getText().toString().isEmpty()) {
            onUserMessage();
            final String text = Largonji.algorithmToLargonji(messageText.getText().toString());
            messageText.setText("");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    onAssistantMessage(text);
                }
            }, 500);
        }
    }

    public void onExpand(View view){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void onUserMessage(){

    }

    public void onAssistantMessage(String message){

    }

}
