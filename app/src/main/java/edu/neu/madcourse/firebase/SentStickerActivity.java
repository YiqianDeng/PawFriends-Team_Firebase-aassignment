package edu.neu.madcourse.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class SentStickerActivity extends AppCompatActivity {

    private User user;
    private String SERVER_KEY;
    private String username;
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<String> active_user_list = new ArrayList<>();
    private int selectedSticker = 0;
    private String selectedUserName;
//    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_sticker);

        // init
        SERVER_KEY = getIntent().getStringExtra("SERVER_KEY");
        username = getIntent().getStringExtra("username");
        selectedUserName = getIntent().getStringExtra("selectedUserName");
        Button btn_sent = findViewById(R.id.btn_sent);


        // database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                user = snapshot.getValue(User.class);
//                assert user != null;
//                if (!user.username.equals(username)) {
//                    users.add(user);
//                    active_user_list.add(user.username);
//                    adapter.notifyDataSetChanged();
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                user = snapshot.getValue(User.class);
                if (Objects.requireNonNull(snapshot.getKey()).equalsIgnoreCase(username)) {
                    TextView textView = findViewById(R.id.textWindow);

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
                //update the send count in database
                updateCount(database);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (User user : users) {
                            if (user.username.equals(selectedUserName)) {
                                sendMessageToSpecUser(user.CLIENT_REGISTRATION_TOKEN);
                                selectedUserName = "";
                            }
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
                selectedSticker = R.drawable.img3;
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
            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "run: " + resp);
//                    Toast.makeText(FCMActivity.this,"response was: " + resp,Toast.LENGTH_LONG).show();
//                }
//            });

        } catch (JSONException | IOException e) {
//            Log.e(TAG,"sendMessageToNews threw error",e);
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