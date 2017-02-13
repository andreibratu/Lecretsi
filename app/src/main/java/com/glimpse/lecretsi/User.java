package com.glimpse.lecretsi;
import com.google.firebase.auth.FirebaseUser;

class User {
    private String id;
    private String email;
    private String name;
    private String photoURL;

    public User() {

    }

    User(String id, String name, String email, String photoURL){
        this.id = id;
        this.name = name;
        this.email = email;
        this.photoURL = photoURL;
    }

    User(FirebaseUser acct) {
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

}
