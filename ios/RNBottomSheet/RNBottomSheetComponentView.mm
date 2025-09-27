#import "RNBottomSheetComponentView.h"
#import <react/renderer/components/RNBottomSheetSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNBottomSheetSpec/EventEmitters.h>
#import <react/renderer/components/RNBottomSheetSpec/Props.h>
#import <react/renderer/components/RNBottomSheetSpec/RCTComponentViewHelpers.h>
#import <React/RCTConversions.h>

using namespace facebook::react;

@implementation RNBottomSheetComponentView {
  RNBottomSheetView *_bottomSheetView;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNBottomSheetComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    _bottomSheetView = [RNBottomSheetView new];
    self.contentView = _bottomSheetView;
  }
  return self;
}

- (void)prepareForRecycle
{
  [super prepareForRecycle];
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &newProps = *std::static_pointer_cast<RNBottomSheetViewProps const>(props);
  [_bottomSheetView setSnapPoints:RCTConvertNSArray(newProps.snapPoints())];
  [_bottomSheetView setIndex:newProps.index() >= 0 ? @(newProps.index()) : nil];
  [_bottomSheetView setEnableContentPanningGesture:@(newProps.enableContentPanningGesture())];
  [_bottomSheetView setEnableHandlePanningGesture:@(newProps.enableHandlePanningGesture())];
  [_bottomSheetView setEnablePanDownToClose:@(newProps.enablePanDownToClose())];
  [_bottomSheetView setEnableDynamicSizing:@(newProps.enableDynamicSizing())];
  [_bottomSheetView setKeyboardBehaviorProp:RCTNSStringFromString(newProps.keyboardBehavior())];
  if (newProps.backgroundColor()) {
    [_bottomSheetView setBackgroundColorProp:RCTUIColorFromSharedColor(newProps.backgroundColor())];
  }
  if (newProps.handleHeight()) {
    [_bottomSheetView setHandleHeight:@(newProps.handleHeight().value())];
  }
  if (newProps.handleIndicatorColor()) {
    [_bottomSheetView setHandleIndicatorColor:RCTUIColorFromSharedColor(newProps.handleIndicatorColor())];
  }
  if (newProps.cornerRadius()) {
    [_bottomSheetView setCornerRadius:@(newProps.cornerRadius().value())];
  }
  [super updateProps:props oldProps:oldProps];
}

- (RNBottomSheetView *)bottomSheetView
{
  return _bottomSheetView;
}

@end

Class<RCTComponentViewProtocol> RNBottomSheetComponentViewCls(void)
{
  return RNBottomSheetComponentView.class;
}
