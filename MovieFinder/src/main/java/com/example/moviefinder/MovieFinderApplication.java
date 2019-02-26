package com.example.moviefinder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.example.moviefinder.Database.DatabaseHelper;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.fabric.sdk.android.Fabric;

public class MovieFinderApplication extends MultiDexApplication {

    Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        //Stetho
        Stetho.initializeWithDefaults(this);
        //Database Creation
//        DatabaseHelper.getsInstance(mContext);
        //Crashlytics
        Fabric.with(this, new Crashlytics());
        //Dbflow
        FlowManager.init(new FlowConfig.Builder(this).build());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
