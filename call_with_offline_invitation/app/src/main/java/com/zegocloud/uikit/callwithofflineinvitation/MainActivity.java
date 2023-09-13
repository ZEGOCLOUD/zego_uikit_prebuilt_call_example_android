package com.zegocloud.uikit.callwithofflineinvitation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ClickListener;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String userName;
    private SharedPreferences sp;

    private ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("offline", Context.MODE_PRIVATE);

        TextView yourUserID = findViewById(R.id.your_user_id);
        String userID = getUserId();

        yourUserID.setText("Your User ID :" + userID);

        initCallInviteService(userID);

        initVoiceButton();

        initVideoButton();

    }


    private void initVideoButton() {
        ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.new_video_call);
        newVideoCall.setIsVideoCall(true);
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVideoCall.setResourceID("zegouikit_call");
        newVideoCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVideoCall.setInvitees(users);
        });
    }

    private void initVoiceButton() {
        ZegoSendCallInvitationButton newVoiceCall = findViewById(R.id.new_voice_call);
        newVoiceCall.setIsVideoCall(false);
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVoiceCall.setResourceID("zegouikit_call");
        newVoiceCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVoiceCall.setInvitees(users);
        });

        newVoiceCall.setOnClickListener(new ClickListener() {
            @Override
            public void onClick(int errorCode, String errorMessage, List<ZegoCallUser> errorInvitees) {

            }
        });
    }

    public void initCallInviteService(String generateUserID) {
        long appID = ;
        String appSign = "";

        String userID = generateUserID;
        userName = generateUserID + "_" + Build.MANUFACTURER;

        callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        //This property needs to be set when you are building an Android app and when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //androidNotificationConfig.channelID must be the same as the FCM Channel ID in [ZEGOCLOUD Admin Console|_blank]https://console.zegocloud.com),
        // and the androidNotificationConfig.channelName can be an arbitrary value.
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "";
        notificationConfig.channelID = ;
        notificationConfig.channelName = "ZegoTestChannelName";
        callInvitationConfig.notificationConfig = notificationConfig;
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,
            callInvitationConfig);
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
        saveUserId(builder.toString());
        return builder.toString();
    }

    private void saveUserId(String userId) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("userId", userId);
        edit.apply();
    }

    private String getUserId() {
        String spValue = sp.getString("userId", "");
        if (spValue.isEmpty()) {
            return generateUserID();
        } else {
            return spValue;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

}