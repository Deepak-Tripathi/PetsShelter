package com.example.deepak.petshelter.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.deepak.petshelter.Data.PetContract.PetsEntry;

public class PetProvider extends ContentProvider {

    private PetDbHelper mDbHelper;

    private SQLiteDatabase database;

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(PetsEntry.CONTENT_AUTHORITY ,PetsEntry.PATH_PETS,PETS);
        sUriMatcher.addURI(PetsEntry.CONTENT_AUTHORITY,PetsEntry.PATH_PETS+"/#",PET_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        mDbHelper = new PetDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.

                cursor = database.query(PetsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowDeleted = 0;

        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowDeleted= database.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowDeleted = database.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }

        if(rowDeleted!=0) getContext().getContentResolver().notifyChange(uri, null);

        return rowDeleted;
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues contentValues) {

        String name=contentValues.getAsString(PetsEntry.COLUMN_PET_NAME);
        Integer gender=contentValues.getAsInteger(PetsEntry.COLUMN_PET_GENDER);
        Integer weight=contentValues.getAsInteger(PetsEntry.COLUMN_PET_WEIGHT);

        if((TextUtils.isEmpty(name)))
        {
            Toast.makeText(getContext(),"Enter name " , Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Pet requires a name");
        }

        if(gender==null || !PetsEntry.isValidGender(gender))
        {
            Toast.makeText(getContext()," Invalid Gender " , Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Pet requires a name");
        }

        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }



        database = mDbHelper.getWritableDatabase();

        long newRowId= database.insert(PetsEntry.TABLE_NAME, null, contentValues);

        getContext().getContentResolver().notifyChange(uri,null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.size() == 0) {
            return 0;
        }

        if(contentValues.containsKey(PetsEntry.COLUMN_PET_NAME))
        {
            String name=contentValues.getAsString(PetsEntry.COLUMN_PET_NAME);
            if((TextUtils.isEmpty(name)))
            {
                Toast.makeText(getContext(),"Enter name " , Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (contentValues.containsKey(PetsEntry.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetsEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetsEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        if (contentValues.containsKey(PetsEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = contentValues.getAsInteger(PetsEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowupdated= database.update(PetsEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if(rowupdated!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowupdated;
    }


}
