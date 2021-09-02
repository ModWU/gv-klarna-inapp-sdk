package com.klarna.inapp.sdk.gv_klarna_inapp_sdk

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference

internal object PluginContext {

    private var weakReferenceActivity: WeakReference<Activity?> = WeakReference(null)
    var activity: Activity?
        get() = weakReferenceActivity.get()
        set(value) {
            weakReferenceActivity = WeakReference(value)
        }

    private var weakReferenceContext: WeakReference<Context?> = WeakReference(null)
    var context: Context?
        get() = weakReferenceContext.get() ?: activity
        set(value) {
            weakReferenceContext = WeakReference(value)
        }
}