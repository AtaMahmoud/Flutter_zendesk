import Flutter
import UIKit
import ChatSDK
import ChatProvidersSDK
import MessagingSDK

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
        
        guard let initArgs = call.arguments as? [String: String] else { return }
        Chat.initialize(accountKey: initArgs["accountKey"] ?? "")
        result(nil)
    }
    
    private func startChat(_ call:FlutterMethodCall,result: @escaping FlutterResult){
        
        guard let chatArgs = call.arguments as? [String: String] else { return }
        if let chatVC = configChat(configs: chatArgs){
            fireChat(chatVC: chatVC)
        }
        
        result(nil)
    }
    
    private func configChat(configs: [String: String]) -> UIViewController?{
        
        guard let brand = configs["appName"], let department = configs["department"], let visitorPath = configs["visitorPath"] else { return nil }
        
        let name = configs["userName"] ?? ""
        let email = configs["email"] ?? ""
        
        let formConfiguration = ChatFormConfiguration(name: .required,
                                                      email: .required,
                                                      phoneNumber: .hidden,
                                                      department: .required)
        let chatConfiguration = ChatConfiguration()
        chatConfiguration.isPreChatFormEnabled = false
        chatConfiguration.isOfflineFormEnabled = true
        chatConfiguration.preChatFormConfiguration = formConfiguration
        
        
        let chatAPIConfiguration = ChatAPIConfiguration()
        chatAPIConfiguration.department = department
        chatAPIConfiguration.tags = [brand]
        
        chatAPIConfiguration.visitorInfo = VisitorInfo(name: name, email: email, phoneNumber: "")
        
        chatAPIConfiguration.visitorPathOne = brand
        
        chatAPIConfiguration.visitorPathTwo = "Mobile Chat connected"
        
        let path =  VisitorPath(title: visitorPath)
        
        Chat.profileProvider?.trackVisitorPath(path)
        
        Chat.instance?.configuration = chatAPIConfiguration
        
        do {
            let chatEngine = try ChatEngine.engine()
            
            let chatVC = try Messaging.instance.buildUI(engines: [chatEngine], configs: [chatConfiguration])
            return chatVC
        } catch {
            return nil
        }
    }
    
    private func fireChat(chatVC: UIViewController){
        let navC = UINavigationController(rootViewController: chatVC)
        
        chatVC.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(dismissChatVC))
        navC.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navC.navigationBar.shadowImage = UIImage()
        navC.navigationBar.isTranslucent = true
        navC.modalPresentationStyle = .fullScreen
        
        let rootVC = UIApplication.shared.windows.first!.rootViewController!
        
        rootVC.present(navC, animated: true, completion: nil)
    }
    
    @objc private func dismissChatVC() {
        Chat.chatProvider?.endChat()
        UIApplication.shared.windows.first!.rootViewController!.dismiss(animated: true, completion: nil)
    }
    
}
