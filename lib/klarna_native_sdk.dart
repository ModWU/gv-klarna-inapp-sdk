import 'dart:async';

import 'package:flutter/services.dart';

enum CallbackType {
  onInitialized,
}

typedef KlarnaNativeCallback = void Function(
    CallbackType type, dynamic arguments);

class KlarnaNativeSDK {
  KlarnaNativeSDK(int viewId)
      : channel = MethodChannel('klarna_native_sdk_$viewId');

  final MethodChannel channel;

  /// return [authToken]
  Future<String> authorizingSession() async {
    return await channel.invokeMethod('authorizingSession');
  }

  /// return [authToken]
  Future<String> reauthorize() async {
    return await channel.invokeMethod('reauthorize');
  }

  /// return [authToken]
  Future<String> finalize() async {
    return await channel.invokeMethod('finalize');
  }

  /// return [showForm]
  Future<bool> loadPaymentReview() async {
    return await channel.invokeMethod('loadPaymentReview');
  }

  /// return [available]
  Future<bool> isAvailable() async {
    return await channel.invokeMethod('isAvailable');
  }

  void setListener(KlarnaNativeCallback callback) {
    channel.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case 'onInitialized':
          callback(CallbackType.onInitialized, call.arguments);
          break;
      }
    });
  }
}
