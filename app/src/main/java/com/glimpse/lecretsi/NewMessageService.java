package com.glimpse.lecretsi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
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


public class NewMessageService extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("conversations");

        final NotificationCompat.Builder newNotificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Uri notifSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent openConversationActivity = new Intent(getApplicationContext()
                , ConversationsActivity.class);
        final PendingIntent resultPendingActivity =
                PendingIntent.getActivity(
                        getApplicationContext(), 0, openConversationActivity,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        newNotificationBuilder.setContentIntent(resultPendingActivity);

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

                                newNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                                newNotificationBuilder.setLargeIcon(bitmap);
                                newNotificationBuilder.setContentTitle(sender.getName());
                                newNotificationBuilder.setContentText(text);
                                newNotificationBuilder.setSound(notifSound);
                                newNotificationBuilder.setVibrate(new long[] { 1000, 1000 });
                                newNotificationBuilder.setLights(Color.GREEN, 3000, 3000);

                                Notification notification = newNotificationBuilder.build();
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                                // Builds the notification and issues it.
                                mNotifyMgr.notify(0, notification);
                            }
                        });
                    }
                } else {
                    if(!ChatActivity.inChat) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setSound(notifSound);
                        Notification notification = newNotificationBuilder.build();
                        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
                        mNotifyMgr.notify(1234, notification);
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(500);
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
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
