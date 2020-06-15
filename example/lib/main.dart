import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter_zendesk/flutter_zendesk.dart';
import 'package:flutter/services.dart';
import 'package:flutter_zendesk_example/logo_live_chat_header.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String ZENDESK_CHAT_KEY = "4kSNfs8T5QbKBJHrW6Ciw7TdfOWYBW4p";
  String APP_TECH_SUPPORT = 'App tech support';
  String APP_NAME = 'Wella Professionals ,0.0.1';

  @override
  void initState() {
    super.initState();
    FlutterZendesk().init(accountKey: ZENDESK_CHAT_KEY);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: LogoAndLiveChatHeader(
              {"department": APP_TECH_SUPPORT, "appName": APP_NAME}),
        ),
      ),
    );
  }
}
