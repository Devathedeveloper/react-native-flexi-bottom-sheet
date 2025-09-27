#import <React/RCTViewComponentView.h>
#import "RNBottomSheetView.h"

NS_ASSUME_NONNULL_BEGIN

@interface RNBottomSheetComponentView : RCTViewComponentView
- (RNBottomSheetView *)bottomSheetView;
@end

NS_ASSUME_NONNULL_END

Class<RCTComponentViewProtocol> RNBottomSheetComponentViewCls(void);
