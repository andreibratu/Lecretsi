package com.glimpse.lecretsi;

        import java.util.ArrayList;
        import java.util.Collections;


public class User {

    private static int userCount = 0;
    private int userID;
    private String name;
    private String email;
    private String photoURL;
    private ArrayList<Integer> friends;
    private ArrayList<Integer> conversations;
    private ArrayList<Phrase> usedPhrases;

    //TODO link user to db
    //TODO add getter for friends and conversations

    User(){}

    User(String name, String email, String photoURL) {
        this.email = email;
        this.name = name;
        this.userID = ++User.userCount;
        this.photoURL = photoURL;
    }

    public void addFriend(int friendID) {
        this.friends.add(friendID);
    }

    public void addConversation(int conversationID) {
        this.conversations.add(conversationID);
    }

    public Phrase[] getRelevantPhrase() {
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
    }

    public ArrayList<Integer> getConversations() {
        return this.conversations;
    }

    public void deleteUserFromConversation(int whatConversation) {
        if(this.conversations.indexOf(whatConversation) != -1) {
            this.conversations.remove(this.conversations.indexOf(whatConversation));
        }
    }
}
