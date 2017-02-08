package com.glimpse.lecretsi;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class User {

    public static int userCount = 0;
    private static Map<String,Integer> usersDictionary ;

    private int userID;
    private String firstName;
    private String lastName;
    private String email;
    private String photoURL;
    private ArrayList<Integer> friends;
    private ArrayList<Integer> friendRequests;
    private ArrayList<Integer> conversations;
    private ArrayList<Phrase> usedPhrases;

    //TODO link user to db
    //TODO add getter for friends and conversations

    User(){}

    User(GoogleSignInAccount acct) {
        this.email = acct.getEmail();
        this.firstName = acct.getFamilyName();
        this.lastName = acct.getGivenName();
        this.userID = User.userCount;
        this.photoURL = (!Objects.equals(acct.getPhotoUrl().toString(), ""))
                ? acct.getPhotoUrl().toString():null;

        usersDictionary.put(this.email,this.userID);
    }

    public int getUserID() {return this.userID;}
    public String getEmail() {return this.email;}
    public String getName() {return this.firstName+" "+this.lastName;}
    public String getPhotoURL() {return this.photoURL;}

    public void addFriend(int indexFriend) {
        assert this.friendRequests.indexOf(indexFriend)!=-1;

        this.friends.add(indexFriend);
        this.friendRequests.remove(this.friendRequests.indexOf(indexFriend));
    }

    public void sendFriendRequest(String email) {
        final DatabaseReference queriedUser = FirebaseDatabase.getInstance()
                .getReference("users/"+Integer.toString(usersDictionary.get(email)));

        queriedUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User mUser = dataSnapshot.getValue(User.class);
                mUser.friendRequests.add(User.this.userID);
                queriedUser.setValue(mUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User.sendRequest error", String.valueOf(databaseError.toException()));
            }
        });
    }

    public ArrayList<User> getFriends() {
        final ArrayList<User> aux = new ArrayList<>();

        DatabaseReference userFriends = FirebaseDatabase.getInstance().getReference();
        for(int x:friends) {
            userFriends = userFriends.child(Integer.toString(x));
            userFriends.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    aux.add(dataSnapshot.getValue(User.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("User.getFriends error", String.valueOf(databaseError.toException()));
                }
            });
        }
        return aux;
    }

    public Phrase[] getRelevantPhrases() {
        Collections.sort(usedPhrases);

        Phrase[] relevantPhrase = new Phrase[30];
        for(int i=0;i<30;i+=1) {
            relevantPhrase[i] = usedPhrases.get(i);
        }
        return relevantPhrase;
    }

    public void updatePhrase(Phrase phrase) {
        if(this.usedPhrases.contains(phrase)) {
            this.usedPhrases.get(this.usedPhrases.indexOf(phrase)).updatePhrase();
        }
        else
            this.usedPhrases.add(phrase);

        /*
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users")
                .child(Integer.toString(this.userID)).child("usedPhrases").setValue(this.usedPhrases);
                */
    }

    public ArrayList<Integer> getConversations() {
        return this.conversations;
    }

    public void deleteUserFromConversation(int whatConversation) {
        assert this.conversations.indexOf(whatConversation) != -1;

        this.conversations.remove(this.conversations.indexOf(whatConversation));
    }
}
