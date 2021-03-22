package edu.neu.madcourse.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class chooseRecipientActivity extends AppCompatActivity {

    private User user;
    private String username;
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<String> usernameList = new ArrayList<>();;
    private ArrayAdapter<String> adapter;


    private String SERVER_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_recipient);

        SERVER_KEY = getIntent().getStringExtra("SERVER_KEY");
        username = getIntent().getStringExtra("username");

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                usernameList);

        ListView usersListView = findViewById(R.id.usersListView);
        usersListView.setAdapter(adapter);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Button bttn_send_img = findViewById(R.id.bttn_send_img);

        database.child("users").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                user = snapshot.getValue(User.class);
                assert user != null;
                if (!user.username.equals(username)) {
                    users.add(user);
                    usernameList.add(user.username);
                    adapter.notifyDataSetChanged();
                }
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

        Intent stickerActivity = new Intent(getApplicationContext(), StickerSendActivity.class);
        //add more data to intent
        stickerActivity.putExtra("SERVER_KEY", SERVER_KEY);
        stickerActivity.putExtra("username", username);
        stickerActivity.putExtra("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);

        if(username.equals("")) {
            new AlertDialog.Builder(this).setMessage("Please enter a username to login!").show();
        }
        else {
            startActivity(stickerActivity);
        }
    }
}