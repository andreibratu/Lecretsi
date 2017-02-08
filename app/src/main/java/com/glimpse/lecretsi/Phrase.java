package com.glimpse.lecretsi;

import java.util.*;

public class Phrase implements Comparable<Phrase>{

    private String phrase;
    private String largonjiPhrase;
    public double priorityKey;
    private int howManyTimesUsed;

    @Override
    public int compareTo(Phrase comparePhrase) {
        //ascending
        //return java.lang.Double.compare(this.priorityKey,comparePhrase.priorityKey);
        //descending
        return java.lang.Double.compare(comparePhrase.priorityKey,this.priorityKey);
    }

    private Date lastTimeUsed;
    private int length;

    Phrase() {}  ///Every table needs a default constructor

    Phrase(String phrase) {
        this.phrase = phrase;
        this.largonjiPhrase = Largonji.algorithmToLargonji(phrase);
        this.howManyTimesUsed = 1;
        this.lastTimeUsed = new Date();
        this.length = phrase.length();
        this.calculateKey();
    }

    double getPriorityKey() {return this.priorityKey;}

    private void calculateKey() {
        Date timeNow = new Date();
        double lastUsedIndex = 1 / (double) (timeNow.getTime() - this.lastTimeUsed.getTime());
        double howOftenUsed = this.howManyTimesUsed * this.length; // Longer more often used words have priority

        double MAGIC_TOUCH = 42;
        this.priorityKey = lastUsedIndex * howOftenUsed / MAGIC_TOUCH;
    }

    public void updatePhrase() {
        this.howManyTimesUsed += 1;
        this.calculateKey();
        this.lastTimeUsed = new Date();
    }

}
