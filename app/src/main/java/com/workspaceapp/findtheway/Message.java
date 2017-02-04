package com.workspaceapp.findtheway;

/**
 * Created by Sebastian on 04.02.2017.
 */

public class Message {
    private String sender;
    private boolean received;
    private String timestamp;
    private String body;

    public Message (String sender, boolean received, String timestamp, String body)
    {
        this.sender = sender;
        this.received = received;
        this.timestamp = timestamp;
        this.body = body;
    }

    public Message()
    {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean getReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
