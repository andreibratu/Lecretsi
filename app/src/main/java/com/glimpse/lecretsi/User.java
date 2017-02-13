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

    public void setId(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhotoURL(String photoURL){
        this.photoURL = photoURL;
    }

    public String getPhotoURL() {
        return photoURL;
    }

}
