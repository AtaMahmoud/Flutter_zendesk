#import "FlutterZendeskPlugin.h"
#import <flutter_zendesk/flutter_zendesk-Swift.h>

@implementation FlutterZendeskPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterZendeskPlugin registerWithRegistrar:registrar];
}
@end
