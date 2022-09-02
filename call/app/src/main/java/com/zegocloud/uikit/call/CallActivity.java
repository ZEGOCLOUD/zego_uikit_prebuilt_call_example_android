package com.zegocloud.uikit.call;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.zegocloud.uikit.components.audiovideo.ZegoViewProvider;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;
import com.zegocloud.uikit.prebuilt.call.internal.ZegoVideoForegroundView;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        addCallFragment();
    }

    public void addCallFragment() {
        long appID = yourAppID;
        String appSign = yourAppSign;

        String callID = ;   // for example, "123456"
        String userID = yourUserID; //for example, Build.MANUFACTURER
        String userName = yourUserName; //for example, Build.MANUFACTURER

        ZegoUIKitPrebuiltCallConfig config = new ZegoUIKitPrebuiltCallConfig();
        ZegoUIKitPrebuiltCallFragment fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
            appID, appSign, callID, userID, userName, config);
        fragment.setForegroundViewProvider(new ZegoViewProvider() {
            @Override
            public View getForegroundView(ZegoUIKitUser userInfo) {
                // AudioVideoForegroundView is your own widget that show user level label based on user.userID
                ZegoVideoForegroundView foregroundView = new ZegoVideoForegroundView(CallActivity.this, userInfo);
                return foregroundView;
            }
        });
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow();
    }
}