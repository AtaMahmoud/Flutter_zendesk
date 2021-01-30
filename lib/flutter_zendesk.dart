import 'dart:async';

import 'package:flutter/services.dart';

class FlutterZendesk {
  static const MethodChannel _channel = const MethodChannel('flutter_zendesk');

  Future<void> init({String accountKey}) async {
    await _channel
        .invokeMethod('init', <String, String>{"accountKey": accountKey});
  }

  Future<void> startChat(Map<String, String> chatConfigs) async {
    await _channel.invokeMethod("startChat", chatConfigs);
  }

  Future<void> disconnect() async {
    await _channel.invokeMethod("disconnect");
  }
}
