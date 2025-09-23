# Blink Receipt Camera SDK

Blink Receipt Camera SDK for Android is an SDK that enables you to easily add camera functionality to your app with the purpose of scanning receipts. Internally, the SDK uses the CameraX library. With provided camera management you can easily create an app that scans receipts.

## <a name="processorConfigurations"></a> Processor Architecture Considerations

BlinkReceipt is distributed with ARM64 native library binaries.

ARM64 is the new processor architecture that most new devices use. ARM64 processors are very powerful and also have the possibility to take advantage of new NEON64 SIMD instruction set to quickly process multiple pixels with single instruction.

## <a name=requirements></a> Requirements
- AndroidX
- Min SDK 23+
- Compile SDK: 36+
- Java 17+
