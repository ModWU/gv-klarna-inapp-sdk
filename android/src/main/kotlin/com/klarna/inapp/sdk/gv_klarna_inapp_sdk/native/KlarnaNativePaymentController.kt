package com.klarna.inapp.sdk.gv_klarna_inapp_sdk.native

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentCategory
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class KlarnaNativePaymentController(context: Context, messenger: BinaryMessenger, viewId: Int, args: Any?) :
        PlatformView,  MethodChannel.MethodCallHandler, KlarnaPaymentViewCallback {

    private val paymentView: KlarnaPaymentView = KlarnaPaymentView(context = context, category = KlarnaPaymentCategory.SLICE_IT, callback = this)
    private var notInitializedView: TextView = TextView(context);
    private var methodChannel: MethodChannel = MethodChannel(messenger, "klarna_native_sdk")

    private lateinit var clientToken: String;
    private lateinit var returnURL: String;
    private var loadArgs: String? = null;
    private var methodResult: MethodChannel.Result? = null;

    private var isInitialized: Boolean = false

    private val context: Context = context;

    init {
        //通信
        methodChannel.setMethodCallHandler(this)
        Log.d("kotlinDebugLog", "KlarnaNativePaymentController init1 => args: $args, viewId: $viewId")

        var layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        notInitializedView.setGravity(Gravity.CENTER);
        notInitializedView.setLayoutParams(layoutParams);
        notInitializedView.setTextColor(context.getResources().getColor(android.R.color.black));
        notInitializedView.setText("Initialization exception")

        if (args is Map<*, *>) {
            val clientToken: String? = args["clientToken"] as String?;
            val returnURL: String? = args["returnURL"] as String?;

            if (clientToken == null || returnURL == null) {
                markNotInitialize("Initialization failed. Neither the 'clientToken'(=$clientToken) nor 'returnURL'(=$returnURL) parameters can be null.");
            } else {
                Log.d("kotlinDebugLog", "KlarnaNativePaymentController init => clientToken: $clientToken, returnURL: $returnURL")
                this.clientToken = clientToken
                this.returnURL = returnURL
                loadArgs = args["loadArgs"] as String?
                paymentView.initialize(clientToken = clientToken, returnURL = returnURL)
            }
        } else {
            markNotInitialize("Initialization failed. Parameter type must be Map.");
        }
    }

    private fun markNotInitialize(message: String) {
        isInitialized = false

        Log.d("kotlinDebugLog", "markNotInitialize message: $message")
        notInitializedView.setText("$message")
        methodChannel.invokeMethod("onInitialized", mapOf(
                "isSuccess" to false,
                "message" to message
        ))
    }


    private fun notInitialized(result: MethodChannel.Result?) {
        result?.error(
                "KlarnaNativeSDKError",
                "KlarnaNativeSDK is not initialized",
                "Call 'KlarnaNativeSDK.initialize' before this."
        )
    }

    // When the payment view is loaded, and the user has confirmed that they want to pay with one of
    // Klarna’s selected payment methods, it’s time to authorize the session.
    //
    // When you are ready to authorize the session, call authorize(). As with load, you can supply an
    // optional string to update the session. You can also specify whether auto-finalization should
    // be turned off; if it is, the user may need to be prompted a second time to input data.
    private fun authorizingSession(call: MethodCall, result: MethodChannel.Result) {
        methodResult = result
        paymentView.authorize(true, /*call.argument<String>("sessionData")*/null)
    }

    // There are cases when you might want to allow your customer to change their order after it has
    // been authorized (e.g. in some form of order/summary view). In these cases, if the order or
    // customer details have changed, you’ll need to reauthorize the session.
    private fun reauthorize(call: MethodCall, result: MethodChannel.Result) {
        methodResult = result
        paymentView.reauthorize(null)
    }

    // If the session needs to be finalized, you’ll need to perform this last step to get an authorization token.
    private fun finalize(call: MethodCall, result: MethodChannel.Result) {
        methodResult = result
        paymentView.finalize(null)
    }

    // If you’d like to allow the user to review their payment after it’s authorized, this can be done in two ways:
    //
    // 1. Render it in a new view:
    //
    //   Create a new KlarnaPaymentView with the same payment method
    //   Call initialize()
    //   Call then loadPaymentReview()
    //
    // 2. Render it in the existing payment view:
    //
    //   Call loadPaymentReview() in the existing view.
    //
    // Only specific payment method categories and countries are currently supported. Contact us to make sure that you can call this method.
    private fun loadPaymentReview(call: MethodCall, result: MethodChannel.Result) {
        methodResult = result
        paymentView.loadPaymentReview()
    }

    private fun isAvailable(call: MethodCall, result: MethodChannel.Result) {
        methodResult = result
        result.success(paymentView.isAvailable())
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (!isInitialized) {
            notInitialized(result)
            return
        }
        when (call.method) {
            "authorizingSession" -> {
                authorizingSession(call, result)
            }
            "reauthorize" -> {
                reauthorize(call, result)
            }
            "finalize" -> {
                finalize(call, result)
            }
            "loadPaymentReview" -> {
                loadPaymentReview(call, result)
            }
            "isAvailable" -> {
                isAvailable(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun getView(): View {
        Log.d("kotlinDebugLog", "getView => isInitialized: $isInitialized")
        return paymentView
    }

    override fun dispose() {
    }

    override fun onAuthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?, finalizedRequired: Boolean?) {
        Log.d("kotlinDebugLog", "onAuthorized, approved: $approved, authToken: $authToken, finalizedRequired: $finalizedRequired")
        if (approved) {
            // the authorization was successful
        } else {
            // user is not approved or might require finalization
        }

        // app needs to call finalize()
        if (finalizedRequired == true) {
            view.finalize(null);
        }

        // authorization is successful, backend may create order
        if (authToken != null) {
            methodResult?.success(authToken)
        } else {
            methodResult?.error(
                    "KlarnaNativeSDKError",
                    "Failed to authorize the session",
                    "Failed to authorize the session, approved: $approved, authToken: $authToken, finalizedRequired: $finalizedRequired."
            )
        }

    }

    override fun onErrorOccurred(view: KlarnaPaymentView, error: KlarnaPaymentsSDKError) {
        Log.d("kotlinDebugLog", "onErrorOccurred, error: $error")
        if (!isInitialized) {
            markNotInitialize("Initialization failed. [${error.name}##${error.action}]${error.message}.");
            return;
        }
        methodResult?.error(
                "KlarnaNativeSDKError",
                error.message,
                "[${error.name}##${error.action}]${error.message}."
        )
    }

    override fun onFinalized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {
        Log.d("kotlinDebugLog", "onFinalized, approved: $approved")
        if (approved) {
            // the authorization was successful
        } else {
            // user is not approved or might require finalization
        }

        if (authToken != null) {
            methodResult?.success(authToken)
        } else {
            methodResult?.error(
                    "KlarnaNativeSDKError",
                    "Failed to finalize the Session",
                    "Failed to finalize the Session, approved: $approved, authToken: $authToken."
            )
        }
    }

    override fun onInitialized(view: KlarnaPaymentView) {
        Log.d("kotlinDebugLog", "onInitialized, loadArgs: $loadArgs")
        view.load(loadArgs);
    }

    override fun onLoadPaymentReview(view: KlarnaPaymentView, showForm: Boolean) {
        Log.d("kotlinDebugLog", "onLoadPaymentReview")
        if (showForm) {
            // successfully loaded the content in the payment view and the content is visible
            methodResult?.success(showForm)
        } else {
            methodResult?.error(
                    "KlarnaNativeSDKError",
                    "Failed to load payment review",
                    "Something went wrong when loading the content and the content is not visible."
            )
        }
    }

    override fun onLoaded(view: KlarnaPaymentView) {
        Log.d("kotlinDebugLog", "onLoaded")
        isInitialized = true
        methodChannel.invokeMethod("onInitialized", mapOf(
                "isSuccess" to true,
                "message" to "Initialization succeeded."
        ))
    }

    override fun onReauthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {
        Log.d("kotlinDebugLog", "onReauthorized, approved: $approved, authToken: $authToken")
        if (approved) {
            // the authorization was successful
        } else {
            // user is not approved or might require finalization
        }

        // authorization is successful, backend may create order
        if (authToken != null) {
            methodResult?.success(authToken)
        } else {
            methodResult?.error(
                    "KlarnaNativeSDKError",
                    "Failed to reauthorizing the Session",
                    "Failed to reauthorizing the Session, approved: $approved, authToken: $authToken."
            )
        }
    }
}