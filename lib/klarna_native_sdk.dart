import 'dart:async';

import 'package:flutter/services.dart';

enum CallbackType {
  onInitialized,
}

typedef CallbackListener = void Function(CallbackType type, dynamic arguments);

class KlarnaNativeSDK {
  static const MethodChannel _channel =
  const MethodChannel('klarna_native_sdk');

  /// return [authToken]
  static Future<String> authorizingSession() async {
    return await _channel
        .invokeMethod('authorizingSession');
  }

  /// return [authToken]
  static Future<String> reauthorize() async {
    return await _channel
        .invokeMethod('reauthorize');
  }

  /// return [authToken]
  static Future<String> finalize() async {
    return await _channel
        .invokeMethod('finalize');
  }

  /// return [showForm]
  static Future<bool> loadPaymentReview() async {
    return await _channel
        .invokeMethod('loadPaymentReview');
  }

  static void setListener(CallbackListener listener) async {
    _channel.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case 'onInitialized':
          listener(CallbackType.onInitialized, call.arguments);
          break;
      }
    });
  }
}