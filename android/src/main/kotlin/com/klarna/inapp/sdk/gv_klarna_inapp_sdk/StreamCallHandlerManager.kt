package com.klarna.inapp.sdk.gv_klarna_inapp_sdk

import com.klarna.inapp.sdk.gv_klarna_inapp_sdk.hybrid.KlarnaHybridSDKEventHandler
import io.flutter.plugin.common.EventChannel

internal object StreamCallHandlerManager {

    val streamHandlerMap: Map<String, EventChannel.StreamHandler> = mapOf(
            "klarna_events" to EventCallbackHandler,
            "klarna_errors" to ErrorCallbackHandler,
            "klarna_hybrid_sdk_events" to KlarnaHybridSDKEventHandler
    )
}