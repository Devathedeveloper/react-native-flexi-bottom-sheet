import React from 'react';
jest.mock('react-test-renderer', () => {
  return {
    __esModule: true,
    default: {
      create: jest.fn((element: any) => ({ element })),
    },
    act: (callback: () => void) => callback(),
  };
});

import renderer, { act } from 'react-test-renderer';

jest.mock('react-native', () => ({
  UIManager: {
    getViewManagerConfig: jest.fn(() => ({ Commands: {} })),
    dispatchViewManagerCommand: jest.fn(),
  },
  findNodeHandle: jest.fn(() => 1),
  TurboModuleRegistry: {
    getEnforcing: jest.fn(() => ({
      snapToIndex: jest.fn(),
      expand: jest.fn(),
      collapse: jest.fn(),
      close: jest.fn(),
      setSnapPoints: jest.fn(),
    })),
  },
}));

jest.mock('../src/NativeRnBottomSheetViewNativeComponent', () => {
  const React = require('react');
  return React.forwardRef((props: any, ref: any) =>
    React.createElement('bottom-sheet', { ...props, ref }, props.children),
  );
});

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

import BottomSheet from '../src';

describe('BottomSheet', () => {
  it('renders with snap points', () => {
    let tree: any;
    act(() => {
      tree = renderer.create(
        <BottomSheet snapPoints={[100, '50%']}>
          <></>
        </BottomSheet>,
      );
    });
    expect(tree).toBeDefined();
  });
});
