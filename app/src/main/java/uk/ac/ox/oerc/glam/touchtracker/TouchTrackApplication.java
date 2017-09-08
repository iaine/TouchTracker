package uk.ac.ox.oerc.glam.touchtracker;

import android.app.Application;
import android.content.Context;

/**
 * Create the application context
 */

public class TouchTrackApplication extends Application {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
}
