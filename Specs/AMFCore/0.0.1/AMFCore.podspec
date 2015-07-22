Pod::Spec.new do |s|

  s.name         = 'AMFCore'
  s.version      = '0.1'
  s.summary      = 'AXA Mobile Factory Core library for iOS'
  s.homepage     = 'https://github.com/AXA-GROUP-SOLUTIONS/amf-core-ios.git'
  s.author       = 'AXA Group Solutions'
  s.platform     = :ios, '7.0'
  s.source       = { :git => 'https://github.com/AXA-GROUP-SOLUTIONS/amf-core-ios.git', :branch => 'master' }
  s.source_files = 'AMFCore/**/*.{h,m}'
  s.frameworks 	 = 'Foundation', 'UIKit'
  s.requires_arc = true

  s.dependency 'PureLayout', '2.0.6'
  s.dependency 'JGProgressHUD', '1.2.7'
  s.dependency 'CocoaLumberjack', '2.0.0'
  s.dependency 'RNCryptor', '2.2'

end
