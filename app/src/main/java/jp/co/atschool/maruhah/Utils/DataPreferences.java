package jp.co.atschool.maruhah.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nttr on 2018/02/05.
 */

public class DataPreferences {
    private static final String TWITTER_ID = "twitter_id";

    public static String getTwitterId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(TWITTER_ID, "");
    }

    public static void setTwitterId(Context context, String uuid) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TWITTER_ID, uuid);
        editor.commit();
    }
}
