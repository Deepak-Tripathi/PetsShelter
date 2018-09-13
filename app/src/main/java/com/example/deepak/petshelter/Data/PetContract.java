package com.example.deepak.petshelter.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public final class PetContract {

    public static abstract class PetsEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Pets";

        public static final String _ID = "_id";
        public static final String COLUMN_PET_NAME = "Name";
        public static final String COLUMN_PET_BREED = "Breed";
        public static final String COLUMN_PET_GENDER = "Gender";
        public static final String COLUMN_PET_WEIGHT = "Weight";

        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

        public static final String CONTENT_AUTHORITY = "com.example.deepak.petshelter";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_PETS = "pets";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;


    }

}
