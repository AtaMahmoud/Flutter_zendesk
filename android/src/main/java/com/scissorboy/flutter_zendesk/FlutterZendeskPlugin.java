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
import zendesk.chat.ChatMenuAction;
import zendesk.chat.ChatProvider;
import zendesk.chat.PreChatFormFieldStatus;
import zendesk.chat.ProfileProvider;
import zendesk.chat.VisitorInfo;
import zendesk.messaging.MessagingActivity;

/**
 * FlutterZendeskPlugin
 */

public class FlutterZendeskPlugin implements MethodCallHandler {

    private final Registrar registrar;

    private FlutterZendeskPlugin(Registrar registrar) {
        this.registrar = registrar;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_zendesk");
        channel.setMethodCallHandler(new FlutterZendeskPlugin(registrar));
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
        Chat.INSTANCE.init(registrar.activeContext(), String.valueOf(call.argument("accountKey")));
        result.success(true);
    }

    private void startChat(MethodCall call, Result result) {

        ChatConfiguration chatConfiguration = ChatConfiguration.builder()

                .withNameFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withEmailFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withDepartmentFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withAgentAvailabilityEnabled(false)
                .withTranscriptEnabled(false)
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
        }

        ChatProvider chatProvider = Chat.INSTANCE.providers().chatProvider();
        chatProvider.setDepartment((String) call.argument("department"), null);

        MessagingActivity
                .builder()
                .withEngines(ChatEngine.engine())
                .show(registrar.activeContext(), chatConfiguration);

        result.success(true);
    }
}

