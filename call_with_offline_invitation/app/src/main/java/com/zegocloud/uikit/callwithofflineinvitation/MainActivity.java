package com.zegocloud.uikit.callwithofflineinvitation;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.tencent.mmkv.MMKV;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView yourUserID = findViewById(R.id.your_user_id);
        TextView yourUserName = findViewById(R.id.your_user_name);
        String userID = getIntent().getStringExtra("userID");
        String userName = getIntent().getStringExtra("userName");

        yourUserID.setText("Your User ID :" + userID);
        yourUserName.setText("Your User Name :" + userName);

        long appID = ;
        String appSign = ;


        initCallInviteService(appID, appSign, userID, userName);

        initVoiceButton();

        initVideoButton();

        findViewById(R.id.user_logout).setOnClickListener(v -> {
            Builder builder = new Builder(MainActivity.this);
            builder.setTitle("Sign Out");
            builder.setMessage("Are you sure to Sign Out?After Sign out you can't receive offline calls");
            builder.setNegativeButton(R.string.call_cancel, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(R.string.call_ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    signOut();
                    finish();
                }
            });
            builder.create().show();
        });

        PermissionX.init(this).permissions(permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason(new ExplainReasonCallback() {
                @Override
                public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                    String message = "We need your consent for the following permissions in order to use the offline call function properly";
                    scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                }
            }).request(new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                    @NonNull List<String> deniedList) {
                }
            });
    }

    private void signOut() {
        MMKV.defaultMMKV().remove("user_id");
        MMKV.defaultMMKV().remove("user_name");
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

    private void initVideoButton() {
        ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.new_video_call);
        newVideoCall.setIsVideoCall(true);

        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //        newVideoCall.setResourceID("zegouikit_call");
        newVideoCall.setResourceID("zego_data");

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
        //        newVoiceCall.setResourceID("zegouikit_call");
        newVoiceCall.setResourceID("zego_data");

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
    }

    public void initCallInviteService(long appID, String appSign, String userID, String userName) {

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,
            callInvitationConfig);
    }

    @Override
    public void onBackPressed() {
        Builder builder = new Builder(MainActivity.this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure to Sign Out?After Sign out you can't receive offline calls");
        builder.setNegativeButton(R.string.call_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.call_ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                signOut();
                finish();
            }
        });
        builder.create().show();
    }
}