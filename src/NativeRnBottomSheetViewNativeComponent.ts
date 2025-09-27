import type { HostComponent, ViewProps } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import type { KeyboardBehavior } from './types';

export type NativeProps = ViewProps & {
  index?: number;
  snapPoints: (number | string)[];
  enableContentPanningGesture?: boolean;
  enableHandlePanningGesture?: boolean;
  enablePanDownToClose?: boolean;
  enableDynamicSizing?: boolean;
  keyboardBehavior?: KeyboardBehavior;
  android_keyboardInputMode?: 'adjustPan' | 'adjustResize';
  overDragResistance?: number;
  backgroundColor?: string;
  handleHeight?: number;
  handleIndicatorColor?: string;
  elevation?: number;
  cornerRadius?: number;
  accessible?: boolean;
  accessibilityLabel?: string;
  onChange?: DirectEventHandler<{ index: number }>;
  onAnimate?: DirectEventHandler<{ index: number; position: number }>;
  onOpen?: DirectEventHandler<null>;
  onClose?: DirectEventHandler<null>;
  onGestureStart?: DirectEventHandler<null>;
  onGestureEnd?: DirectEventHandler<null>;
};

export default codegenNativeComponent<NativeProps>('RnBottomSheetView') as HostComponent<NativeProps>;
