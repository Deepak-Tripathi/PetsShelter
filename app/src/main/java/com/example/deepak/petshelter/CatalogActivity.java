package com.example.deepak.petshelter;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.deepak.petshelter.Data.PetContract.PetsEntry;
import com.example.deepak.petshelter.Data.PetDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private PetDbHelper mDbHelper;
    private final int LOADER_ID=0;
    private  PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);

        View  emptyview = findViewById(R.id.empty_view);

        listView.setBackgroundResource(R.drawable.petshelter2);

        listView.setEmptyView(emptyview);

        mCursorAdapter = new PetCursorAdapter(this,null);

        listView.setAdapter(mCursorAdapter);

        android.app.LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID,null,this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this , EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(PetsEntry.CONTENT_URI ,id);

                intent.setData(currentPetUri);

                startActivity(intent);

            }
        });


    }

    protected void onStart() {
        super.onStart();

    }

    private void insertData() {
        ContentValues value = new ContentValues();

        value.put(PetsEntry.COLUMN_PET_NAME, "Tota");
        value.put(PetsEntry.COLUMN_PET_BREED, "Terrier");
        value.put(PetsEntry.COLUMN_PET_GENDER, PetsEntry.GENDER_MALE);
        value.put(PetsEntry.COLUMN_PET_WEIGHT, 7);

        //SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //db.insert(PetsEntry.TABLE_NAME, null, value);

        Uri newRowUri = getContentResolver().insert(PetsEntry.CONTENT_URI,value);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                deleteAllPets();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                PetsEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);

    }

    private void deleteAllPets()
    {
        int rowDeleted = getContentResolver().delete(PetsEntry.CONTENT_URI,null,null);

        Toast.makeText(this,R.string.all_pet_deleted,Toast.LENGTH_SHORT).show();
    }

}
