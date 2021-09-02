package com.klarna.inapp.sdk.gv_klarna_inapp_sdk.core.util

internal fun Any?.toJson(): String? {
    return ParserUtil.toJson(this)
}