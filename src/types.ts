import type { ViewStyle } from 'react-native';

export type KeyboardBehavior = 'extend' | 'fillParent' | 'interactive';

export interface BottomSheetProps {
  index?: number;
  snapPoints: (number | `${number}%`)[];
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
  onChange?: (index: number) => void;
  onAnimate?: (payload: { index: number; position: number }) => void;
  onOpen?: () => void;
  onClose?: () => void;
  onGestureStart?: () => void;
  onGestureEnd?: () => void;
  children?: React.ReactNode;
  style?: ViewStyle;
}

export type BottomSheetRef = {
  snapToIndex: (index: number) => void;
  expand: () => void;
  collapse: () => void;
  close: () => void;
  setSnapPoints: (points: BottomSheetProps['snapPoints']) => void;
};
