package com.scissorboy.flutter_zendesk;

import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.PreChatForm;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import androidx.annotation.NonNull;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

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
        ZopimChat.init((String) call.argument("accountKey"));
        result.success(true);
    }


    private void startChat(MethodCall call, Result result) {
        PreChatForm preChatForm = new PreChatForm.Builder()
                .name(PreChatForm.Field.REQUIRED)
                .email(PreChatForm.Field.REQUIRED)
                .department(PreChatForm.Field.REQUIRED)
                .message(PreChatForm.Field.REQUIRED)
                .build();

        if (call.hasArgument("userName") && call.hasArgument("email")) {
            VisitorInfo visitorInfo = new VisitorInfo.Builder()
                    .name((String) call.argument("userName"))
                    .email((String) call.argument("email"))
                    .phoneNumber((String) call.argument("phoneNumber"))
                    .build();

            ZopimChat.setVisitorInfo(visitorInfo);
        }


        ZopimChat.SessionConfig config = new ZopimChat.SessionConfig()
                .visitorPathOne((String) call.argument("appName"))
                .preChatForm(preChatForm)
                .department((String) call.argument("department"));


        ZopimChatActivity.startActivity(registrar.activeContext(), config);
        result.success(true);
    }
}
