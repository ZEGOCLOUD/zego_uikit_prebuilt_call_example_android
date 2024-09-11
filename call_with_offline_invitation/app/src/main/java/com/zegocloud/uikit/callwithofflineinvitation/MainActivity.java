package com.zegocloud.uikit.callwithofflineinvitation;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputLayout;
import com.tencent.mmkv.MMKV;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode;
import com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules;
import com.zegocloud.uikit.internal.ZegoUIKitLanguage;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.call.core.invite.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener;
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener;
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener;
import com.zegocloud.uikit.prebuilt.call.event.ZegoCallEndReason;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoTranslationText;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> permissionRequest = registerForActivityResult(new RequestPermission(),
        result -> {
            if (result) {
            }
        });

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
            builder.setNegativeButton("Cancel", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("OK", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    signOut();
                    finish();
                }
            });
            builder.create().show();
        });

        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            permissionRequest.launch(permission.POST_NOTIFICATIONS);
        }
    }

    private void signOut() {
        MMKV.defaultMMKV().remove("user_id");
        MMKV.defaultMMKV().remove("user_name");
        ZegoUIKitPrebuiltCallService.unInit();
    }

    private void initVideoButton() {
        ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.new_video_call);
        newVideoCall.setIsVideoCall(true);

        //resourceID is used for offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //        newVideoCall.setResourceID("zegouikit_call");

        newVideoCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }

            // call setInvitees method before button to send a invitation
            newVideoCall.setInvitees(users);
        });
    }

    private void initVoiceButton() {
        ZegoSendCallInvitationButton newVoiceCall = findViewById(R.id.new_voice_call);
        newVoiceCall.setIsVideoCall(false);

        //resourceID is used for offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //        newVoiceCall.setResourceID("zegouikit_call");

        newVoiceCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            // call setInvitees method before button to send a invitation
            newVoiceCall.setInvitees(users);
        });
    }

    public void initCallInviteService(long appID, String appSign, String userID, String userName) {

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        // optional,set language,default is English
        callInvitationConfig.translationText = new ZegoTranslationText(ZegoUIKitLanguage.CHS);

        // optional
        customCallConfig(callInvitationConfig);

        // optional
        listenEvents();

        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName, callInvitationConfig);

    }

    private static void listenEvents() {
        ZegoUIKitPrebuiltCallService.events.setErrorEventsListener(new ErrorEventsListener() {
            @Override
            public void onError(int errorCode, String message) {
                Timber.d("onError() called with: errorCode = [" + errorCode + "], message = [" + message + "]");
            }
        });
        ZegoUIKitPrebuiltCallService.events.invitationEvents.setPluginConnectListener(
            new SignalPluginConnectListener() {
                @Override
                public void onSignalPluginConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event,
                    JSONObject extendedData) {
                    Timber.d(
                        "onSignalPluginConnectionStateChanged() called with: state = [" + state + "], event = [" + event
                            + "], extendedData = [" + extendedData + "]");
                }
            });

        ZegoUIKitPrebuiltCallService.events.callEvents.setCallEndListener(new CallEndListener() {
            @Override
            public void onCallEnd(ZegoCallEndReason callEndReason, String jsonObject) {
                Timber.d("onCallEnd() called with: callEndReason = [" + callEndReason + "], jsonObject = [" + jsonObject
                    + "]");
            }
        });
    }


    private static void customCallConfig(ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig) {
        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = ZegoUIKitPrebuiltCallInvitationConfig.generateDefaultConfig(
                    invitationData);
                //                customAvatarConfig(config);
                //                useGalleryConfig(config);
                //                customMenuBars(config);
                return config;
            }
        };
    }


    private void useGalleryConfig(ZegoUIKitPrebuiltCallConfig config) {
        ZegoLayoutGalleryConfig galleryConfig = new ZegoLayoutGalleryConfig();
        galleryConfig.removeViewWhenAudioVideoUnavailable = true;
        galleryConfig.showNewScreenSharingViewInFullscreenMode = true;
        galleryConfig.showScreenSharingFullscreenModeToggleButtonRules = ZegoShowFullscreenModeToggleButtonRules.SHOW_WHEN_SCREEN_PRESSED;
        config.layout = new ZegoLayout(ZegoLayoutMode.GALLERY, galleryConfig);

    }

    private void customMenuBars(ZegoUIKitPrebuiltCallConfig config) {
        config.bottomMenuBarConfig.buttons.add(ZegoMenuBarButtonName.CHAT_BUTTON);
        config.bottomMenuBarConfig.buttons.add(ZegoMenuBarButtonName.SHOW_MEMBER_LIST_BUTTON);

        config.bottomMenuBarConfig.buttonConfig.toggleCameraOnImage = ContextCompat.getDrawable(this,
            R.drawable.call_icon_dialog_video_accept);
        config.bottomMenuBarConfig.buttonConfig.toggleCameraOffImage = ContextCompat.getDrawable(this,
            R.drawable.call_icon_dialog_voice_accept);
    }

    @Override
    public void onBackPressed() {
        Builder builder = new Builder(MainActivity.this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure to Sign Out?After Sign out you can't receive offline calls");
        builder.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                signOut();
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // when use minimize feature,it you swipe close this activity,call endCall()
        // to make sure call is ended and the float window is dismissed.
        ZegoUIKitPrebuiltCallService.endCall();

    }
}