package cz.vaclavtolar.volebnizavod.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import cz.vaclavtolar.volebnizavod.R;
import cz.vaclavtolar.volebnizavod.dto.CachedElections;
import cz.vaclavtolar.volebnizavod.dto.CachedElectionsData;
import cz.vaclavtolar.volebnizavod.dto.CachedElectionsDistrictsData;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesUtil {

    private static final String ELECTIONS_KEY = "elections";
    private static final String ELECTIONS_DATA_KEY = "elections_data";
    private static final String ELECTIONS_DISTRICTS_DATA_KEY = "elections_districts_data";
    private static Gson gson;

    private PreferencesUtil() {
    }

    public static CachedElections getElectionsFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        Gson gson = getGson();
        String json = prefs.getString(ELECTIONS_KEY, null);
        if (json != null) {
            return gson.fromJson(json, CachedElections.class);
        }
        return null;
    }

    public static CachedElectionsData getElectionsDataFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        Gson gson = getGson();
        String json = prefs.getString(ELECTIONS_DATA_KEY, null);
        if (json != null) {
            return gson.fromJson(json, CachedElectionsData.class);
        }
        return null;
    }

    public static CachedElectionsDistrictsData getElectionsDistrictsDataFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        Gson gson = getGson();
        String json = prefs.getString(ELECTIONS_DISTRICTS_DATA_KEY, null);
        if (json != null) {
            return gson.fromJson(json, CachedElectionsDistrictsData.class);
        }
        return null;
    }

    public static void storeElectionsToPreferences(Context context, CachedElections cachedElectionData) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = getGson();
        String settingsJson = gson.toJson(cachedElectionData);
        prefsEditor.putString(ELECTIONS_KEY, settingsJson);

        prefsEditor.apply();
    }

    public static void storeElectionsDataToPreferences(Context context, CachedElectionsData cachedElectionData) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = getGson();

        String settingsJson = gson.toJson(cachedElectionData);
        prefsEditor.putString(ELECTIONS_DATA_KEY, settingsJson);

        prefsEditor.apply();
    }

    public static void storeElectionsDistrictsDataToPreferences(Context context, CachedElectionsDistrictsData cachedElectionsDistrictsData) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = getGson();

        String settingsJson = gson.toJson(cachedElectionsDistrictsData);
        prefsEditor.putString(ELECTIONS_DISTRICTS_DATA_KEY, settingsJson);

        prefsEditor.apply();
    }

    @NotNull
    private static Gson getGson() {
        if (gson == null) {
            gson = Converters.registerAll(new GsonBuilder()).create();
        }
        return gson;
    }
}
