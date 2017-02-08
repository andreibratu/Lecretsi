package com.glimpse.lecretsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.api.client.util.Data;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactsActivity extends AppCompatActivity {

    final String LOGGED_USER_ID = LoginActivity.loggedInUser.getUserID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        DatabaseReference newFriendListener = FirebaseDatabase.getInstance().getReference()
                .child("friendships")
                .child(LOGGED_USER_ID);

        newFriendListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // added friend as a pair {userID-true}

                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(LOGGED_USER_ID).child("friends")
                        .child(dataSnapshot.getValue(User.class).getUserID())
                        .setValue(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ContactsActivity", "Error adding friendships: "+ databaseError.getMessage());
            }
        });

        
        DatabaseReference newFriendRequestListener = FirebaseDatabase.getInstance().getReference()
                .child("friend_requests").child(LOGGED_USER_ID);

        newFriendRequestListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //added friend request as userID-true
                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(LOGGED_USER_ID).child("friend_requests")
                        .child(dataSnapshot.getValue(User.class).getUserID())
                        .setValue(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ContactsActivity", "Error adding friend request: "+databaseError.getMessage());
            }
        });

        //TODO @ADI https://firebaseui.com/docs/android/com/firebase/ui/FirebaseListAdapter.html
        //TODO List adapters for firebase
        DatabaseReference mUserFriends = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER_ID).child("friends");
        DatabaseReference mUserFriendRequests = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER_ID).child("friend_requests");
    }

    void acceptFriendRequest(String whoseFriendReqID, String receiverFriendReqID) {

    }
}
