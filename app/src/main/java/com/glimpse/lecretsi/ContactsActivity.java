package com.glimpse.lecretsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    ArrayList<User> contactsToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        View assistantContact = findViewById(R.id.assistantContact);
        TextView assistantName = (TextView) assistantContact.findViewById(R.id.contactUsername);
        TextView assistantEmail = (TextView) assistantContact.findViewById(R.id.contactEmail);

        assistantName.setText("Largonji Assistant");
        assistantEmail.setText("@largonji");

        DatabaseReference friendListener = FirebaseDatabase.getInstance().getReference()
                .child("friendships")
                .child(Integer.toString(LoginActivity.loggedInUser.getUserID()));

        friendListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // added friend as a pair {userID-true}
                String friendID = dataSnapshot.getKey();
                DatabaseReference newFriend = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(friendID);

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
    }
}
