package com.banyar.myrollcall_cumdy;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by banyar on 4/26/17.
 */

public class FirebaseDBPersistence extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
