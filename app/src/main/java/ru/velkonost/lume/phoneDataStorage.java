package ru.velkonost.lume;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static ru.velkonost.lume.Constants.APP_PREFERENCES;

class PhoneDataStorage {

    private static SharedPreferences mSharedPreferences;

    public PhoneDataStorage() {
    }

    public static void saveText(Context context, String key, String value) {
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString(key, value);
        ed.commit();
    }

    public static String loadText(Context context, String key) {
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String savedText = mSharedPreferences.getString(key, "");
        return savedText;
    }

    public static void deleteText(Context context, String key) {
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString(key, "");
        ed.commit();
    }

}
