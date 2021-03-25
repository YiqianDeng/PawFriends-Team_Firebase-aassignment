package edu.neu.madcourse.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //server key from firebase console
    private static final String SERVER_KEY = "key=AAAAejI3rJU:APA91bFfoP1QPWOKjRzN8r3OeLIZsNPitrT-EzAF5dBWW-RxsP4Tg3ypzPaN8dasnmFKt2lfU1I_JRsLvSSHTJ4EF0Fs1uDEAhbpl4edeHPdLzZHgXu45H3zJesdps3cuRsxMwZI6P56";
    private DatabaseReference database;

    private static final String PREFS_NAME = "preferences";
    private static final String PREF_UNAME = "Username";

    private static String username;
    protected static String CLIENT_REGISTRATION_TOKEN;

    private EditText editText;

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.input_username);
        Button bttn_go = findViewById(R.id.bttn_go);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                CLIENT_REGISTRATION_TOKEN = instanceIdResult.getToken();
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener( task -> {
                    if(!task.isSuccessful()) {
                        return;
                    }
                    if(null != task.getResult()) {
                        database = FirebaseDatabase.getInstance().getReference();
                    }
                });





        bttn_go.setOnClickListener(v -> {
            username = editText.getText().toString();

            //check whether username is exist, if exist continue, else create a new user
            database.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        MainActivity.this.createUser(
                                editText.getText().toString(), CLIENT_REGISTRATION_TOKEN
                        );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            Intent chooseActivity = new Intent(getApplicationContext(), chooseRecipientActivity.class);
            // pass data to next activity
            chooseActivity.putExtra("SERVER_KEY", SERVER_KEY);
            chooseActivity.putExtra("username", username);
            chooseActivity.putExtra("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);



            if(username.equals("")) {
                new AlertDialog.Builder(this).setMessage("Please enter a username to login!").show();
            }
            else {

                startActivity(chooseActivity);
            }

        });

    }


    private void createUser(String username, String token) {
        //create user by username and token and store in db
        User user = new User(username, token);
        database.child("users").child(username).setValue(user);
    }

    /**
     * Citation: https://stackoverflow.com/questions/21697172/android-how-to-save-user-name-and-password-after-the-app-is-closed
     */
    private void savePreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Edit and commit
        username = editText.getText().toString();
        editor.putString(PREF_UNAME, username);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get value
        String defaultUnameValue = "";
        username = settings.getString(PREF_UNAME, defaultUnameValue);
        editText.setText(username);
    }
}