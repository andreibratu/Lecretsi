package com.glimpse.lecretsi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import java.util.Objects;

public class AssistantActivity extends AppCompatActivity {

    private User LOGGED_USER = new User(FirebaseAuth.getInstance().getCurrentUser());
    private String ENCRYPT;
    private String DECRYPT;

    class AssistantPreferences {
        private String prefAssistantMode = ENCRYPT;

        SharedPreferences getSharedPreference(Context ctx) {
            return PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        void setAssistantMode(Context ctx, String mode) {
            SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
            editor.putString(prefAssistantMode, mode);
            editor.apply();
        }

        String getAssistantMode(Context ctx) {
            return getSharedPreference(ctx).getString(prefAssistantMode, ENCRYPT);
        }
    }

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

    // This is the activity that's gonna enlist all the different conversations

    ImageButton mSendButton, expandButton;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;

    private DatabaseReference mConversationReference, timestampReference;

    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;

    DateFormat dateFormat = new SimpleDateFormat("d EEE", Locale.FRANCE);
    DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    String date, time;

    private Long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Largonji Activity");

        ENCRYPT = getString(R.string.to_largonji);
        DECRYPT = getString(R.string.to_normal);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mConversationReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER.getId()).child("conversations").child("largonjiAssistant");

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.user_message,
                MessageViewHolder.class,
                mConversationReference.child("chatMessages")) {

            MessageViewHolder lastMessageSelected = null;
            ChatMessage lastMessage = null;

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final ChatMessage chatMessage, int position) {
                viewHolder.messageTextView.setText(chatMessage.getText());
                viewHolder.messageDateTime.setText(chatMessage.getDate() + " • " + chatMessage.getTime());

                if (chatMessage.getId().equals(LOGGED_USER.getId())) {
                    viewHolder.messageLayout.setGravity(Gravity.END);
                    viewHolder.messagePosition.setGravity(Gravity.END);
                    viewHolder.messagePosition.setPadding(150, 0, 0, 0);
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.user_text_box);
                } else {
                    viewHolder.messageLayout.setGravity(Gravity.START);
                    viewHolder.messagePosition.setGravity(Gravity.START);
                    viewHolder.messagePosition.setPadding(0, 0, 150, 0);
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.friend_text_box);
                }
                viewHolder.messageDateTime.setVisibility(View.GONE);
                viewHolder.messageTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Tap a message to see / hide the date it was sent

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

                lastMessage = chatMessage;

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageText);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        expandButton = (ImageButton) findViewById(R.id.expandButton);

        timestampReference = FirebaseDatabase.getInstance().getReference().child("users").child(LOGGED_USER.getId()).child("serverTimestamp");
        timestampReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                timestamp = Long.parseLong(snapshot.getValue().toString());
                date = dateFormat.format(new Date(timestamp));
                time = timeFormat.format(new Date(timestamp));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        timestampReference.setValue(ServerValue.TIMESTAMP);

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
                timestampReference.setValue(ServerValue.TIMESTAMP);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                                            onAssistantMessage(getString(R.string.assistant_code_hint));
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    onAssistantMessage(getString(R.string.assistant_decode_hint));
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            onAssistantMessage(getString(R.string.assistant_expect_response));
                                                        }
                                                    }, 500);
                                                }
                                            }, 500);
                                        }
                                    }, 500);
                                }
                            }, 500);
                        }
                    }, 500);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onSend(View view){

        if(!mMessageEditText.getText().toString().isEmpty()) {
            mMessageRecyclerView.scrollToPosition(mFirebaseAdapter.getItemCount() - 1);
            timestampReference.setValue(ServerValue.TIMESTAMP);
        }

        onUserMessage(mMessageEditText.getText().toString());
        AssistantPreferences updateAssistantMode;
        switch (mMessageEditText.getText().toString()) {
            case "#decrypt" :
                updateAssistantMode = new AssistantPreferences();
                updateAssistantMode.setAssistantMode(getApplicationContext(), DECRYPT);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onAssistantMessage(getString(R.string.assistant_was_set_decrypt));
                    }
                }, 500);

                break;

            case "#encrypt" :
                updateAssistantMode = new AssistantPreferences();
                updateAssistantMode.setAssistantMode(getApplicationContext(), ENCRYPT);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onAssistantMessage(getString(R.string.assistant_was_set_to_encrypt));
                    }
                }, 500);

                break;
            default:
                AssistantPreferences assistantMode = new AssistantPreferences();
                Boolean isSetEncrypt = assistantMode.getAssistantMode(getApplicationContext()).equals(ENCRYPT);
                final String text = Largonji.algorithmWrapper(mMessageEditText.getText().toString(), isSetEncrypt);
                if(!text.equals("invalid_input")) {
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onAssistantMessage(getString(R.string.could_not_decrypt1));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onAssistantMessage(getString(R.string.could_not_decrypt2));
                                }
                            }, 500);
                        }
                    }, 500);
                }
                break;
        }
        mMessageEditText.setText("");
    }

    public void onUserMessage(final String message){
        final ChatMessage chatMessage = new ChatMessage(LOGGED_USER.getId(), message, date, time);
        mConversationReference.child("chatMessages").push().setValue(chatMessage);
        mConversationReference.child("lastMessage").setValue(message);
        mConversationReference.child("lastMessageDate").setValue(ServerValue.TIMESTAMP);
    }

    public void onAssistantMessage(String message){
        if(message != null) {
            ChatMessage chatMessage = new ChatMessage("largonjiAssistant", message, date, time);
            mConversationReference.child("chatMessages").push().setValue(chatMessage);
            mConversationReference.child("lastMessage").setValue(message);
            mConversationReference.child("lastMessageDate").setValue(ServerValue.TIMESTAMP);
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
        endPhrases[1] = getString(R.string.assistant_anything_else);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

}
