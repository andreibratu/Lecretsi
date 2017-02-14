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

public class ChatActivity extends AppCompatActivity {

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

    // This is the activity that's gonna enlist all the different conversations

    ImageButton mSendButton, expandButton;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;

    private DatabaseReference myConversationReference, friendConversationReference, databaseReference;

    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;

    DateFormat dateFormat = new SimpleDateFormat("d EEE", Locale.FRANCE);
    DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    String date, time;

    private Long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String friendUserID = intent.getStringExtra("friendUserID");
        String friendUsername = intent.getStringExtra("friendUsername");

        setTitle(friendUsername);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        myConversationReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER.getId()).child("conversations").child(friendUserID);
        friendConversationReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(friendUserID).child("conversations").child(LOGGED_USER.getId());

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.user_message,
                MessageViewHolder.class,
                myConversationReference.child("chatMessages")) {

            MessageViewHolder lastMessageSelected, lastMessageLongClicked;
            ChatMessage lastMessage = null;

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final ChatMessage chatMessage, int position) {
                viewHolder.messageTextView.setText(chatMessage.getLargonjiText());
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

                viewHolder.messageTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(lastMessageLongClicked != null){
                            lastMessageLongClicked.messageTextView.setText(chatMessage.getLargonjiText());
                        }
                        if(viewHolder.messageTextView.getText().equals(chatMessage.getLargonjiText())) {
                            viewHolder.messageTextView.setText(chatMessage.getNormalText());
                        } else {
                            viewHolder.messageTextView.setText(chatMessage.getLargonjiText());
                        }
                        lastMessageLongClicked = viewHolder;
                        return false;
                    }
                });

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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(LOGGED_USER.getId()).child("serverTimestamp").addValueEventListener(new ValueEventListener() {
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

        databaseReference.child(LOGGED_USER.getId()).child("serverTimestamp").setValue(ServerValue.TIMESTAMP);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mMessageEditText.getText().toString();
                if (text.isEmpty()) {
                    expandButton.setVisibility(View.VISIBLE);
                    mSendButton.setVisibility(View.GONE);
                } else {
                    mSendButton.setVisibility(View.VISIBLE);
                    expandButton.setVisibility(View.GONE);
                }
                databaseReference.child(LOGGED_USER.getId()).child("serverTimestamp").setValue(ServerValue.TIMESTAMP);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void onSend(View view){
        if(!mMessageEditText.getText().toString().isEmpty()) {
            final String text = Largonji.algorithmWrapper(mMessageEditText.getText().toString(),true);
            mMessageRecyclerView.scrollToPosition(mFirebaseAdapter.getItemCount() - 1);
            onUserMessage(text);
            mMessageEditText.setText("");
        }

    }

    public void onUserMessage(final String message){
        final ChatMessage chatMessage = new ChatMessage(LOGGED_USER.getId(), mMessageEditText.getText().toString(), message, date, time);
        myConversationReference.child("chatMessages").push().setValue(chatMessage);
        myConversationReference.child("lastMessage").setValue(message);
        myConversationReference.child("lastMessageDate").setValue(ServerValue.TIMESTAMP);
        friendConversationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    Conversation conversation = new Conversation(LOGGED_USER, null, null);
                    friendConversationReference.setValue(conversation);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        friendConversationReference.child("chatMessages").push().setValue(chatMessage);
        friendConversationReference.child("lastMessage").setValue(message);
        friendConversationReference.child("lastMessageDate").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.userActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.userActive = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
