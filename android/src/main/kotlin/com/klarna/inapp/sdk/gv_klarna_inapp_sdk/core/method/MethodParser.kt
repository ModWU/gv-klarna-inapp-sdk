package com.klarna.inapp.sdk.gv_klarna_inapp_sdk.core.method

import io.flutter.plugin.common.MethodCall

internal interface MethodParser<T> {
    fun parse(call: MethodCall): T?
}