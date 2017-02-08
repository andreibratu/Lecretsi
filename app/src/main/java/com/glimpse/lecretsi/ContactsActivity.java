package com.glimpse.lecretsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    ArrayList<User> contactsToDisplay;
    ArrayList<User> friendRequestToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        DatabaseReference friendListener = FirebaseDatabase.getInstance().getReference()
                .child("friendships")
                .child(LoginActivity.loggedInUser.getUserID());

        final DatabaseReference friendRequestListener = FirebaseDatabase.getInstance().getReference()
                .child("friend_requests")
                .child(LoginActivity.loggedInUser.getUserID());

        friendListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // added friend as a pair {userID-true}

                DatabaseReference newFriend = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(dataSnapshot.getKey());

                newFriend.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        contactsToDisplay.add(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("ContactsActivity", "Error reading friend profile");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ContactsActivity", "Error reading friendships");
            }
        });


        friendRequestListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference findFriend = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(dataSnapshot.getKey());

                findFriend.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendRequestToDisplay.add(dataSnapshot.getValue(User.class));
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
    }
}
