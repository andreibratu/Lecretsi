package com.glimpse.lecretsi;
import com.orm.SugarRecord;
import java.util.*;

public class Phrase extends SugarRecord {
    /**
     *  We will use SugarORM for DB http://satyan.github.io/sugar/getting-started.html
     *  This class should be initialised once at the start of the app
     */
    //TODO Read documentation for Sugar
    private String phrase;
    private String largonjiPhrase;
    private long priorityKey;
    private int howManyTimesUsed;
    private Date lastTimeUsed;
    private int length;

    public static void updateDatabase(String phrase, String phraseLargonji) {
        List<Phrase> recordExistsCheck = Phrase.find(Phrase.class, "phrase = ?", phrase);
        if(recordExistsCheck.size()!=0) {
            updatePhrase(recordExistsCheck.get(0));
        }
        else {
            Phrase newRecord = new Phrase(phrase, phraseLargonji);
            newRecord.save();
        }
    }

    public List<Phrase> getRelevantPhrases() {
        return Phrase.findWithQuery(
                Phrase.class,
                "SELECT * from Phrase ORDER BY priorityKey DESC LIMIT 50"
        );
    }

    private static void updatePhrase(Phrase e) {
        e.lastTimeUsed = new Date();
        e.howManyTimesUsed += 1;
        e.calculateKey();
        e.save();
    }

    private void calculateKey() {
        Date timeNow = new Date();
        long lastUsedIndex = 1 / (timeNow.getTime() - lastTimeUsed.getTime());
        long howOftenUsed = this.howManyTimesUsed * this.length; // Longer more often used words have priority

        int MAGIC_TOUCH = 42;
        this.priorityKey = lastUsedIndex * howOftenUsed / MAGIC_TOUCH;
    }

    private Phrase(String phrase, String largonjiPhrase) {
        this.phrase = phrase;
        this.largonjiPhrase = largonjiPhrase;
        this.howManyTimesUsed = 1;
        this.lastTimeUsed = new Date();
        this.length = phrase.length();

        this.save();
    }

    /*
    private Date parseStringToDate(String input) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return iso8601Format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    */
}
