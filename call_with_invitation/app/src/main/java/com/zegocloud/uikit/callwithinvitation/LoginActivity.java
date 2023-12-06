package com.zegocloud.uikit.callwithinvitation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.tencent.mmkv.MMKV;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputLayout userIDInput = findViewById(R.id.user_id);
        TextInputLayout userNameInput = findViewById(R.id.user_name);
        Button userLogin = findViewById(R.id.user_login);
        userIDInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userNameInput.getEditText().setText(s + "_" + Build.MANUFACTURER.toLowerCase());
            }
        });

        userIDInput.getEditText().setText(Build.MANUFACTURER.toLowerCase());

        userLogin.setOnClickListener(v -> {
            String userID = userIDInput.getEditText().getText().toString();
            String userName = userNameInput.getEditText().getText().toString();

            signIn(userID, userName);
        });
    }

    private void signIn(String userID, String userName) {
        CircularProgressIndicator progress = findViewById(R.id.progress_circular);
        progress.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(userID) && !TextUtils.isEmpty(userName)) {
            Handler fakeLoginProcess = new Handler(Looper.getMainLooper());
            fakeLoginProcess.postDelayed((Runnable) () -> {
                progress.setVisibility(View.GONE);
                // fake login success
                MMKV.defaultMMKV().putString("user_id", userID);
                MMKV.defaultMMKV().putString("user_name", userName);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }, 1000);
        }
    }
}