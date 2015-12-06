package com.mohammedx.cart.android.client;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "adsDatabase.db";
    public static final String TABLE_NAME = "ads";
    public static final int DATABASE_VERSION = 1;
    public static final String UID = "_id";
    public static final String TITLE = "Title";
    public static final String DESCRIPTION = "Description";
    public static final String UUID = "uuid";
    public static final String MAJOR = "Major";
    public static final String MINOR = "Minor";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE + " VARCHAR," + DESCRIPTION + " VARCHAR," + UUID + " VARCHAR," + MAJOR + " VARCHAR," + MINOR + " VARCHAR);";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS" + TABLE_NAME;
    public Context context;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e) {

        }
    }
}
