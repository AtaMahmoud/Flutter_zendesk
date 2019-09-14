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
        
        do {
            let json = call.arguments as! String
            let jsonDecoder = JSONDecoder()
            let initArgs = try jsonDecoder.decode([String:String].self, from: json.data(using: .utf8)!)
            print(initArgs["accountKey"]!)
            ZDCChat.initialize(withAccountKey: initArgs["accountKey"])
        } catch {

                print("FlutterZendeskPlugin error:" + error.localizedDescription)

        }

      
        result(nil)
        
        
        
    }
    
    
    private func startChat(_ call:FlutterMethodCall,result: @escaping FlutterResult){
        
        do {
            let json = call.arguments as! String
            let jsonDecoder = JSONDecoder()
            let chatArgs = try jsonDecoder.decode([String:String].self, from: json.data(using: .utf8)!)

            
            if(!chatArgs["userName"]!.isEmpty && !chatArgs["email"]!.isEmpty ){
                
                let config = ZDCConfig()
                
                config.preChatDataRequirements.name = .required
                config.preChatDataRequirements.email = .required
                config.preChatDataRequirements.department = .required
                config.preChatDataRequirements.message = .required
                
                ZDCChat.updateVisitor { user in
                    user?.phone = chatArgs["phoneNumber"]!
                    user?.name = chatArgs["userName"]!
                    user?.email = chatArgs["email"]!
                }
                
                ZDCChat.start { config in
                    config?.department = chatArgs["department"]
                    config?.visitorPathOne = chatArgs["appName"]
                    config?.preChatDataRequirements = config?.preChatDataRequirements
                }
                
                
            }
            
            
        } catch {
            
            print("FlutterZendeskPlugin error:" + error.localizedDescription)
            
        }
        
        result(nil)

    }
    
    
}
