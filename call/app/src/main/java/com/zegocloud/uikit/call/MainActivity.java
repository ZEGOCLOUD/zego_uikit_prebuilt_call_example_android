package com.zegocloud.uikit.call;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long appID = ;
        String appSign = ;

        String userID = generateUserID();
        String userName = userID + "_Name";
        String callID = "test_call_id";

        findViewById(R.id.join_btn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CallActivity.class);
            intent.putExtra("appID", appID);
            intent.putExtra("appSign", appSign);
            intent.putExtra("userID", userID);
            intent.putExtra("userName", userName);
            intent.putExtra("callID", callID);
            startActivity(intent);
        });
    }

    private String generateUserID() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        while (builder.length() < 5) {
            int nextInt = random.nextInt(10);
            if (builder.length() == 0 && nextInt == 0) {
                continue;
            }
            builder.append(nextInt);
        }
        return builder.toString();
    }
}