package org.idodekov.unscramblethesentence.library.sentence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import org.idodekov.unscramblethesentence.persistance.DatabaseHelper;

/**
 * Created by Iliyan on 25.4.2016 г..
 */
public class SentenceManager {
    private DatabaseHelper dbHelper;

    public static final String INTENT_PARAM_FULL_SENTENCE = "INTENT_PARAM_FULL_SENTENCE";
    public static final String INTENT_PARAM_RESULT = "INTENT_PARAM_RESULT";

    public static final int INTENT_PARAM_RESULT_FLAG_NOT_PLAYED = 0;
    public static final int INTENT_PARAM_RESULT_FLAG_SUCCESS = 1;
    public static final int INTENT_PARAM_RESULT_FLAG_FAILURE = -1;

    public SentenceManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public Sentence getNextSentence() {
        Sentence sentence = null;
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] cols = new String[] {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_SENTENCE};
        String filter = DatabaseHelper.COLUMN_PLAYED + " = 0";

        Cursor cursor = database.query(false, DatabaseHelper.TABLE_SENTENCES, cols, filter, null, null, null, null, "1");
        if (cursor.moveToFirst()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String sentenceText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SENTENCE));

            sentence = new Sentence(sentenceText, itemId);
        }

        database.close();

        return sentence;
    }

    public boolean hasMoreSentences() {
        Sentence sentence = getNextSentence();
        return sentence != null;
    }

    public long getSentenceCount(Integer playedCode) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        long result = 0;

        if(playedCode != null) {
            String filter = DatabaseHelper.COLUMN_PLAYED + " = " + playedCode;
            result = DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_SENTENCES, filter, null);
        } else {
            result = DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_SENTENCES, null, null);
        }

        return result;
    }

    public void markSentenceAsPlayed(Sentence sentence, int successFlag) {
        long id = sentence.getId();
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseHelper.COLUMN_PLAYED, successFlag);

        int rowsAffected = database.update(DatabaseHelper.TABLE_SENTENCES, updateValues, DatabaseHelper.COLUMN_ID + "=" + id, null);
        Log.d(SentenceManager.class.toString(), "Sentence with id " + id + " marked as played. Rows affected: " + rowsAffected);
        database.close();
    }
}
