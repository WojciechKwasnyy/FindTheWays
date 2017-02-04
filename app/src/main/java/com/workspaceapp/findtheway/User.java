package com.workspaceapp.findtheway;

/**
 * Created by Sebastian on 29.01.2017.
 */

public class User {
    private String userID;
    private String useremail;
    private String provider;
    private String displayname;

    User(String userID, String useremail, String provider, String displayname)
    {
        this.userID = userID;
        this.useremail = useremail;
        this.provider = provider;
        this.displayname = displayname;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }
}
