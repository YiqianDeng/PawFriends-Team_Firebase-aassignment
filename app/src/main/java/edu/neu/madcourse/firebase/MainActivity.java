package edu.neu.madcourse.firebase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {


    //server key from firebase console
    private static final String SERVER_KEY = "key=AAAAejI3rJU:APA91bFnp3D9yh58tw5nCYk66Wi1AFF7rdMVMj6WmF-r-VQ-LFq6If3sLW84jw5Uz4b5oM4o4R48qiPaB-L8R8JYx7rQS2Tv7n_waoxVCFgxnKrUiKryRMQ0uortWJfHVb06Tl5tO_ET";
    private DatabaseReference database;

    private static String username;
    private static String CLIENT_REGISTRATION_TOKEN;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.input_username);
        Button bttn_go = findViewById(R.id.bttn_go);

        //FirebaseInstanceId -> get instanceId -> get token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                CLIENT_REGISTRATION_TOKEN = instanceIdResult.getToken();
                database = FirebaseDatabase.getInstance().getReference();
            }
        });

        bttn_go.setOnClickListener(v -> {
            username = editText.getText().toString();

            MainActivity.this.createUser(
                    editText.getText().toString(), CLIENT_REGISTRATION_TOKEN
            );

            Intent stickerActivity = new Intent(getApplicationContext(), UserListActivity.class);
            //add more data to intent
            stickerActivity.putExtra("SERVER_KEY", SERVER_KEY);
            stickerActivity.putExtra("username", username);
            stickerActivity.putExtra("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);


            //TODO: login by history
            if(username.equals("")) {
                new AlertDialog.Builder(this).setMessage("Please enter a username to login!").show();
            }
            else {
                startActivity(stickerActivity);
            }
        });

    }


    private void createUser(String username, String token) {
        //create user by username and token and store in db
        User user = new User(username, token);
        database.child("users").child(username).setValue(user);
    }

}