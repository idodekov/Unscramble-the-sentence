package org.idodekov.unscramblethesentence.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.idodekov.unscramblethesentence.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Iliyan on 28.4.2016 г..
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UnscrambleTheSentence";

    public static final String TABLE_CATEGORIES = "Categories";
    public static final String TABLE_SENTENCES = "Sentences";

    public static final String COLUMN_SENTENCE = "sentence";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLAYED = "played";
    public static final String COLUMN_CATEGORY_ID = "categoryId";
    public static final String COLUMN_NAME = "name";

    public static final int DATABASE_VERSION = 1;

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Database creation sql statement
    private static final String DATABASE_CREATE_CATEGORIES = "create table " + TABLE_CATEGORIES +
            "( "+ COLUMN_ID +" integer primary key, " + COLUMN_NAME +" text not null);";
    private static final String DATABASE_CREATE_SENTENCES = "create table " + TABLE_SENTENCES +
            "( "+ COLUMN_ID +" integer primary key, " + COLUMN_CATEGORY_ID +" integer not null, "+COLUMN_PLAYED +" integer not null, "+ COLUMN_SENTENCE +" text not null);";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(DatabaseHelper.class.toString(), "Preparing to setup app database...");

        sqLiteDatabase.execSQL(DATABASE_CREATE_CATEGORIES);
        sqLiteDatabase.execSQL(DATABASE_CREATE_SENTENCES);

        importCategories(sqLiteDatabase, R.raw.sentence_import);
    }

    public void importCategories(SQLiteDatabase database, int rawResourceId) {
        InputStream is = null;

        try {
            is = context.getResources().openRawResource(rawResourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }

            JSONArray json = new JSONArray(total.toString());

            for (int i=0; i < json.length(); i++) {
                JSONObject categoryObject = json.getJSONObject(i);
                String categoryName = categoryObject.getString("Name");

                ContentValues categoryValues = new ContentValues();
                categoryValues.put("name", categoryName);
                long categoryId = database.insert("Categories", null, categoryValues);

                JSONArray sentences = categoryObject.getJSONArray("Sentences");
                for (int j=0; j < sentences.length(); j++) {
                    String sentence = sentences.getString(j);
                    ContentValues sentenceValues = new ContentValues();
                    sentenceValues.put("categoryId", categoryId);
                    sentenceValues.put("played", 0);
                    sentenceValues.put("sentence", sentence);
                    database.insert("Sentences", null, sentenceValues);
                    Log.i(DatabaseHelper.class.toString(), "Importing the following sentence: " + sentence);
                }

            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
