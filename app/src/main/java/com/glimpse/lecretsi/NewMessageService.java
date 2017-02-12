package com.glimpse.lecretsi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;


public class NewMessageService extends Service {

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "New message service started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mReference.child("users").child(ConversationsActivity.loggedInUser.getId())
                .child("conversations");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot newMessage:dataSnapshot.getChildren()) {

                    Conversation mConversation = newMessage.getValue(Conversation.class);
                    String text = mConversation.getLastMessage();
                    User sender = mConversation.getUser();
                    Bitmap bitmap = null;

                    try {
                        bitmap = Glide.with(getApplicationContext())
                                .load(sender.getPhotoURL())
                                .asBitmap()
                                .into(64,64).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    Intent openConversationActivity = new Intent(getApplicationContext()
                            ,ConversationsActivity.class);
                    PendingIntent resultPendingActivity =
                            PendingIntent.getActivity(
                                getApplicationContext(), 0, openConversationActivity,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    NotificationCompat.Builder newNotificationBuilder
                            = new NotificationCompat.Builder(getApplicationContext());
                    newNotificationBuilder.setSmallIcon(R.drawable.ic_menu_conversations);
                    newNotificationBuilder.setLargeIcon(bitmap);
                    newNotificationBuilder.setContentTitle(sender.getName());
                    newNotificationBuilder.setContentText(mConversation.getLastMessage());
                    newNotificationBuilder.setContentIntent(resultPendingActivity);

                    // Sets an ID for the notification
                    int mNotificationId = 1;
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(mNotificationId, newNotificationBuilder.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"New message service stopped", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
