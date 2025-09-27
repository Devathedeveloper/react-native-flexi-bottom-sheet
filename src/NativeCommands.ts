import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface BottomSheetNativeCommands extends TurboModule {
  snapToIndex(tag: number, index: number): void;
  expand(tag: number): void;
  collapse(tag: number): void;
  close(tag: number): void;
  setSnapPoints(tag: number, points: (number | string)[]): void;
}

export default TurboModuleRegistry.getEnforcing<BottomSheetNativeCommands>('BottomSheetCommandsModule');
