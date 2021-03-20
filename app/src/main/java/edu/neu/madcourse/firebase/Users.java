package edu.neu.madcourse.firebase;

import java.util.ArrayList;

public class Users {

    //no password required
    public String username;
    //count the number of stickers the user has sent
    public Integer stickerCount;
    public String CLIENT_REGISTRATION_TOKEN;
    //history of stickers that user received
    public ArrayList<String> stickerReceived;

    public Users() {}

    public Users(String username, String CLIENT_REGISTRATION_TOKEN) {
        this.username = username;
        this.stickerCount = 0;
        this.CLIENT_REGISTRATION_TOKEN = CLIENT_REGISTRATION_TOKEN;
        this.stickerReceived = new ArrayList<>();
    }
}
