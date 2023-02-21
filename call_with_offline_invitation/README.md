# Quick start

- - -

![call_gif](https://storage.zego.im/sdk-doc/Pics/ZegoUIKit/Flutter/call/invitation_calls.gif)


## Prerequisites

- Go to [ZEGOCLOUD Admin Console](https://console.zegocloud.com), and do the following:
  - Create a project, get the **AppID** and **AppSign**.
  - Activate the **In-app Chat** service (as shown in the following figure).

![ActivateZIMinConsole](https://storage.zego.im/sdk-doc/Pics/InappChat/ActivateZIMinConsole2.png)


## Add ZegoUIKitPrebuiltCall as dependencies

1. Add the `jitpack` configuration.

- If your Android Gradle Plugin is **7.1.0 or later**: enter your project's root directory, open the `settings.gradle` file to add the jitpack to `dependencyResolutionManagement` > `repositories` like this:

``` groovy
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      google()
      mavenCentral()
      maven { url 'https://www.jitpack.io' } // <- Add this line.
   }
}
```

<div class="mk-warning">

If you can't find the above fields in `settings.gradle`, it's probably because your Android Gradle Plugin version is lower than v7.1.0.

For more details, see [Android Gradle Plugin Release Note v7.1.0](https://developer.android.com/studio/releases/gradle-plugin#settings-gradle).
</div>

- If your Android Gradle Plugin is **earlier than 7.1.0**: enter your project's root directory, open the `build.gradle` file to add the jitpack to `allprojects`->`repositories` like this:

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }  // <- Add this line.
    }
}
```

2. Modify your app-level `build.gradle` file:
```groovy
dependencies {
    ...
    implementation 'com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:1.4.0'    // Add this line to your module-level build.gradle file's dependencies, usually named [app].
    implementation 'com.github.ZEGOCLOUD:zego_uikit_signaling_plugin_android:1.4.0'  // Add this line to your module-level build.gradle file's dependencies, usually named [app].
    implementation platform('com.google.firebase:firebase-bom:29.3.1') // Add this line to your module-level build.gradle file's dependencies，Import the Firebase BoM。
}
```
3. follow the instructions in the video below.

[![Watch the video](https://storage.zego.im/sdk-doc/Pics/ZegoUIKit/videos/how_to_enable_offline_call_invitation_android.png)](https://youtu.be/mhetL3MTKsE)

Resource may help: [Firebase Console](https://console.firebase.google.com/)


## Integrate the SDK with the call invitation feature

### 1、Initialize the call invitation service

Call the `init` method on the App startup, and specify the `userID` and `userName` for connecting the Call Kit service. 

- Go to [ZEGOCLOUD Admin Console](https://console.zegocloud.com/), get the `appID` and `appSign` of your project.
- `userID` can be something like a phone number or the user ID on your own user system. userID can only contain numbers, letters, and underlines (_).
- `userName` can be any character or the user name on your own user system.

### 2、Add the button for making call invitations

You can customize the position of the `ZegoSendCallInvitationButton` accordingly, pass in the ID of the user you want to call.

```java
public class MainActivity extends AppCompatActivity {
    long appID = YourAppID;
    String appSign = YourAppSign;
    String userID = "userID";
    String userName = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSendCallInvitationButton();
        initCallInviteService();
    }

    public void initCallInviteService() {
      ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig(ZegoSignalingPlugin.getInstance());
      //Change notifyWhenAppRunningInBackgroundOrQuit to false if you don't need to receive a call invitation notification while your app running in the background or quit.
      callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
      //This property needs to be set when you are building an Android app and when the notifyWhenAppRunningInBackgroundOrQuit is true. notificationConfig.channelID must be the same as the FCM Channel ID in ZEGOCLOUD Admin Console, and the notificationConfig.channelName can be an arbitrary value. The notificationConfig.soundmust be the same as the FCM sound in Admin Console either.
      ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
      notificationConfig.sound = "zego_uikit_sound_call";
      notificationConfig.channelID = "CallInvitation";
      notificationConfig.channelName = "CallInvitation";
      ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }

    private void initSendCallInvitationButton(){
        String targetUserID = ; // The ID of the user you want to call.
        Context context = ; // Android context.

        ZegoSendCallInvitationButton button = new ZegoSendCallInvitationButton(context);
        //	If true, a video call is made when the button is pressed. Otherwise, a voice call is made.
        button.setIsVideoCall(true);
        //resourceID can be used to specify the ringtone of an offline call invitation, which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console. This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        button.setResourceID("zego_uikit_call");
        button.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID)));
    }
}
```

Then, you can create a call invitation by starting your `MainActivity`.


## Check if your local configuration is correct

### Android

Please run the cmmand as below:

`python3 zego_check_android_offline_notification.py` 

You will get the output like this if everything is good:
```
✅ The google-service.json is in the right location.
✅ The package name matches google-service.json.
✅ The project level gradle file is ready.
✅ The plugin config in the app-level gradle file is correct.
✅ Firebase dependencies config in the app-level gradle file is correct.
✅ Firebase-Messaging dependencies config in the app-level gradle file is correct.
```

## Related guide
[Custom Prebuilt UI](https://docs.zegocloud.com/article/14766)