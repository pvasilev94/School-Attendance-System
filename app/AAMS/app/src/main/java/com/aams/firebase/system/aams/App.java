package com.aams.firebase.system.aams;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bluvision.beeks.sdk.util.BeaconManager;

public class App extends Application  {

    public static final String TAG = "Application";
    public static final String LEVEL = "level";
    SharedPreferences sharedPreferences;
    private Context context;
    private BeaconManager mBeaconManager;

    @Override
    public void onCreate() {
        Log.d(TAG, "FUCKIT" +this.toString());
        super.onCreate();
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LEVEL, 2);

    }
    public Context getInstance() {
        return context;
    }


    public BeaconManager getBeaconManager() {
        return mBeaconManager;
    }

    public void setBeaconManager(BeaconManager beaconManager) {
        mBeaconManager = beaconManager;
    }
}
