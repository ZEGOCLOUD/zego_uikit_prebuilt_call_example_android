package com.zegocloud.uikit.prebuilt.groupcallexample;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextInputLayout textInputLayout = findViewById(R.id.text_input_layout);
        findViewById(R.id.join_btn).setOnClickListener(v -> {
            String callID = textInputLayout.getEditText().getText().toString();
            if (callID.isEmpty()) {
                textInputLayout.setError("please input conferenceID");
                return;
            }
            textInputLayout.setError("");
            Intent intent = new Intent(MainActivity.this, CallActivity.class);
            intent.putExtra("callID", callID);
            startActivity(intent);
        });

        textInputLayout.getEditText().setText("call_id");
    }
}