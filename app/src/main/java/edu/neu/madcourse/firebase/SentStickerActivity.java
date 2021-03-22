package edu.neu.madcourse.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class SentStickerActivity extends AppCompatActivity {
    private int selectedSticker = 0;

    private String SERVER_KEY;
    private String username;
    private ArrayList<User> users;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_sticker);

        SERVER_KEY = getIntent().getStringExtra("SERVER_KEY");
        username = getIntent().getStringExtra("username");
        database = (DatabaseReference) getIntent().getSerializableExtra("database");
        users = (ArrayList<User>) getIntent().getSerializableExtra("users");

        Button btn_sent = findViewById(R.id.btn_sent);

        btn_sent.setOnClickListener(v -> {
            if (selectedSticker == 0) {
                new AlertDialog.Builder(this).setMessage("Please select an image").show();
            } else {
                //update the send count in database
                updateCount(database);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (User user : users) {
                            sendMessageToSpecUser(user.CLIENT_REGISTRATION_TOKEN);
                        }
                    }
                }).start();
            }
        });
    }

    // before sent image
    private void updateCount(DatabaseReference database) {
        database.child("users").child(username).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }
                user.sentCount ++;
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                //N/A
            }


        });
    }

    // select image
    @SuppressLint("NonConstantResourceId")
    public void clickImg(View view) {
        switch (view.getId()) {
            case R.id.img1:
                selectedSticker = R.drawable.img1;
                break;
            case R.id.img2:
                selectedSticker = R.drawable.img2;
                break;
            case R.id.img3:
                selectedSticker = R.drawable.img3;
                break;
            case R.id.img4:
                selectedSticker = R.drawable.img4;
                break;
        }
    }

    // sent messsage
    public void sendMessageToSpecUser(String userToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();

        try {
            jNotification.put("title", "A new sticker!");
            jNotification.put("body", "You received a new sticker from " + username);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("tag", "" + selectedSticker);

            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            jdata.put("title", "data title");
            jdata.put("content", "data content");
            jdata.put("image", "" + selectedSticker);


            // send to specific user
            jPayload.put("to", userToken);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);


            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Helper function.
     */
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}