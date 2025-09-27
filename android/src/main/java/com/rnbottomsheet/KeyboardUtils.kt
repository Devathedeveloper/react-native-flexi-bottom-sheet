package com.rnbottomsheet

import android.app.Activity
import android.view.ViewTreeObserver
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Listens for IME changes and notifies the supplied callback.
 */
class KeyboardUtils(private val activity: Activity) {

    private var listener: ((Boolean, Int) -> Unit)? = null
    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    fun setKeyboardListener(callback: (visible: Boolean, height: Int) -> Unit) {
        listener = callback
        val decorView = activity.window?.decorView ?: return
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val insets = WindowInsetsCompat.toWindowInsetsCompat(decorView.rootWindowInsets)
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val height = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            listener?.invoke(imeVisible, height)
        }
        decorView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    fun dispose() {
        val decorView = activity.window?.decorView ?: return
        globalLayoutListener?.let { decorView.viewTreeObserver.removeOnGlobalLayoutListener(it) }
        globalLayoutListener = null
        listener = null
    }

    fun hideKeyboard() {
        val controller = activity.window?.let { WindowInsetsControllerCompat(it, it.decorView) }
        controller?.hide(WindowInsetsCompat.Type.ime())
    }
}
