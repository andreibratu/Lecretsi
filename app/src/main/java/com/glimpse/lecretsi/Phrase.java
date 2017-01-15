package com.glimpse.lecretsi;
import com.orm.SugarRecord;
import java.util.*;

public class Phrase extends SugarRecord {
    /**
     *  We will use SugarORM for DB http://satyan.github.io/sugar/getting-started.html
     *  This class should be initialised once at the start of the app
     */
    ///@ADI IMPORTANT : INSTANT RUN *MIGHT* BREAK THIS CLASS further testing needed

    private String phrase;
    private String largonjiPhrase;
    private double priorityKey;
    private int howManyTimesUsed;
    private Date lastTimeUsed;
    private int length;

    //TODO predefined phrases initialised in def constr ?
    public Phrase() {}  ///Every table needs a default constructor

    static void updateDatabase(String phrase, String phraseLargonji) {
        List<Phrase> recordExistsCheck = Phrase.find(Phrase.class, "phrase=?", phrase);

        if(recordExistsCheck.size()==0) {
            Phrase newRecord = new Phrase(phrase, phraseLargonji);
            newRecord.save();
        }
        else {
            Phrase updatePhrase = recordExistsCheck.get(0);
            updatePhrase.calculateKey();
            updatePhrase.lastTimeUsed = new Date();
            updatePhrase.howManyTimesUsed += 1;

            updatePhrase.save();
        }
    }

    public List<Phrase> getRelevantPhrases() {
        return Phrase.findWithQuery(
                Phrase.class,
                "SELECT * from Phrase ORDER BY priorityKey DESC LIMIT 50"
        );
    }

    private void calculateKey() {
        Date timeNow = new Date();
        double lastUsedIndex = 1 / (double) (timeNow.getTime() - this.lastTimeUsed.getTime());
        double howOftenUsed = this.howManyTimesUsed * this.length; // Longer more often used words have priority

        double MAGIC_TOUCH = 42;
        this.priorityKey = lastUsedIndex * howOftenUsed / MAGIC_TOUCH;
    }

    private Phrase(String phrase, String largonjiPhrase) {
        this.phrase = phrase;
        this.largonjiPhrase = largonjiPhrase;
        this.howManyTimesUsed = 1;
        this.lastTimeUsed = new Date();
        this.length = phrase.length();
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
