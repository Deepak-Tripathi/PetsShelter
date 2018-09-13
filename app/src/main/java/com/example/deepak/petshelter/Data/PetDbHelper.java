package com.example.deepak.petshelter.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.deepak.petshelter.Data.PetContract.PetsEntry;

public class PetDbHelper extends SQLiteOpenHelper {

    public static final String DATABSE_NAME ="shelter.db";
    public static final int DATABASE_VERSION = 1;


    public PetDbHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // CREATE TABLE pets (_id INTEGER, name TEXT, breed TEXT, gender INTEGER, weight INTEGER);

           String SQL_CREATE_ENTRIES = "CREATE TABLE " + PetsEntry.TABLE_NAME +"("
                                        + PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                                        + PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL ,"
                                        + PetsEntry.COLUMN_PET_BREED + " TEXT ,"
                                        + PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL ,"
                                        + PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0 ) ; ";


        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String SQL_DELETE_ENTRIES = " DROP TABLE IF EXISTS " + PetsEntry.TABLE_NAME + ";" ;

        db.execSQL(SQL_DELETE_ENTRIES);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
