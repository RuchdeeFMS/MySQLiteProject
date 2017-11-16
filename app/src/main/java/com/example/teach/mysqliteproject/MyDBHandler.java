package com.example.teach.mysqliteproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by teach on 7/11/2560.
 */

public class MyDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "movieDB.db";
    public static final String TABLE_NAME = "movies";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_MOVIE_NAME = "movie_name";
    public static final String COLUMN_RELEASE_YEAR = "release_year";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_MOVIE_NAME + " TEXT, " +
                COLUMN_RELEASE_YEAR + " TEXT" +
                ")";
        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_MOVIE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DROP_MOVIE_TABLE);
        onCreate(db);
    }

    public ArrayList<String> getAllMovies() {
        ArrayList<String> movie_list = new ArrayList<String>();
        String SQL = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_MOVIE_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery(SQL, null);
        while(resultSet.moveToNext()) {
            movie_list.add(resultSet.getString(resultSet.getColumnIndex(COLUMN_MOVIE_NAME)) + " " +
            resultSet.getString(resultSet.getColumnIndex(COLUMN_RELEASE_YEAR)));
        }
        resultSet.close();
        db.close();
        return movie_list;
    }

    public ArrayList<Integer> getAllMoviesID() {
        ArrayList<Integer> movie_list_ID = new ArrayList<Integer>();
        String SQL = "SELECT " + COLUMN_MOVIE_ID + " FROM " + TABLE_NAME + " ORDER BY " + COLUMN_MOVIE_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery(SQL, null);
        while (resultSet.moveToNext()) {
            movie_list_ID.add(resultSet.getInt(resultSet.getColumnIndex(COLUMN_MOVIE_ID)));
        }
        resultSet.close();
        db.close();
        return movie_list_ID;
    }

    public void addNewMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(COLUMN_MOVIE_NAME, movie.get_movie_name());
        mContentValues.put(COLUMN_RELEASE_YEAR, movie.get_release_year());
        db.insert(TABLE_NAME, null, mContentValues);
        db.close();
    }

    public int getLastRowID() {
        int last_movie_id = 0;
        String SQL = "SELECT MAX(" + COLUMN_MOVIE_ID + ") FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery(SQL, null);
        if (resultSet.moveToLast()) {
            last_movie_id = resultSet.getInt(0);
        }
        resultSet.close();
        db.close();
        return last_movie_id;
    }

    public Cursor getMoviebyID(int id) {
        String SQL = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_MOVIE_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet = db.rawQuery(SQL, null);
        //db.close();
        return resultSet;
    }

    public void updateMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(COLUMN_MOVIE_NAME, movie.get_movie_name());
        mContentValues.put(COLUMN_RELEASE_YEAR, movie.get_release_year());
        db.update(TABLE_NAME, mContentValues, COLUMN_MOVIE_ID + " = ? ", new String[] {
                Integer.toString(movie.get_movie_id())
        });
        db.close();
    }

    public boolean deleteMovie(int mID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_MOVIE_ID + " = ? ", new String[] {
                Integer.toString(mID)
        });
        db.close();
        return true;
    }

}
