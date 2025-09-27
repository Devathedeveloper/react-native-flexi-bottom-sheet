# Bottom Sheet Example App

This example demonstrates how to consume the local `react-native-flexi-bottom-sheet` library.

## Getting started

1. Install dependencies for the monorepo and the example project:

   ```sh
   npm install
   cd example
   npm install
   ```

2. Link the native module assets and install iOS pods (macOS):

   ```sh
   npx react-native-clean-project --remove-iOS-build --remove-android-build --keep-node-modules
   npx pod-install ios
   ```

3. Run Metro in one terminal:

   ```sh
   npm start
   ```

4. Launch the example on a device or simulator:

   ```sh
   npm run android
   # or
   npm run ios
   ```

The example app uses the local package via a `file:` dependency declared in `package.json` and a Metro watch folder so changes in the library are reflected immediately.
