package com.zegocloud.uikit.callwithofflineinvitation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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

        MMKV.initialize(this);

        TextInputLayout userIDInput = findViewById(R.id.user_id);
        TextInputLayout userNameInput = findViewById(R.id.user_name);
        Button userLogin = findViewById(R.id.user_login);

        if (MMKV.defaultMMKV().contains("user_id")) {
            String previousStoredUserID = MMKV.defaultMMKV().getString("user_id", "");
            String previousStoredUserName = MMKV.defaultMMKV().getString("user_name", "");

            userIDInput.getEditText().setText(previousStoredUserID);
            userNameInput.getEditText().setText(previousStoredUserName);

            signIn(previousStoredUserID, previousStoredUserName);
        }

        userLogin.setOnClickListener(v -> {
            String userID = userIDInput.getEditText().getText().toString();
            String userName = userNameInput.getEditText().getText().toString();

            signIn(userID, userName);
        });
    }


    private void signIn(String userID, String userName) {
        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(userName)) {
            return;
        }
        CircularProgressIndicator progress = findViewById(R.id.progress_circular);
        progress.setVisibility(View.VISIBLE);
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