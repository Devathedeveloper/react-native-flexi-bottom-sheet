package com.rnbottomsheet

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.UIManagerHelper

class BottomSheetCommandsModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = NAME

    @ReactMethod
    fun snapToIndex(tag: Int, index: Int) {
        resolveView(tag)?.setIndex(index)
    }

    @ReactMethod
    fun expand(tag: Int) {
        resolveView(tag)?.expand()
    }

    @ReactMethod
    fun collapse(tag: Int) {
        resolveView(tag)?.collapse()
    }

    @ReactMethod
    fun close(tag: Int) {
        resolveView(tag)?.close()
    }

    @ReactMethod
    fun setSnapPoints(tag: Int, snapPoints: ReadableArray) {
        val list = mutableListOf<Any>()
        for (i in 0 until snapPoints.size()) {
            when (snapPoints.getType(i)) {
                com.facebook.react.bridge.ReadableType.Number -> list.add(snapPoints.getDouble(i).toFloat())
                com.facebook.react.bridge.ReadableType.String -> list.add(snapPoints.getString(i) ?: "0%")
                else -> {}
            }
        }
        resolveView(tag)?.setSnapPoints(list)
    }

    private fun resolveView(tag: Int): RnBottomSheetView? {
        val uiManager = UIManagerHelper.getUIManagerForReactTag(reactContext, tag)
        return uiManager?.resolveView(tag) as? RnBottomSheetView
    }

    companion object {
        const val NAME = "BottomSheetCommandsModule"
    }
}
