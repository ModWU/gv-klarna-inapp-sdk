package com.klarna.inapp.sdk.gv_klarna_inapp_sdk.hybrid

import android.webkit.WebView
import com.klarna.inapp.sdk.gv_klarna_inapp_sdk.ErrorCallbackHandler
import com.klarna.mobile.sdk.KlarnaMobileSDKError
import com.klarna.mobile.sdk.api.OnCompletion
import com.klarna.mobile.sdk.api.hybrid.KlarnaHybridSDKCallback

internal class KlarnaHybridSDKCallback : KlarnaHybridSDKCallback {

    override fun didHideFullscreenContent(webView: WebView, completion: OnCompletion) {
        completion.run()
    }

    override fun didShowFullscreenContent(webView: WebView, completion: OnCompletion) {
        completion.run()
    }

    override fun onErrorOccurred(webView: WebView, error: KlarnaMobileSDKError) {
        ErrorCallbackHandler.sendValue("${error.name}:${error.message}:${error.isFatal}")
    }

    override fun willHideFullscreenContent(webView: WebView, completion: OnCompletion) {
        completion.run()
    }

    override fun willShowFullscreenContent(webView: WebView, completion: OnCompletion) {
        completion.run()
    }
}