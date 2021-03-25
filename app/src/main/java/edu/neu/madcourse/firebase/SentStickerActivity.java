package edu.neu.madcourse.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class SentStickerActivity extends AppCompatActivity {

    private User user;
    private String SERVER_KEY;
    private String username;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> active_user_list = new ArrayList<>();
    private int selectedSticker = 0;
    private String selectedUserName;
    private String CLIENT_REGISTRATION_TOKEN;
    private Map<String, Integer> sendHistory = new HashMap<>();
    //    private String userID = "";
    private final String TAG = "SentStickerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_sticker);

        // init
        SERVER_KEY = getIntent().getStringExtra("SERVER_KEY");
        username = getIntent().getStringExtra("username");
        selectedUserName = getIntent().getStringExtra("selectedUserName");
        CLIENT_REGISTRATION_TOKEN = getIntent().getStringExtra("CLIENT_REGISTRATION_TOKEN");



        Button btn_sent = findViewById(R.id.btn_sent);

        //go to history
        Button bttn_history = findViewById(R.id.bttn_history);
        bttn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SentStickerActivity.this, HistoryActivity.class);
                intent.putExtra("map", (Serializable) sendHistory);
                startActivity(intent);
            }
        });


        // database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                user = snapshot.getValue(User.class);
//                int i = 0;
//                Log.d(TAG, user.toString());
                assert user != null;
                if (!user.username.equals(username)) {
                    users.add(user);
                    active_user_list.add(user.username);
//                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                user = snapshot.getValue(User.class);
                if (Objects.requireNonNull(snapshot.getKey()).equalsIgnoreCase(username)) {
                    TextView textView = findViewById(R.id.selectEmoji);

                    //Display how many stickers a user has sent
                    textView.setText(
                            String.format("%s" + " has sent %s stickers!", user.username, user.sentCount)
                    );
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        TextView display = findViewById(R.id.selectEmoji);
//        display.setText("enter successful");

        // select image
        btn_sent.setOnClickListener(v -> {
            if (selectedSticker == 0) {
                new AlertDialog.Builder(this).setMessage("Please select an image").show();
            } else if (selectedUserName.equals("")) {
                new AlertDialog.Builder(this).setMessage("Please select an User").show();
            } else {
                // enable network connection check
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    if (isNetworkOnline()) {
                        //update the send count in database
                        updateCount(database);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (User user : users) {
                                    if (user.username.equals(selectedUserName)) {
                                        sendHistory.put(user.username, selectedSticker);

                                        sendMessageToSpecUser(user.CLIENT_REGISTRATION_TOKEN);
                                        selectedUserName = "";
                                    }
                                }
                            }
                        }).start();

                    } else {
                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                    }

                }
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

    // sent message
    public void sendMessageToSpecUser(String userToken) {
        JSONObject payload = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            notification.put("title", "A new sticker!");
            notification.put("body", "You received a new sticker from " + username);
            notification.put("sound", "default");
            notification.put("badge", "1");
            notification.put("tag", "" + selectedSticker);

            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            data.put("title", "data title");
            data.put("content", "data content");


            data.put("image", "" + selectedSticker);


            // send to specific user
            payload.put("to", userToken);
            payload.put("priority", "high");
            payload.put("notification", notification);
            payload.put("data", data);


            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Authorization", SERVER_KEY);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(payload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = httpURLConnection.getInputStream();
            final String resp = convertStreamToString(inputStream);
            int i =0 ;
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    // helper
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    public static boolean isNetworkOnline() {
        boolean isOnline = false;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 3000);
            // socket.connect(new InetSocketAddress("114.114.114.114", 53), 3000);
            isOnline = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isOnline;
    }
}