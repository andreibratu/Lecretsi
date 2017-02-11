package com.glimpse.lecretsi;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    static final User LOGGED_USER = ConversationsActivity.loggedInUser;

    public static String lastMessage = "";
    //TODO This warning sounds kinda bad
    static TextView lastMessageSelected = null;

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView messageTextView;
        TextView messageDateTime;
        LinearLayout messageLayout;
        LinearLayout messagePosition;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.userMessage);
            messageDateTime = (TextView) itemView.findViewById(R.id.messageDateTime);
            messageLayout = (LinearLayout) itemView.findViewById(R.id.messageLayout);
            messagePosition = (LinearLayout) itemView.findViewById(R.id.messagePosition);

            messageDateTime.setVisibility(View.GONE);
            messageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lastMessageSelected != null)
                        lastMessageSelected.setVisibility(View.GONE);
                    if(messageDateTime.getVisibility() != View.VISIBLE){
                        messageDateTime.setVisibility(View.VISIBLE);
                    } else {
                        messageDateTime.setVisibility(View.GONE);
                    }
                    lastMessageSelected = messageDateTime;
                }
            });

            lastMessage = messageTextView.getText().toString();
        }
    }

    // This is the activity that's gonna enlist all the different conversations

    ImageButton mSendButton, expandButton;
    RelativeLayout chatLayout;
    private BottomSheetBehavior mBottomSheetBehavior;

    private static final String TAG = "ChatActivity";

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;

    private DatabaseReference mConversationReference;

    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>
            mFirebaseAdapter;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        mConversationReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER.getId()).child("conversations").child(userId);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.user_message,
                MessageViewHolder.class,
                mConversationReference.child("chatMessages")) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage chatMessage, int position) {
                /*
                if(firstMessage){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 250);
                    viewHolder.messageLayout.setLayoutParams(layoutParams);
                    firstMessage = false;
                }*/
                if (chatMessage.getId().equals(LOGGED_USER.getId())) {
                    viewHolder.messageLayout.setGravity(Gravity.END);
                    viewHolder.messagePosition.setGravity(Gravity.END);
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.user_text_box);
                    viewHolder.messageTextView.setText(chatMessage.getText());
                    viewHolder.messageDateTime.setText(chatMessage.getDateTime());
                } else {
                    viewHolder.messageLayout.setGravity(Gravity.START);
                    viewHolder.messagePosition.setGravity(Gravity.START);
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.friend_text_box);
                    viewHolder.messageTextView.setText(chatMessage.getText());
                    viewHolder.messageDateTime.setText(chatMessage.getDateTime());
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        chatLayout = (RelativeLayout) findViewById(R.id.chatLayout);
        //messagesLayout = (LinearLayout)findViewById(R.id.messagesLayout);

        mMessageEditText = (EditText) findViewById(R.id.messageText);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        expandButton = (ImageButton) findViewById(R.id.expandButton);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mMessageEditText.getText().toString();
                if(text.isEmpty()){
                    expandButton.setVisibility(View.VISIBLE);
                    mSendButton.setVisibility(View.GONE);
                } else {
                    mSendButton.setVisibility(View.VISIBLE);
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
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        //TODO @Adi how ab this ?
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/assistantfont.ttf");

        if(userId.equals("largonjiAssistant")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (lastMessage.isEmpty()) {
                        onAssistantMessage(getString(R.string.assistant_greeting) + " " + LOGGED_USER.getName());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onAssistantMessage(getString(R.string.assistant_presentation));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        onAssistantMessage(getString(R.string.assistant_expect_response));
                                    }
                                }, 1000);
                            }
                        }, 1000);
                    }
                }
            }, 1000);
        }
    }

    public void onSend(View view){
        if(!mMessageEditText.getText().toString().isEmpty()) {
            final String text = Largonji.algorithmWrapper(mMessageEditText.getText().toString());
            onUserMessage(text);
            mMessageEditText.setText("");
            if(userId.equals("largonjiAssistant")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        randomStartPhrase();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onAssistantMessage(text);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        randomEndPhrase();
                                    }
                                }, 500);
                            }
                        }, 500);
                    }
                }, 500);
            }
        }
    }

    public void onExpand(View view){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    //Locale.getDefault for local time
    DateFormat df = new SimpleDateFormat("d EEE • HH:mm", Locale.getDefault());
    String date = df.format(Calendar.getInstance().getTime());

    public void onUserMessage(String message){
        final ChatMessage chatMessage = new ChatMessage(LOGGED_USER.getId(), message, date);
        mConversationReference.child("chatMessages").push().setValue(chatMessage);
        if(!userId.equals("largonjiAssistant")) {
            final Conversation conversation = new Conversation(LOGGED_USER, null, null);
            final DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(userId).child("conversations").child(LOGGED_USER.getId());
            conversationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null) {
                        conversationReference.setValue(conversation);
                    }
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(userId).child("conversations").child(LOGGED_USER.getId()).child("chatMessages").push().setValue(chatMessage);

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public void onAssistantMessage(String message){
        if(message != null) {
            ChatMessage chatMessage = new
                    ChatMessage("largonjiAssistant", message, date);
            mConversationReference.child("chatMessages").push().setValue(chatMessage);
        }
    }

    public void randomStartPhrase(){
        int randomNum = 1 + (int)(Math.random() * 3);
        String startPhrases[] = new String[4];
        startPhrases[1] = "Here's your phrase in Largonji";
        onAssistantMessage(startPhrases[randomNum]);
    }

    public void randomEndPhrase(){
        int randomNum = 1 + (int)(Math.random() * 3);
        String endPhrases[] = new String[4];
        endPhrases[1] = "Anything else?";
        endPhrases[2] = "What else?";
        onAssistantMessage(endPhrases[randomNum]);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
