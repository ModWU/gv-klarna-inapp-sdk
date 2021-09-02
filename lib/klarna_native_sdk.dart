import 'dart:async';

import 'package:flutter/services.dart';

enum CallbackType {
  onInitialized,
}

typedef CallbackListener = void Function(CallbackType type, dynamic arguments);

class KlarnaNativeSDK {
  static const MethodChannel _channel =
  const MethodChannel('klarna_native_sdk');

  static Future<Null> initialize(String returnUrl) async {
    return await _channel
        .invokeMethod('initialize', <String, dynamic>{'returnUrl': returnUrl});
  }

  static void setListener(CallbackListener listener) async {
    _channel.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case 'onInitialized':
          listener(CallbackType.onInitialized, call.arguments);
          /*print('MethodCall onInitialized => $call, call.arguments type => ${call.arguments?.runtimeType}');
          final bool isSuccess = call.arguments['isSuccess'];
          final String message = call.arguments['message'];
          print('isSuccess: $isSuccess, message: $message');*/
          break;
      }
    });
  }
}