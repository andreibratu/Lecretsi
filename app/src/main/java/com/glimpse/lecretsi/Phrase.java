package com.glimpse.lecretsi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class Phrase {

    private final static String LOGGED_IN_USER = ConversationsActivity.loggedInUser.getId();
    private String phrase;
    private String largonjiPhrase;
    public double priorityKey;
    private int howManyTimesUsed;
    private Date lastTimeUsed;
    private int length;

    Phrase() {}

    Phrase(String phrase) {
        phrase = phrase;
        largonjiPhrase = Largonji.algorithmWrapper(phrase);
        howManyTimesUsed = 1;
        lastTimeUsed = new Date();
        length = phrase.length();
        calculateKey();
    }

    public double calculateKey() {
        Date timeNow = new Date();
        double lastUsedIndex = 1 / (double) (timeNow.getTime() - this.lastTimeUsed.getTime());
        double howOftenUsed = this.howManyTimesUsed * this.length;
        // Longer more often used words have priority

        double MAGIC_TOUCH = 42;
        this.priorityKey = lastUsedIndex * howOftenUsed / MAGIC_TOUCH;
    }

    public static void updatePhrase(final String phrase) {

        DatabaseReference pathPhrase = FirebaseDatabase.getInstance().getReference().
                child("priority_phrases").child(LOGGED_IN_USER).child(phrase);

        if( pathPhrase == null ) {
            pathPhrase.setValue(User(phrase));
        }
        else
        {
            pathPhrase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                //TODO Tommorow
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot x:dataSnapshot) {
                        Phrase phraseToUpdate = x.getValue(Phrase.class);
                        phraseToUpdate.howManyTimesUsed++;
                        phraseToUpdate.calculateKey();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
