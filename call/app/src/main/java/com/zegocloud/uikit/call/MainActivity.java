package com.zegocloud.uikit.call;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start_call).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CallActivity.class);
            startActivity(intent);
        });
    }
}