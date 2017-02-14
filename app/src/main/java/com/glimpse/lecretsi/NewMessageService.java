package com.glimpse.lecretsi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewMessageService extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference()
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
                    final Conversation mConversation = dataSnapshot.getValue(Conversation.class);
                    if (!MainActivity.userActive) {
                        if (mConversation != null) {
                            if(!mConversation.getUser().getId().equals("largonjiAssistant")) {
                                Glide.with(getApplicationContext())
                                        .load(mConversation.getUser().getPhotoURL())
                                        .asBitmap()
                                        .into(new SimpleTarget<Bitmap>(128, 128) {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                newNotificationBuilder.setLargeIcon(resource);
                                                newNotificationBuilder.setSmallIcon(R.drawable.ic_menu_conversations);
                                                newNotificationBuilder.setContentTitle(mConversation.getUser().getName());
                                                newNotificationBuilder.setContentText(mConversation.getLastMessage());
                                                newNotificationBuilder.setSound(notifSound);
                                                newNotificationBuilder.setVibrate(new long[]{500, 500, 500});
                                                newNotificationBuilder.setLights(Color.GREEN, 3000, 3000);

                                                Notification notification = newNotificationBuilder.build();
                                                // Builds the notification and issues it.
                                                mNotifyMgr.notify(0, notification);
                                            }
                                        });
                            }
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
