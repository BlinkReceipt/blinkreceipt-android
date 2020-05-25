# Blink Receipt Barcode SDK

Blink Receipt Barcode SDK for Android is an SDK that enables you to easily add barcode functionality to your app with the purpose of scanning receipts. With provided camera management you can easily create an app that scans receipts. You can also scan images stored as Android Bitmaps that are loaded either from gallery, network or SD card.

## <a name=processorConfigurations></a> Processor Architecture Considerations

Blink Receipt Barcode is distributed with both ARMv7, ARM64, x86 native library binaries.

ARMv7 architecture gives the ability to take advantage of hardware accelerated floating point operations and SIMD processing with NEON. This gives BlinkReceipt a huge performance boost on devices that have ARMv7 processors. Most new devices (all since 2012.) have ARMv7 processor so it makes little sense not to take advantage of performance boosts that those processors can give. Also note that some devices with ARMv7 processors do not support NEON instruction sets. Most popular are those based on NVIDIA Tegra 2 fall into this category. Since these devices are old by today's standard, BlinkReceipt does not support them.

ARM64 is the new processor architecture that most new devices use. ARM64 processors are very powerful and also have the possibility to take advantage of new NEON64 SIMD instruction set to quickly process multiple pixels with single instruction.

x86 architecture gives the ability to obtain native speed on x86 android devices, like Asus Zenfone 4. Without that, BlinkReceipt will not work on such devices, or it will be run on top of ARM emulator that is shipped with device - this will give a huge performance penalty.

However, there are some issues to be considered:

ARMv7 build of native library cannot be run on devices that do not have ARMv7 compatible processor (list of those old devices can be found here)
ARMv7 processors does not understand x86 instruction set
x86 processors do not understand neither ARM64 nor ARMv7 instruction sets
however, some x86 android devices ship with the builtin ARM emulator - such devices are able to run ARM binaries but with performance penalty. There is also a risk that builtin ARM emulator will not understand some specific ARM instruction and will crash.
ARM64 processors understand ARMv7 instruction set, but ARMv7 processors does not understand ARM64 instructions
if ARM64 processor executes ARMv7 code, it does not take advantage of modern NEON64 SIMD operations and does not take advantage of 64-bit registers it has - it runs in emulation mode
x86_64 processors understand x86 instruction set, but x86 processors do not understand x86_64 instruction set
if x86_64 processor executes x86 code, it does not take advantage of 64-bit registers and use two instructions instead of one for 64-bit operations
LibBlinkBarcodeReceipt.aar archive contains ARMv7, ARM64, x86 and x86_64 builds of native library. By default, when you integrate BlinkReceipt into your app, your app will contain native builds for all processor architectures. Thus, BlinkReceipt will work on ARMv7, ARM64, x86 and x86_64 devices and will use ARMv7 features on ARMv7 devices and ARM64 features on ARM64 devices. However, the size of your application will be rather large.

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 21+
- Compile SDK: 29+
- Java 8+

## <a name=intro></a> Project Integration and Initialization
To add sdk to your android project please add the following to your dependency section in your app `build.gradle`.

```
dependencies {
    implementation 'androidx.appcompat:appcompat:1.0.0'
    
    implementation 'com.squareup.okhttp3:okhttp:4.7.0'
    
    implementation 'com.squareup.retrofit2:retrofit:2.8.2'
    implementation 'com.squareup.retrofit2:converter-gson:2.8.2'
    implementation 'com.squareup.retrofit2:converter-scalars:2.8.2'

    implementation 'com.squareup.okhttp3:logging-interceptor:2.8.2'
    
    implementation 'com.squareup.okio:okio:2.6.0'
    
    implementation "com.google.android.gms:play-services-tasks:17.0.2"
    
    implementation 'com.jakewharton.timber:timber:4.7.1'

    // Lifecycles only (without ViewModel or LiveData)
    implementation 'androidx.lifecycle:lifecycle-runtime:2.2.0'

    implementation 'com.google.zxing:core:3.3.0'
    
    implementation project( ':blinkreceipt-core' )

    implementation project( ':blinkreceipt-camera' )
}
```