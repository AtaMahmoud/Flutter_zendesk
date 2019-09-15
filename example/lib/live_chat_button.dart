import 'package:flutter/material.dart';
import 'package:flutter_zendesk/flutter_zendesk.dart';

class LiveChat extends StatelessWidget {
  final FlutterZendesk _flutterZendesk=FlutterZendesk();
  final Map<String,String>chatConfigs;
  LiveChat({@required this.chatConfigs});
  @override
  Widget build(BuildContext context) {

    return InkWell(
      child: Container(
        width: MediaQuery.of(context).size.width * .36,
        height: MediaQuery.of(context).size.height * .05,
        decoration: BoxDecoration(
          gradient: LinearGradient(colors: [
            Colors.grey,
            Colors.black,
            Colors.black54
          ]),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Material(
          color: Colors.transparent,
          child: InkWell(
            onTap: () =>_flutterZendesk.startChat(chatConfigs),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Icon(
                  Icons.chat,
                  color: Colors.white,
                ),
                SizedBox(
                  width: 7,
                ),
                Text(
                  'Live Chat',
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 18)),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
