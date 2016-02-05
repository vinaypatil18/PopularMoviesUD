package com.vinay.passioneer.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * this class will contain common methods and variables
 */
public class Util {

    private static final int DESIRED_WIDTH = 300;
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BACKDROP_IMAGE_URL = "http://image.tmdb.org/t/p/w342";
    /**
     * calculate the screen width and image/grid width
     * @param context
     * @return imageWidth
     */
    public static int getImageWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int optimalColumnCount = Math.round(screenWidth / DESIRED_WIDTH);
        int imageWidth = screenWidth / optimalColumnCount;
        return imageWidth;
    }

    public static String getPreferredSortByOption(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sortBy_key),
                context.getString(R.string.pref_sortBy_popular_value));
    }
}
