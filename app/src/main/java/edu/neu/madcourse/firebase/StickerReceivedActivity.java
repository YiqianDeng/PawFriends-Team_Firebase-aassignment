package edu.neu.madcourse.firebase;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

public class StickerReceivedActivity extends AppCompatActivity {

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_received);
        ImageView receivedImageView = findViewById(R.id.receivedImageView);

        String image = getIntent().getStringExtra("sticker");
        receivedImageView.setImageDrawable(getDrawable(Integer.parseInt(image)));
    }
}