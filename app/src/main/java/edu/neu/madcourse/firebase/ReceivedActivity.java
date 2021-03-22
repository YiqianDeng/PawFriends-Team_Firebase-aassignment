package edu.neu.madcourse.firebase;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

public class ReceivedActivity extends AppCompatActivity {

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_received);
        String image = getIntent().getStringExtra("sticker");
        ImageView receivedImageView = findViewById(R.id.receivedImageView);
        receivedImageView.setImageDrawable(getDrawable(Integer.parseInt(image)));
    }
}