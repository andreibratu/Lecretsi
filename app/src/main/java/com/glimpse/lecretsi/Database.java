package com.glimpse.lecretsi;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.*;
import android.provider.BaseColumns;

import static com.glimpse.lecretsi.Database.PriorityQueue.HOW_MANY_TIMES_USED;
import static com.glimpse.lecretsi.Database.PriorityQueue.LAST_TIME_USED;
import static com.glimpse.lecretsi.Database.PriorityQueue.LENGTH;
import static com.glimpse.lecretsi.Database.PriorityQueue.PHRASE;
import static com.glimpse.lecretsi.Database.PriorityQueue.PRIORITY_KEY;

public final class Database {

    //TODO https://developer.android.com/training/basics/data-storage/databases.html#WriteDbRow

    Date parseStringToDate(String input) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return iso8601Format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    } ///DB holds date as String

    private Database() {}

    public class Element {
        private String phrase;
        private long priorityKey;
        private int howManyTimesUsed;
        private String lastTimeUsed;
        private int length;

        Element(String phrase) {
            this.phrase = phrase;
            this.howManyTimesUsed = 1;
            this.lastTimeUsed = new Date().toString();
            this.length = phrase.length();
            calculateKey();
        }

        void calculateKey() {
            Date timeNow = new Date();
            Date lastTimeUsed = parseStringToDate(this.lastTimeUsed);
            long lastUsedIndex = 1 / (timeNow.getTime() - lastTimeUsed.getTime());
            long howOftenUsed = this.howManyTimesUsed * this.length; // Longer more often used words have priority

            int MAGIC_TOUCH = 42;
            this.priorityKey = lastUsedIndex * howOftenUsed / MAGIC_TOUCH;
        }
    }

    public static class PriorityQueue implements BaseColumns {
        public static final String TABLE_NAME = "elements";
        public static final String PHRASE = "phrase";
        public static final String HOW_MANY_TIMES_USED = "how_many_times_used";
        public static final String LAST_TIME_USED = "last_time_used";
        public static final String PRIORITY_KEY = "priority_key";
        public static final String LENGTH = "length";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PriorityQueue.TABLE_NAME + " (" +
                        PriorityQueue._ID + " INTEGER PRIMARY KEY," +
                        PriorityQueue.PHRASE + " TEXT," +
                        PriorityQueue.LENGTH + " INTEGER," +
                        PriorityQueue.HOW_MANY_TIMES_USED + " LONG," +
                        PriorityQueue.LAST_TIME_USED + " STRING," +
                        PriorityQueue.PRIORITY_KEY + " LONG)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + PriorityQueue.TABLE_NAME;
    }

    public class PriorityQueueHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Database.db";

        public PriorityQueueHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PriorityQueue.SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(PriorityQueue.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }


    void addPhraseIntoDB(String phrase) {
        //TODO @Adi add asynctask for getWritableDatabase() & getReadableDatabase()
        //SQLiteDatabase db = PriorityQueueHelper.getWritableDatabase();

        Element newElement = new Element(phrase);
        ContentValues newRow = new ContentValues();

        newRow.put( PHRASE, newElement.phrase );
        newRow.put( HOW_MANY_TIMES_USED, newElement.howManyTimesUsed );
        newRow.put( LAST_TIME_USED, newElement.lastTimeUsed );
        newRow.put( PRIORITY_KEY, newElement.priorityKey );
        newRow.put( LENGTH, newElement.phrase.length() );
    }

    //TODO UpdateDB
    //TODO QueryDB
}
