package com.klarna.inapp.sdk.gv_klarna_inapp_sdk

internal enum class ResultError(val errorCode: String) {
    UNKNOWN_ERROR("UnknownError"),
    PLUGIN_METHOD_ERROR("KlarnaFlutterPluginMethodError"),
    HYBRID_SDK_ERROR("KlarnaHybridSDKError"),
    WEB_VIEW_ERROR("KlarnaWebViewError"),
    POST_PURCHASE_ERROR("KlarnaPostPurchaseExperienceError")
}