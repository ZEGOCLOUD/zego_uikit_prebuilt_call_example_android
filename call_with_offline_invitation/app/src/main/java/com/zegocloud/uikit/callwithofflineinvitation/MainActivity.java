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
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener;
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener;
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener;
import com.zegocloud.uikit.prebuilt.call.event.ZegoCallEndReason;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler;
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import timber.log.Timber;

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
        ZegoUIKitPrebuiltCallService.unInit();
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

        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = getConfig(invitationData);
                return config;
            }
        };

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

        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,
            callInvitationConfig);

        ZegoUIKitPrebuiltCallService.events.callEvents.setCallEndListener(new CallEndListener() {
            @Override
            public void onCallEnd(ZegoCallEndReason callEndReason, String jsonObject) {
                Timber.d("onCallEnd() called with: callEndReason = [" + callEndReason + "], jsonObject = [" + jsonObject
                    + "]");
            }
        });

        ZegoUIKitPrebuiltCallService.events.callEvents.setExpressEngineEventHandler(
            new IExpressEngineEventHandler() {
                @Override
                public void onRoomStateChanged(String roomID, ZegoRoomStateChangedReason reason, int errorCode,
                    JSONObject extendedData) {
                    Timber.d("onRoomStateChanged() called with: roomID = [" + roomID + "], reason = [" + reason
                        + "], errorCode = [" + errorCode + "], extendedData = [" + extendedData + "]");
                }
            });
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

    public ZegoUIKitPrebuiltCallConfig getConfig(ZegoCallInvitationData invitationData) {
        boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
        boolean isGroupCall = invitationData.invitees.size() > 1;
        ZegoUIKitPrebuiltCallConfig callConfig;
        if (isVideoCall && isGroupCall) {
            callConfig = ZegoUIKitPrebuiltCallConfig.groupVideoCall();
        } else if (!isVideoCall && isGroupCall) {
            callConfig = ZegoUIKitPrebuiltCallConfig.groupVoiceCall();
        } else if (!isVideoCall) {
            callConfig = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
        } else {
            callConfig = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
        }
        return callConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // when use minimize feature,it you swipe close this activity,call endCall()
        // to make sure call is ended and the float window is dismissed
        ZegoUIKitPrebuiltCallService.endCall();
    }
}