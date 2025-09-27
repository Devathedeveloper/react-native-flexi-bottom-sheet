package com.rnbottomsheet

import android.graphics.Color
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableType
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

@ReactModule(name = RnBottomSheetViewManager.NAME)
class RnBottomSheetViewManager : SimpleViewManager<RnBottomSheetView>() {

    override fun getName(): String = NAME

    override fun createViewInstance(reactContext: ThemedReactContext): RnBottomSheetView {
        return RnBottomSheetView(reactContext)
    }

    @ReactProp(name = "snapPoints")
    fun setSnapPoints(view: RnBottomSheetView, snapPoints: ReadableArray) {
        val list = mutableListOf<Any>()
        for (i in 0 until snapPoints.size()) {
            when (snapPoints.getType(i)) {
                ReadableType.Number -> list.add(snapPoints.getDouble(i).toFloat())
                ReadableType.String -> list.add(snapPoints.getString(i) ?: "0%")
                else -> {}
            }
        }
        view.setSnapPoints(list)
    }

    @ReactProp(name = "index", defaultInt = -1)
    fun setIndex(view: RnBottomSheetView, index: Int) {
        view.setIndex(index)
    }

    @ReactProp(name = "enableContentPanningGesture", defaultBoolean = true)
    fun setEnableContentPanning(view: RnBottomSheetView, enabled: Boolean) {
        view.setEnableContentPanningGesture(enabled)
    }

    @ReactProp(name = "enableHandlePanningGesture", defaultBoolean = true)
    fun setEnableHandle(view: RnBottomSheetView, enabled: Boolean) {
        view.setEnableHandlePanningGesture(enabled)
    }

    @ReactProp(name = "enablePanDownToClose", defaultBoolean = false)
    fun setEnablePanDownToClose(view: RnBottomSheetView, enabled: Boolean) {
        view.setEnablePanDownToClose(enabled)
    }

    @ReactProp(name = "enableDynamicSizing", defaultBoolean = false)
    fun setEnableDynamicSizing(view: RnBottomSheetView, enabled: Boolean) {
        view.setEnableDynamicSizing(enabled)
    }

    @ReactProp(name = "keyboardBehavior")
    fun setKeyboardBehavior(view: RnBottomSheetView, behavior: String?) {
        view.setKeyboardBehavior(behavior)
    }

    @ReactProp(name = "android_keyboardInputMode")
    fun setAdjustInputMode(view: RnBottomSheetView, mode: String?) {
        view.setAdjustSoftInputMode(mode)
    }

    @ReactProp(name = "overDragResistance")
    fun setOverDrag(view: RnBottomSheetView, resistance: Float) {
        view.setOverDragResistance(resistance)
    }

    @ReactProp(name = "backgroundColor")
    fun setBackgroundColor(view: RnBottomSheetView, color: Int?) {
        val resolved = color ?: Color.WHITE
        view.setBackgroundColor(resolved)
    }

    @ReactProp(name = "handleHeight")
    fun setHandleHeight(view: RnBottomSheetView, height: Int) {
        view.setHandleHeight(height)
    }

    @ReactProp(name = "handleIndicatorColor")
    fun setHandleIndicatorColor(view: RnBottomSheetView, color: Int?) {
        color?.let { view.setHandleIndicatorColor(it) }
    }

    @ReactProp(name = "elevation")
    fun setElevation(view: RnBottomSheetView, elevation: Float) {
        view.setElevationDp(elevation)
    }

    override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
        return MapBuilder.of(
            RnBottomSheetEvent.Change.NAME, MapBuilder.of("registrationName", "onChange"),
            RnBottomSheetEvent.Animate.NAME, MapBuilder.of("registrationName", "onAnimate"),
            RnBottomSheetEvent.Open.NAME, MapBuilder.of("registrationName", "onOpen"),
            RnBottomSheetEvent.Close.NAME, MapBuilder.of("registrationName", "onClose"),
            RnBottomSheetEvent.GestureStart.NAME, MapBuilder.of("registrationName", "onGestureStart"),
            RnBottomSheetEvent.GestureEnd.NAME, MapBuilder.of("registrationName", "onGestureEnd")
        )
    }

    companion object {
        const val NAME = "RnBottomSheetView"
    }
}
