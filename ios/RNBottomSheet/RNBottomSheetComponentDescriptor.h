#import <react/renderer/components/RNBottomSheetSpec/ComponentDescriptors.h>

namespace facebook {
namespace react {

class RNBottomSheetComponentDescriptor : public RNBottomSheetViewComponentDescriptor {
public:
  RNBottomSheetComponentDescriptor(ComponentDescriptorParameters const &parameters)
      : RNBottomSheetViewComponentDescriptor(parameters) {}
};

} // namespace react
} // namespace facebook
