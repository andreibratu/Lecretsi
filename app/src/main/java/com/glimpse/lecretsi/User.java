package com.glimpse.lecretsi;
import com.google.firebase.auth.FirebaseUser;

public class User {

    private String id;
    private String email;
    private String name;
    private String photoURL;

    public User() {

    }

    public User(String id, String name, String email, String photoURL){
        this.id = id;
        this.name = name;
        this.email = email;
        this.photoURL = photoURL;
    }

    public User(FirebaseUser acct) {
        this.id = acct.getUid();
        this.name = acct.getDisplayName();
        this.email = acct.getEmail();
        if(acct.getPhotoUrl() != null) {
            this.photoURL = acct.getPhotoUrl().toString().replace("/s96-c/","/s256-c/");
        }
    }

    // TODO: Save all the info in a SavedSharedPreference

    public String getId() {return id;}
    public String getEmail() {return email;}
    public String getName() {return name;}
    public String getPhotoURL() {return photoURL;}

    /*
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


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users")
                .child(Integer.toString(this.userID)).child("usedPhrases").setValue(this.usedPhrases);

    }
    */
}
