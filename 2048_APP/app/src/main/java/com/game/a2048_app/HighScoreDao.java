package com.game.a2048_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HighScoreDao extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HighScoreDB";
    private static final String TABLE_NAME = "HighScore";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HIGH_SCORE = "highScore";
    private static final String GET_HIGH_SCORE_QUERY = "SELECT MAX(" + COLUMN_HIGH_SCORE + ") FROM " + TABLE_NAME;

    public HighScoreDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_HIGH_SCORE + " INTEGER )";
        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void addHighScore(int highScore) {
        if (checkIfItIsHighScore(highScore)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_HIGH_SCORE, highScore);
            db.insert(TABLE_NAME, null, values);
            String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_HIGH_SCORE + " NOT LIKE " + highScore;
            Cursor cursor = db.rawQuery(query, null);
            db.close();
        }
    }

    public int getHighScore() {
        int highScore = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(GET_HIGH_SCORE_QUERY, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isNull(0)) {
                highScore = Integer.parseInt(cursor.getString(0));
            }
        }

        return highScore;
    }

    private boolean checkIfItIsHighScore(int newHighScore) {
        int actualHighScore = 0;
        actualHighScore = getHighScore();
        return actualHighScore <= newHighScore;
    }

    public void deleteHighScore(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

}