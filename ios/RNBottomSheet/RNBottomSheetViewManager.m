#import <React/RCTViewManager.h>
#import "RNBottomSheetView.h"

@interface RNBottomSheetViewManager : RCTViewManager
@end

@implementation RNBottomSheetViewManager

RCT_EXPORT_MODULE(RnBottomSheetView)

- (UIView *)view
{
  return [RNBottomSheetView new];
}

RCT_EXPORT_VIEW_PROPERTY(index, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(snapPoints, NSArray)
RCT_EXPORT_VIEW_PROPERTY(enableContentPanningGesture, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableHandlePanningGesture, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enablePanDownToClose, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(enableDynamicSizing, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(keyboardBehaviorProp, NSString)
RCT_EXPORT_VIEW_PROPERTY(backgroundColorProp, UIColor)
RCT_EXPORT_VIEW_PROPERTY(handleHeight, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(handleIndicatorColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(cornerRadius, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAnimate, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onOpen, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onClose, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onGestureStart, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onGestureEnd, RCTBubblingEventBlock)

@end
