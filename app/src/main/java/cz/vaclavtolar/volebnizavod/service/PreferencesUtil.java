package cz.vaclavtolar.volebnizavod.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import cz.vaclavtolar.volebnizavod.R;
import cz.vaclavtolar.volebnizavod.dto.CachedElectionData;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesUtil {

    private static final String ELECTIONS_KEY = "elections";

    private PreferencesUtil() {
    }

    public static CachedElectionData getDataFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(ELECTIONS_KEY, null);
        if (json != null) {
            return gson.fromJson(json, CachedElectionData.class);
        }
        return null;
    }

    public static void storeDataToPreferences(Context context, CachedElectionData cachedElectionData) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();

        String settingsJson = gson.toJson(cachedElectionData);
        prefsEditor.putString(ELECTIONS_KEY, settingsJson);

        prefsEditor.commit();
    }
}
