package com.workspaceapp.findtheway;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String customdisplayname;
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

    public String getDisplayname(String userID)
    {

        FirebaseDatabase.getInstance().getReference().child(userID).child("displayname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                customdisplayname = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return customdisplayname;
    }
}
