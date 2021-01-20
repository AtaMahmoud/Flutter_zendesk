package com.scissorboy.flutter_zendesk;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import zendesk.chat.Chat;
import zendesk.chat.ChatConfiguration;
import zendesk.chat.ChatEngine;
import zendesk.chat.ChatMenuAction;
import zendesk.chat.ChatProvider;
import zendesk.chat.PreChatFormFieldStatus;
import zendesk.chat.ProfileProvider;
import zendesk.chat.VisitorInfo;
import zendesk.messaging.MessagingActivity;

/**
 * FlutterZendeskPlugin
 */

public class FlutterZendeskPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

    private Context context;
    private MethodChannel methodChannel;


    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        FlutterZendeskPlugin flutterZendeskPlugin = new FlutterZendeskPlugin();
        flutterZendeskPlugin.onAttachedToEngine(registrar.activeContext(), registrar.messenger());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "init":
                init(call, result);
                break;
            case "startChat":
                startChat(call, result);
                break;
            case "disconnect":
                disconnect();
                break;    
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.context = null;
        methodChannel = null;
    }

    private void onAttachedToEngine(Context context, BinaryMessenger messenger) {
        this.context = context;
        methodChannel = new MethodChannel(messenger, "flutter_zendesk");
        methodChannel.setMethodCallHandler(this);
    }

    private void init(MethodCall call, Result result) {
        Chat.INSTANCE.init(context, String.valueOf(call.argument("accountKey")));
        result.success(true);
    }

    private void startChat(MethodCall call, Result result) {

        ChatConfiguration chatConfiguration = ChatConfiguration.builder()

                .withNameFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withEmailFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withDepartmentFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withAgentAvailabilityEnabled(true)
                .withOfflineFormEnabled(true)
                .withTranscriptEnabled(false)
                .withPreChatFormEnabled(false)
                .withChatMenuActions()
                .build();

        if (call.hasArgument("userName") && call.hasArgument("email")) {
            ProfileProvider profileProvider = Chat.INSTANCE.providers().profileProvider();
            VisitorInfo visitorInfo = VisitorInfo.builder()
                    .withName((String) call.argument("userName"))
                    .withEmail((String) call.argument("email"))
                    .withPhoneNumber((String) call.argument("phoneNumber"))
                    .build();
            profileProvider.setVisitorInfo(visitorInfo, null);

            List<String> tags = new ArrayList<>();
            if(call.hasArgument("userName")) {
                tags.add((String)call.argument("appName"));
            }

            if(call.hasArgument("keys")) {
                String keyListString =  (String)call.argument("keys");
                String[] keyList = keyListString.split(",");
                for (String key : keyList) {
                    if(call.hasArgument(key)) {
                        tags.add((String)call.argument(key));
                        Log.i(key+"java",(String)call.argument(key));
                    }
                }
            }

            if(tags.size() > 0) {
                profileProvider.addVisitorTags(tags,null);
            }

        }

        ChatProvider chatProvider = Chat.INSTANCE.providers().chatProvider();
        chatProvider.setDepartment((String) call.argument("department"), null);

        MessagingActivity
                .builder()
                .withEngines(ChatEngine.engine())
                .show(context, chatConfiguration);

        String order_details = "";
        if(call.hasArgument("order_details")) {
            order_details =  (String) call.argument("order_details");
        }
        Log.i("Order details","before");
        if(order_details.length() > 0) {
            Log.i("Order details 1",order_details);
        final Handler handler = new Handler();

        final String finalOrder_details = order_details;
        final Runnable r = new Runnable() {
        public void run() {
            ChatLog.Message test = chatProvider.sendMessage(finalOrder_details);
            Log.i("Message sent successfull",test.getMessage());
            //handler.postDelayed(this, 5000);
        }
    };

    handler.postDelayed(r, 5000);

}
        

        result.success(true);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        Log.i("onAttachedToActivity", "onAttachedToActivity");
        context = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        context = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        context = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        context = null;
    }

    void disconnect() {
        Chat.INSTANCE.providers().chatProvider().endChat(null);
        Chat.INSTANCE.resetIdentity();
        Chat.INSTANCE.clearCache();
    }
}

