package com.glimpse.lecretsi;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

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

    //TODO Update db
    Phrase(String phrase) {

        if( FirebaseDatabase.getInstance().getReference().child("priority_phrase")
                .child(LOGGED_IN_USER).child(this.phrase) ==null ) {
            this.phrase = phrase;
            this.largonjiPhrase = Largonji.algorithmWrapper(phrase);
            this.howManyTimesUsed = 1;
            this.lastTimeUsed = new Date();
            this.length = phrase.length();
            this.calculateKey();

            FirebaseDatabase.getInstance().getReference().child("priority_phrase")
                    .child(LOGGED_IN_USER).child(this.phrase).setValue(this);

        } else {
            this.updatePhrase();
        }
    }

    double getPriorityKey() {return this.priorityKey;}

    private void calculateKey() {
        Date timeNow = new Date();
        double lastUsedIndex = 1 / (double) (timeNow.getTime() - this.lastTimeUsed.getTime());
        double howOftenUsed = this.howManyTimesUsed * this.length;
        // Longer more often used words have priority

        double MAGIC_TOUCH = 42;
        this.priorityKey = lastUsedIndex * howOftenUsed / MAGIC_TOUCH;
    }

    public void updatePhrase() {
        this.howManyTimesUsed += 1;
        this.calculateKey();
        this.lastTimeUsed = new Date();
    }

}
