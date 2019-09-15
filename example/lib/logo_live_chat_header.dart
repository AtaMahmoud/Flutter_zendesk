import 'package:flutter/material.dart';
import 'package:flutter_zendesk_example/live_chat_button.dart';

class LogoAndLiveChatHeader extends StatelessWidget {
  final Map<String,String>chatConfigs;
  LogoAndLiveChatHeader(this.chatConfigs);
  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        LiveChat(chatConfigs: chatConfigs,),
      ],
    );
  }
}
