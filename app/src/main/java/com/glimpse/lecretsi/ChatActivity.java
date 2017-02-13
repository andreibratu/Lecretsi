package com.glimpse.lecretsi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//We will use the db to sync all clients' message dates else messages won't appear in order

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    final User LOGGED_USER = new User(FirebaseAuth.getInstance().getCurrentUser());

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
        }
    }

    public static Boolean inChat;

    // This is the activity that's gonna enlist all the different conversations

    ImageButton mSendButton, expandButton;
    RelativeLayout chatLayout;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;

    private DatabaseReference mConversationReference, databaseReference;

    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;

    private String userId; //id of the chat partner

    DateFormat dateFormat = new SimpleDateFormat("d EEE", Locale.FRANCE);
    DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    String date, time;

    private Long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // TODO: Set recycler views for every group of messages

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        mConversationReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER.getId()).child("conversations").child(userId);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.user_message,
                MessageViewHolder.class,
                mConversationReference.child("chatMessages")) {

            MessageViewHolder lastMessageSelected = null;
            ChatMessage lastMessage = null;

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, ChatMessage chatMessage, int position) {
                viewHolder.messageDateTime.setVisibility(View.GONE);
                viewHolder.messageTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ///Tap a message to see/hide the date it was sent

                        if(viewHolder.messageDateTime.getVisibility() != View.VISIBLE){
                            viewHolder.messageDateTime.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.messageDateTime.setVisibility(View.GONE);

                        }
                        if(lastMessageSelected != null && lastMessageSelected != viewHolder)
                            lastMessageSelected.messageDateTime.setVisibility(View.GONE);

                        lastMessageSelected = viewHolder;
                    }
                });

                //display the message in chat

                viewHolder.messageTextView.setText(chatMessage.getText());
                viewHolder.messageDateTime.setText(chatMessage.getDate() + " • " + chatMessage.getTime());

                if (chatMessage.getId().equals(LOGGED_USER.getId())) {
                    viewHolder.messageLayout.setGravity(Gravity.END);
                    viewHolder.messagePosition.setGravity(Gravity.END);
                    viewHolder.messagePosition.setPadding(100, 0, 0, 0);
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.user_text_box);
                    /* TODO: margins between different groups of messages
                    if(lastMessage != null){
                        if(!lastMessage.getId().equals(chatMessage.getId())){
                            viewHolder.messagePosition.setPadding(100, 75, 0, 0);
                        }
                    }*/
                } else {
                    viewHolder.messageLayout.setGravity(Gravity.START);
                    viewHolder.messagePosition.setGravity(Gravity.START);
                    viewHolder.messagePosition.setPadding(0, 0, 100, 0);
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.friend_text_box);
                    /*
                    if(lastMessage != null){
                        if(!lastMessage.getId().equals(chatMessage.getId())){
                            viewHolder.messagePosition.setPadding(0, 75, 100, 0);
                        }
                    }*/
                }

                lastMessage = chatMessage;

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            //adapter that queries and displays messages from db

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

        //TODO @Adi document the code starting from here
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

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

        //TODO @Adi how ab this ?
        //Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/assistantfont.ttf");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(LOGGED_USER.getId()).child("serverTimestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                timestamp = Long.parseLong(snapshot.getValue().toString());
                System.out.println(timestamp);
                date = dateFormat.format(new Date(timestamp));
                time = timeFormat.format(new Date(timestamp));
                mConversationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mConversationReference.child("lastMessageDate").setValue(timestamp.toString());
                        databaseReference.child(userId).child("conversations").child("lastMessageDate").setValue(timestamp.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //code for handling assistant's logic
        if(userId.equals("largonjiAssistant")) {
            mConversationReference.child("chatMessages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
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
                        }, 1000);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    //code that handles the translation of phrase by the assistant
    public void onSend(View view){
        if(!mMessageEditText.getText().toString().isEmpty()) {
            final String text = Largonji.algorithmWrapper(mMessageEditText.getText().toString(),true);
            if(userId.equals("largonjiAssistant")) {
                onUserMessage(mMessageEditText.getText().toString());
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
            } else {
                onUserMessage(text);
            }
            mMessageEditText.setText("");
        }

    }

    public void onUserMessage(final String message){
        final ChatMessage chatMessage = new ChatMessage(LOGGED_USER.getId(), message, date, time);
        mConversationReference.child("chatMessages").push().setValue(chatMessage);

        if(!userId.equals("largonjiAssistant")) {
            final DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(userId).child("conversations").child(LOGGED_USER.getId());
            conversationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null) {
                        Conversation conversation = new Conversation(LOGGED_USER, null, null);
                        conversationReference.setValue(conversation);
                    }
                    conversationReference.child("chatMessages").push().setValue(chatMessage);
                    conversationReference.child("lastMessage").setValue(message);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mConversationReference.child("lastMessage").setValue(message);
    }

    public void onAssistantMessage(String message){
        if(message != null) {
            //get time from server
            databaseReference.child("serverTimestamp").setValue(ServerValue.TIMESTAMP);
            ChatMessage chatMessage = new ChatMessage("largonjiAssistant", message, date, time);
            mConversationReference.child("chatMessages").push().setValue(chatMessage);
            mConversationReference.child("lastMessage").setValue(message);
        }
    }

    public void randomStartPhrase(){
        int randomNum = 1 + (int)(Math.random() * 3);
        String startPhrases[] = new String[4];
        startPhrases[1] = getString(R.string.assistant_start_phrase);
        onAssistantMessage(startPhrases[randomNum]);
    }

    public void randomEndPhrase(){
        int randomNum = 1 + (int)(Math.random() * 3);
        String endPhrases[] = new String[4];
        endPhrases[1] = getString(R.string.assistant_aything_else);
        endPhrases[2] = getString(R.string.assistant_anything_else2);
        onAssistantMessage(endPhrases[randomNum]);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        inChat = false;
        ConversationsActivity.userActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConversationsActivity.userActive = true;
        inChat = true;
        databaseReference.child("serverTimestamp").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs
        // (including Sign-In) will not be available
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
