package com.glimpse.lecretsi;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class User {

    private String userID;
    private String firstName;
    private String lastName;
    private String photoURL;
    private ArrayList<Phrase> usedPhrases;

    //TODO link user to db
    //TODO add getter for friends and conversations

    User(){}

    User(GoogleSignInAccount acct) {
        this.firstName = acct.getFamilyName();
        this.lastName = acct.getGivenName();
        this.userID = acct.getEmail();
        this.photoURL = (!Objects.equals(acct.getPhotoUrl().toString(), ""))
                ? acct.getPhotoUrl().toString():null;
    }

    public String getUserID() {return this.userID;}
    public String getName() {return this.firstName+" "+this.lastName;}
    public String getPhotoURL() {return this.photoURL;}

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
}
