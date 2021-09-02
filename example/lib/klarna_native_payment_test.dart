import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:gv_klarna_inapp_sdk/klarna_native_sdk.dart';

Widget get paymentTestWidget {
  return MaterialApp(
    home: _NativePaymentTestWidget(),
  );
}

class _NativePaymentTestWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _NativePaymentTestWidgetState();
}

class _NativePaymentTestWidgetState extends State<_NativePaymentTestWidget> {
  static const StandardMessageCodec _codec = StandardMessageCodec();

  late AndroidViewController _androidViewController;

  @override
  void initState() {
    super.initState();
    _androidViewController = PlatformViewsService.initSurfaceAndroidView(
      id: 0,
      viewType: "klarna_native_payment_view",
      layoutDirection: TextDirection.ltr,
      creationParams: {
        "clientToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjAwMDAwMDAwMDAtMDAwMDAtMDAwMC0wMDAwMDAwMC0wMDAwIiwidXJsIjoiaHR0cHM6Ly9jcmVkaXQtZXUua2xhcm5hLmNvbSJ9.A_rHWMSXQN2NRNGYTREBTkGwYwtm-sulkSDMvlJL87M",
        "returnURL": null
      },
      creationParamsCodec: _codec,
    );
    _androidViewController.create();

    KlarnaNativeSDK.setListener((CallbackType type, dynamic arguments) {
      if (type == CallbackType.onInitialized) {
        final bool isSuccess = arguments['isSuccess'];
        final String message = arguments['message'];
        print('KlarnaNativeSDK listener => onInitialized => isSuccess: $isSuccess, message: $message');
      }
    });
  }

  @override
  void dispose() {
    _androidViewController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              color: Colors.black12,
              height: 120,
              child: AndroidViewSurface(
                controller: _androidViewController,
                hitTestBehavior: PlatformViewHitTestBehavior.opaque,
                gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>[
                  Factory<OneSequenceGestureRecognizer>(
                        () => EagerGestureRecognizer(),
                  ),
                ].toSet(),
              ),
            ),
            Text("AndroidView")
          ],
        ),
      ),
    );
  }
}
