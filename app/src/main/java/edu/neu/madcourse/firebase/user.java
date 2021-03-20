package edu.neu.madcourse.firebase;

import java.util.ArrayList;

public class user {

    public String username;
    public String CLIENT_REGISTRATION_TOKEN;
    public Integer sentCount;
    public ArrayList<String> receivedHistory;

    public user() {}

    public user(String username, String CLIENT_REGISTRATION_TOKEN) {
        this.username = username;
        this.CLIENT_REGISTRATION_TOKEN = CLIENT_REGISTRATION_TOKEN;
        this.sentCount = 0;
        this.receivedHistory = new ArrayList<>();
    }
}
