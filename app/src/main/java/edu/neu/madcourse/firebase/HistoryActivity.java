package edu.neu.madcourse.firebase;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    private String img_name = "";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        TextView textView = findViewById(R.id.textView4);
        Map<String, Integer> sendHistory = (Map<String, Integer>) getIntent().getSerializableExtra("map");
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sendHistory.entrySet()) {
            stringBuilder.append("Send to ")
                    .append(entry.getKey()).append(": ")
                    .append(convertImg(entry.getValue()))
                    .append("\n");
        }
        textView.setText(stringBuilder);

    }

    private String convertImg(Integer value) {
        if (value == R.drawable.img1) {
            img_name = "emoji_1";
        } else if (value == R.drawable.img2) {
            img_name = "emoji_2";
        } else if (value == R.drawable.img3) {
            img_name = "emoji_3";
        } else if (value == R.drawable.img3) {
            img_name = "emoji_4";
        }
        return img_name;
    }
}