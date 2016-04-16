package com.benzino.facebookphotos;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


/**
 * Created on 15/4/16.
 *
 * @author Anas
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize Facebook Sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }


}
