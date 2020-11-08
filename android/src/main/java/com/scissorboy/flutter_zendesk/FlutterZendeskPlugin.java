package com.scissorboy.flutter_zendesk;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
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
import zendesk.chat.ChatProvider;
import zendesk.chat.PreChatFormFieldStatus;
import zendesk.chat.ProfileProvider;
import zendesk.chat.VisitorInfo;
import zendesk.messaging.MessagingActivity;

/**
 * FlutterZendeskPlugin
 */

public class FlutterZendeskPlugin implements MethodCallHandler, FlutterPlugin {

    private Registrar registrar;
    private Context applicationContext;
    private Activity activity;
    private MethodChannel methodChannel;

    public FlutterZendeskPlugin(Activity activity) {
        this.activity = activity;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        FlutterZendeskPlugin plugin = new FlutterZendeskPlugin(registrar.activity());
        plugin.onAttachedToEngine(registrar.context(), registrar.messenger());
    }

    public void onAttachedToEngine(FlutterPluginBinding binding) {
        onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
    }

    private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
        this.applicationContext = applicationContext;
        methodChannel = new MethodChannel(messenger, "flutter_zendesk");
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        applicationContext = null;
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
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
            default:
                result.notImplemented();
                break;
        }
    }

    private void init(MethodCall call, Result result) {
        Chat.INSTANCE.init(applicationContext, String.valueOf(call.argument("accountKey")));
        result.success(true);
    }

    private void startChat(MethodCall call, Result result) {

        ChatConfiguration chatConfiguration = ChatConfiguration.builder()
                .withNameFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withEmailFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withDepartmentFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withAgentAvailabilityEnabled(false)
                .build();


        if (call.hasArgument("userName") && call.hasArgument("email")) {
            ProfileProvider profileProvider = Chat.INSTANCE.providers().profileProvider();
            VisitorInfo visitorInfo = VisitorInfo.builder()
                    .withName((String) call.argument("userName"))
                    .withEmail((String) call.argument("email"))
                    .withPhoneNumber((String) call.argument("phoneNumber"))
                    .build();
            profileProvider.setVisitorInfo(visitorInfo, null);
        }

        ChatProvider chatProvider = Chat.INSTANCE.providers().chatProvider();
        chatProvider.setDepartment((String) call.argument("department"), null);

        if (activity == null) {
            return;
        }

        MessagingActivity
                .builder()
                .withEngines(ChatEngine.engine())
                .show(activity, chatConfiguration);

        result.success(true);
    }
}

