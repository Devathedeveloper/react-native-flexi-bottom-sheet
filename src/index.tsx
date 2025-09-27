import React, {
  forwardRef,
  useCallback,
  useImperativeHandle,
  useRef,
} from 'react';
import type { NativeSyntheticEvent } from 'react-native';
import { UIManager, findNodeHandle } from 'react-native';
import NativeComponent from './NativeRnBottomSheetViewNativeComponent';
import type { BottomSheetProps, BottomSheetRef } from './types';
import NativeCommands from './NativeCommands';

const BottomSheet = forwardRef<BottomSheetRef, BottomSheetProps>(
  (
    {
      index = -1,
      snapPoints,
      children,
      onAnimate,
      onChange,
      onClose,
      onOpen,
      onGestureStart,
      onGestureEnd,
      ...rest
    },
    ref,
  ) => {
    const nativeRef = useRef<React.ElementRef<typeof NativeComponent>>(null);

    const dispatchCommand = useCallback(
      (
        command: keyof BottomSheetRef | 'setSnapPoints' | 'snapToIndex',
        args: unknown[],
      ) => {
        const viewTag = findNodeHandle(nativeRef.current);
        if (viewTag == null) {
          return;
        }

        // Fabric command dispatch – fall back to legacy UIManager lookup when available.
        const config = UIManager.getViewManagerConfig?.('RnBottomSheetView');
        const getCommandId = (name: string) =>
          (config?.Commands as Record<string, number> | undefined)?.[name];

        switch (command) {
          case 'snapToIndex':
            NativeCommands.snapToIndex(viewTag, args[0] as number);
            {
              const commandId = getCommandId('snapToIndex');
              if (commandId != null) {
                UIManager.dispatchViewManagerCommand(viewTag, commandId, [args[0]]);
              }
            }
            break;
          case 'expand':
            NativeCommands.expand(viewTag);
            {
              const commandId = getCommandId('expand');
              if (commandId != null) {
                UIManager.dispatchViewManagerCommand(viewTag, commandId, []);
              }
            }
            break;
          case 'collapse':
            NativeCommands.collapse(viewTag);
            {
              const commandId = getCommandId('collapse');
              if (commandId != null) {
                UIManager.dispatchViewManagerCommand(viewTag, commandId, []);
              }
            }
            break;
          case 'close':
            NativeCommands.close(viewTag);
            {
              const commandId = getCommandId('close');
              if (commandId != null) {
                UIManager.dispatchViewManagerCommand(viewTag, commandId, []);
              }
            }
            break;
          case 'setSnapPoints':
            NativeCommands.setSnapPoints(viewTag, args[0] as (number | string)[]);
            {
              const commandId = getCommandId('setSnapPoints');
              if (commandId != null) {
                UIManager.dispatchViewManagerCommand(viewTag, commandId, [args[0]]);
              }
            }
            break;
          default:
            break;
        }
      },
      [],
    );

    useImperativeHandle(
      ref,
      () => ({
        snapToIndex: (snapIndex: number) => dispatchCommand('snapToIndex', [snapIndex]),
        expand: () => dispatchCommand('expand', []),
        collapse: () => dispatchCommand('collapse', []),
        close: () => dispatchCommand('close', []),
        setSnapPoints: (points) => dispatchCommand('setSnapPoints', [points]),
      }),
      [dispatchCommand],
    );

    const handleAnimate = useCallback(
      (event: NativeSyntheticEvent<{ index: number; position: number }>) => {
        onAnimate?.(event.nativeEvent);
      },
      [onAnimate],
    );

    const handleChange = useCallback(
      (event: NativeSyntheticEvent<{ index: number }>) => {
        onChange?.(event.nativeEvent.index);
      },
      [onChange],
    );

    const handleOpen = useCallback(() => {
      onOpen?.();
    }, [onOpen]);

    const handleClose = useCallback(() => {
      onClose?.();
    }, [onClose]);

    const handleGestureStart = useCallback(() => {
      onGestureStart?.();
    }, [onGestureStart]);

    const handleGestureEnd = useCallback(() => {
      onGestureEnd?.();
    }, [onGestureEnd]);

    return (
      <NativeComponent
        ref={nativeRef}
        index={index}
        snapPoints={snapPoints as (number | string)[]}
        onAnimate={handleAnimate}
        onChange={handleChange}
        onClose={handleClose}
        onOpen={handleOpen}
        onGestureStart={handleGestureStart}
        onGestureEnd={handleGestureEnd}
        {...rest}
      >
        {children}
      </NativeComponent>
    );
  },
);

BottomSheet.displayName = 'BottomSheet';

export type { BottomSheetProps, BottomSheetRef, KeyboardBehavior } from './types';
export default BottomSheet;
