import Flutter
import UIKit
import ZDCChat

public class SwiftFlutterZendeskPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_zendesk", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterZendeskPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "init":
        setUp(call, result: result)
    case "startChat":
        startChat(call, result: result)
    default:
        result(FlutterMethodNotImplemented)
    }
  }
 
    private func setUp(_ call:FlutterMethodCall,result: @escaping FlutterResult){

            let initArgs = call.arguments as! Dictionary<String, String>

            ZDCChat.initialize(withAccountKey: initArgs["accountKey"])
      
            result(nil)
        
        
        
    }
    
    
    private func startChat(_ call:FlutterMethodCall,result: @escaping FlutterResult){

            let chatArgs = call.arguments as! Dictionary<String, String>
        
            var userName = chatArgs["userName"] ?? "";
            var email = chatArgs["email"] ?? "";
        
            if(userName.count > 0 && email.count > 2){
                
                ZDCChat.updateVisitor { user in
                    user?.phone = chatArgs["phoneNumber"]!
                    user?.name = chatArgs["userName"]!
                    user?.email = chatArgs["email"]!
                }
                
            }
        
            ZDCChat.start {config in
                config?.visitorPathOne = chatArgs["appName"]
                config?.preChatDataRequirements.name = .required
                config?.preChatDataRequirements.email = .required
                config?.preChatDataRequirements.department = .required
                config?.preChatDataRequirements.message = .required
                config?.department = chatArgs["department"]
               
            }
        

        
        result(nil)

    }
    
    
}
