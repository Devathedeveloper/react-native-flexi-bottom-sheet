#import <React/RCTBridgeModule.h>
#import <React/RCTUIManager.h>
#import "RNBottomSheetComponentView.h"

@interface BottomSheetCommandsModule : NSObject <RCTBridgeModule>
@end

@implementation BottomSheetCommandsModule

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(snapToIndex:(nonnull NSNumber *)reactTag index:(NSInteger)index)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    RNBottomSheetView *view = [self resolveView:reactTag];
    [view setIndex:@(index)];
  });
}

RCT_EXPORT_METHOD(expand:(nonnull NSNumber *)reactTag)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    RNBottomSheetView *view = [self resolveView:reactTag];
    [view setIndex:@(view.snapPoints.count - 1)];
  });
}

RCT_EXPORT_METHOD(collapse:(nonnull NSNumber *)reactTag)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    RNBottomSheetView *view = [self resolveView:reactTag];
    [view setIndex:@0];
  });
}

RCT_EXPORT_METHOD(close:(nonnull NSNumber *)reactTag)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    RNBottomSheetView *view = [self resolveView:reactTag];
    [view setIndex:@(-1)];
  });
}

RCT_EXPORT_METHOD(setSnapPoints:(nonnull NSNumber *)reactTag points:(NSArray *)points)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    RNBottomSheetView *view = [self resolveView:reactTag];
    [view setSnapPoints:points];
  });
}

- (RNBottomSheetView *)resolveView:(NSNumber *)reactTag
{
  RCTUIManager *uiManager = self.bridge.uiManager;
  UIView *view = [uiManager viewForReactTag:reactTag];
  if ([view isKindOfClass:[RNBottomSheetView class]]) {
    return (RNBottomSheetView *)view;
  }
  return nil;
}

@end
