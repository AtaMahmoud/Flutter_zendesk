package com.scissorboy.flutter_zendesk;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

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
import zendesk.chat.Account;
import zendesk.chat.AccountStatus;
import zendesk.chat.Chat;
import zendesk.chat.ChatConfiguration;
import zendesk.chat.ChatEngine;
import zendesk.chat.ChatLog;
import zendesk.chat.ChatMenuAction;
import zendesk.chat.ChatProvider;
import zendesk.chat.OfflineForm;
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

        VisitorInfo visitorInfo = null;

        ChatConfiguration chatConfiguration = ChatConfiguration.builder()

                .withNameFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withEmailFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withDepartmentFieldStatus(PreChatFormFieldStatus.REQUIRED)
                .withAgentAvailabilityEnabled(true)
                .withOfflineFormEnabled(true)
                .withTranscriptEnabled(false)
                .withPreChatFormEnabled(false)
                .withChatMenuActions(ChatMenuAction.END_CHAT)
                .build();

        if (call.hasArgument("userName") && call.hasArgument("email")) {
            ProfileProvider profileProvider = Chat.INSTANCE.providers().profileProvider();
             visitorInfo = VisitorInfo.builder()
                    .withName((String) call.argument("userName"))
                    .withEmail((String) call.argument("email"))
                    .withPhoneNumber((String) call.argument("phoneNumber"))
                    .build();
            profileProvider.setVisitorInfo(visitorInfo, null);

            List<String> tags = new ArrayList<>();
            if (call.hasArgument("userName")) {
                tags.add((String) call.argument("appName"));
            }

            if (call.hasArgument("keys")) {
                String keyListString = (String) call.argument("keys");
                String[] keyList = keyListString.split(",");
                for (String key : keyList) {
                    if (call.hasArgument(key)) {
                        tags.add((String) call.argument(key));
                        Log.i(key + "java", (String) call.argument(key));
                    }
                }
            }

            if (tags.size() > 0) {
                profileProvider.addVisitorTags(tags, null);
            }

        }

        final ChatProvider chatProvider = Chat.INSTANCE.providers().chatProvider();
        chatProvider.setDepartment((String) call.argument("department"), null);

        final String[] order_details = {""};
        if(call.hasArgument("order_details")) {
            order_details[0] =  (String) call.argument("order_details");
        }

        if(order_details[0].length() > 0) {
            final VisitorInfo finalVisitorInfo = visitorInfo;
            Chat.INSTANCE.providers().accountProvider().getAccount(new ZendeskCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    if (account.getStatus() == AccountStatus.ONLINE) {
                        chatProvider.sendMessage(order_details[0]);
                        order_details[0] = "";
                    } else {
                        OfflineForm offlineForm = OfflineForm.builder(order_details[0])
                                .withVisitorInfo(finalVisitorInfo)
                                .build();
                        order_details[0] = "";
                        Chat.INSTANCE.providers().chatProvider().sendOfflineForm(offlineForm, null);
                    }
                }

                @Override
                public void onError(ErrorResponse errorResponse) {
                    Log.i("account error", errorResponse.getReason());
                }
            });
        }


        MessagingActivity
                .builder()
                .withEngines(ChatEngine.engine())
                .show(context, chatConfiguration);


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
        Chat.INSTANCE.providers().chatProvider().endChat(null);
        Chat.INSTANCE.resetIdentity();
        Chat.INSTANCE.clearCache();
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

