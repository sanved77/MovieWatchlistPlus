package com.sanved.moviewatchlistplus2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Sanved on 07-08-2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "moviedb";
    private static final String TABLE_NAME = "movies";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LINK = "link";
    private static final String KEY_YEAR = "year";
    private static final String KEY_IMDB = "imdb";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_RATEIMDB = "rateimdb";
    private static final String KEY_RATETOMA = "ratetoma";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_PLOT = "plot";
    private static final String KEY_DIR = "dir";
    private static final String KEY_STAR = "star";
    private static final String KEY_SEASONS = "seasons";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "
                + TABLE_NAME
                + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, " +
                "link VARCHAR, " +
                "year VARCHAR, " +
                "imdb VARCHAR," +
                "duration VARCHAR," +
                "rateimdb VARCHAR," +
                "ratetoma VARCHAR," +
                "genre VARCHAR," +
                "plot VARCHAR," +
                "dir VARCHAR," +
                "star VARCHAR," +
                "seasons VARCHAR)");
    }

    /*  0 - id
    *   1 - name
    *   2 - link
    *   3 - year
    *   4 - imdb
    *   5 - duration
    *   6 - rateimdb
    *   7 - ratetoma
    *   8 - genre
    *   9 - plot
    *   10 - dir
    *   11 - stars
    *   12 - seasons
    */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    // Used to add new words
    public void addMovie(MovieData md) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, md.getName());
        values.put(KEY_LINK, md.getLink());
        values.put(KEY_YEAR, md.getYear());
        values.put(KEY_IMDB, md.getImdb());
        values.put(KEY_DURATION, md.getDuration());
        values.put(KEY_RATEIMDB, md.getRateimdb());
        values.put(KEY_RATETOMA, md.getRatetoma());
        values.put(KEY_GENRE, md.getGenre());
        values.put(KEY_PLOT, md.getPlot());
        values.put(KEY_DIR, md.getDirector());
        values.put(KEY_STAR, md.getStars());
        values.put(KEY_SEASONS, md.getSeasons());

        // insert
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table " + TABLE_NAME);
    }

    // Get aa ArrayList of all words
    public ArrayList<MovieData> allMovies() {

        ArrayList<MovieData> wordDataList = new ArrayList<MovieData>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MovieData word = null;

        if (cursor.moveToFirst()) {
            do {
                word = new MovieData(
                        cursor.getString(2),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12));

                wordDataList.add(word);
            } while (cursor.moveToNext());
        }

        return wordDataList;
    }


    // Written here is a method use to delete every word/row.
    // Use it for testing purposes. Not for release.
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }


    // Searching a word by giving a similar term
    public MovieData getMovieById(String term) {

        String query = "SELECT  * FROM " + TABLE_NAME + " WHERE imdb LIKE '" + term + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MovieData word = null;

        if (cursor.moveToFirst()) {

            word = new MovieData(
                    cursor.getString(2),
                    cursor.getString(1),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12));

        }

        return word;
    }


    // Gives an ArrayList with all the words starting from the alphabet which is sent as a parameter.
    public ArrayList alphaWords(String s) {

        ArrayList<MovieData> wordDataList = new ArrayList<MovieData>();
        String query = "SELECT  * FROM " + TABLE_NAME + " WHERE name LIKE '" + s + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MovieData word = null;

        if (cursor.moveToFirst()) {
            do {
                word = new MovieData(
                        cursor.getString(2),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12));

                wordDataList.add(word);
            } while (cursor.moveToNext());
        }

        return wordDataList;
    }


    // Delete a given word
    public void deleteMovie(String term) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where imdb like '" + term + "'");
    }


    // Get the primary ID of any word
    public int getId(String term) {

        int id = 99999;
        String query = "SELECT  * FROM " + TABLE_NAME + " WHERE name LIKE '" + term + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return id;
    }


    // Method to search a word by giving it's ID
    public MovieData searchWord(int id) {

        String query = "SELECT  * FROM " + TABLE_NAME + " WHERE id=" + id + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MovieData word = null;

        if (cursor.moveToFirst()) {
            do {
                word = new MovieData(
                        cursor.getString(2),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12));

            } while (cursor.moveToNext());
        }

        return word;
    }

}
