package com.glimpse.lecretsi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewMessageService extends Service {

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mReference.child("users").child(ConversationsActivity.loggedInUser.getId())
                .child("conversations");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot newMessage:dataSnapshot.getChildren()) {

                    Conversation mMessage = newMessage.getValue(Conversation.class);
                    String text = mMessage.getLastMessage();
                    User sender = mMessage.getUser();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
