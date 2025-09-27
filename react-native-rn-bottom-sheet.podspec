require "json"
package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-rn-bottom-sheet"
  s.version      = package["version"]
  s.summary      = "Fabric bottom sheet"
  s.description  = s.summary
  s.homepage     = "https://github.com/example/react-native-flexi-bottom-sheet"
  s.license      = "MIT"
  s.author       = { "OpenAI" => "opensource@example.com" }
  s.source       = { :git => "https://github.com/example/react-native-flexi-bottom-sheet.git", :tag => s.version }
  s.platform     = :ios, "13.0"

  s.source_files = "ios/RNBottomSheet/**/*.{h,m,mm,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "React-Core"
  s.dependency "React-RCTFabric"
  s.dependency "React-Codegen"
end
