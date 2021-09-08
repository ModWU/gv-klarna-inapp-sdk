import 'dart:async';

import 'package:flutter/services.dart';

enum CallbackType {
  onInitialized,
}

typedef KlarnaNativeCallback = void Function(CallbackType type, dynamic arguments);

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

  /// return [available]
  static Future<bool> isAvailable() async {
    return await _channel
        .invokeMethod('isAvailable');
  }

  static void setListener(KlarnaNativeCallback callback) {
    _channel.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case 'onInitialized':
          callback(CallbackType.onInitialized, call.arguments);
          break;
      }
    });
  }
}