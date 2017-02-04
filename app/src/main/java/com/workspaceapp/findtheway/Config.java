package com.workspaceapp.findtheway;

import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.messaging.MessageClient;

/**
 * Created by Sebastian on 28.01.2017.
 */

public class Config {
    SinchClient sinchClient;
    MessageClient messageClient;
    User user;
    String email;
    String username;
    String provider;
    String userID;
    private static Config instance = null;
    protected Config() {
        // Exists only to defeat instantiation.
    }
    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }
        return instance;
    }
}
