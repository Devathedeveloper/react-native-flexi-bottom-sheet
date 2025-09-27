# react-native-flexi-bottom-sheet
A flexible bottom sheet for React Native built completely in native layers.

## Getting Started
- Install dependencies with `yarn` or `npm install`.
- Run `yarn build` to compile TypeScript definitions.
- For Android, include the module via Gradle and register `RnBottomSheetViewPackage`.
- For iOS, add `react-native-rn-bottom-sheet` to your Podfile and run `pod install`.

## Local Example App

The repository ships with a full React Native sample that consumes the library through a `file:` dependency. To try it locally:

```sh
npm install
cd example
npm install
npm start # run Metro bundler
npm run android # or npm run ios
```

Metro is configured to watch the library workspace so changes in `src/` are reflected immediately inside the example.

## Example Usage
See [`example/App.tsx`](example/App.tsx) for a full showcase of snap points, keyboard behaviors, and imperative commands.

## Acceptance Criteria
- Dragging from handle/content updates position smoothly at 60fps on mid-tier devices.
- Releasing with velocity snaps to the correct nearest snap considering direction + speed.
- Inner scroll views consume upward drag until contentOffset.y <= 0, then sheet takes over.
- With enablePanDownToClose=true, a downward fling from the lowest snap closes the sheet to index=-1 and fires onClose.
- Keyboard behaviors:
  - extend: focusing an input causes sheet to move to a larger snap that fully reveals the input.
  - fillParent: sheet goes to max snap while keyboard is visible.
  - interactive: sheet stays at current snap and insets adjust to avoid overlap.
- All props are controllable at runtime; setSnapPoints live-updates without remount.
- onAnimate throttled but frequent during drag; onChange fires once per snap settle.
- VoiceOver/TalkBack sees a modal region when open; escape/Back dismisses when enabled.
- Works on Android (API 23+) and iOS 13+; rotation & window size changes re-calc snap points.
