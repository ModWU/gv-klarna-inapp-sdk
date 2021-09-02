package com.klarna.inapp.sdk.gv_klarna_inapp_sdk.core.util

import com.google.gson.Gson
import java.lang.reflect.Type

internal object ParserUtil {
    private val gson by lazy { Gson() }

    fun <T> fromJson(json: String?, typeOfT: Type?): T? {
        return gson.fromJson(json, typeOfT)
    }

    inline fun <reified T> fromJson(json: String?): T? {
        return gson.fromJson(json, T::class.java)
    }

    inline fun <reified T> toJson(obj: T?): String? {
        return gson.toJson(obj, T::class.java)
    }
}