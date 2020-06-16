import 'package:flutter/material.dart';
import 'package:flutter_zendesk/flutter_zendesk.dart';
import 'package:flutter_zendesk_example/logo_live_chat_header.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String ZENDESK_CHAT_KEY = "key";
  String DEPARTMENT = 'department';
  String APP_NAME = 'app name';
  String APP_PATH = 'path, 0.0(0)';
  String EMAIL = 'user@mail.com';
  String NAME = 'user';

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
          child: LogoAndLiveChatHeader({
            "department": DEPARTMENT,
            "appName": APP_NAME,
            "visitorPath": APP_PATH,
            "email": EMAIL,
            "userName": NAME,
          }),
        ),
      ),
    );
  }
}
