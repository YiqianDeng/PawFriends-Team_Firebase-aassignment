package edu.neu.madcourse.firebase;


import java.util.ArrayList;

public class User {

    public String username;
    public String CLIENT_REGISTRATION_TOKEN;
    public Integer sentCount;
    public ArrayList<String> sendHistory;

    public User() {}

    public User(String username, String CLIENT_REGISTRATION_TOKEN) {
        this.username = username;
        this.CLIENT_REGISTRATION_TOKEN = CLIENT_REGISTRATION_TOKEN;
        this.sentCount = 0;
        this.sendHistory = new ArrayList<>();
    }
}
