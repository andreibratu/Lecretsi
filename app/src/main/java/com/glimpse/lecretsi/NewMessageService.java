package com.glimpse.lecretsi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;


public class NewMessageService extends Service implements Runnable {

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "New message service started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void run() {

        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("conversations");


        mReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (!ConversationsActivity.userActive) {
                    Conversation mConversation = dataSnapshot.getValue(Conversation.class);
                    final String text = mConversation.getLastMessage();
                    final User sender = mConversation.getUser();
                    if(sender != null) {

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = null;
                                try {
                                    bitmap = Glide.with(getApplicationContext())
                                            .load(sender.getPhotoURL())
                                            .asBitmap()
                                            .into(64, 64).get();
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                                Intent openConversationActivity = new Intent(getApplicationContext()
                                        , ConversationsActivity.class);
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
                                newNotificationBuilder.setContentText(text);
                                newNotificationBuilder.setContentIntent(resultPendingActivity);

                                // Sets an ID for the notification
                                int mNotificationId = 1;
                                // Gets an instance of the NotificationManager service
                                NotificationManager mNotifyMgr =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                // Builds the notification and issues it.
                                mNotifyMgr.notify(mNotificationId, newNotificationBuilder.build());
                            }
                        });



                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        run();
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
