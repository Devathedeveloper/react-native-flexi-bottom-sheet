const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');
const path = require('path');

const projectRoot = __dirname;
const workspaceRoot = path.resolve(__dirname, '..');

const config = getDefaultConfig(projectRoot);

config.watchFolders = [workspaceRoot];
config.resolver.extraNodeModules = {
  ...config.resolver.extraNodeModules,
  'react-native-flexi-bottom-sheet': path.resolve(workspaceRoot),
};

module.exports = mergeConfig(config, {});
