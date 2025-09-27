require "json"

package = JSON.parse(File.read(File.join(__dir__, "../package.json")))

Pod::Spec.new do |s|
  s.name         = "RNBottomSheet"
  s.version      = package["version"]
  s.summary      = "React Native Fabric bottom sheet"
  s.description  = s.summary
  s.homepage     = "https://github.com/example/react-native-flexi-bottom-sheet"
  s.license      = { :type => "MIT" }
  s.authors      = { "React Native" => "opensource@example.com" }
  s.platforms    = { :ios => "13.0" }
  s.source       = { :git => "https://github.com/example/react-native-flexi-bottom-sheet.git", :tag => s.version }

  s.source_files = "RNBottomSheet/**/*.{h,m,mm,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "React-Core"
  s.dependency "React-RCTFabric"
  s.dependency "React-Codegen"

  s.swift_version = "5.0"
end
