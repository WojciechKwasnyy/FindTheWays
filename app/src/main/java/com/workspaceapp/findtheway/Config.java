package com.workspaceapp.findtheway;

/**
 * Created by Sebastian on 28.01.2017.
 */

public class Config {

    User user;
    String email;
    String username;
    String provider;
    String userID;
    boolean isfriendlocationenabled = true;
    String connectedwith = "OqoD3d2anhbiep6u6SqmxE2aVbF2";
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
