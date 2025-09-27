package com.rnbottomsheet

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

internal sealed class RnBottomSheetEvent(name: String, reactTag: Int) : Event<RnBottomSheetEvent>(reactTag) {
    private val eventName = name
    override fun getEventName(): String = eventName

    override fun canCoalesce(): Boolean = false

    override fun dispatch(rctEventEmitter: RCTEventEmitter) {
        rctEventEmitter.receiveEvent(viewTag, eventName, Arguments.createMap())
    }

    class Change(context: ReactContext, viewId: Int, private val index: Int) : RnBottomSheetEvent(NAME, viewId) {
        override fun dispatch(rctEventEmitter: RCTEventEmitter) {
            val map = Arguments.createMap()
            map.putInt("index", index)
            rctEventEmitter.receiveEvent(viewTag, NAME, map)
        }

        companion object { const val NAME = "topChange" }
    }

    class Animate(context: ReactContext, viewId: Int, private val index: Int, private val position: Float) :
        RnBottomSheetEvent(NAME, viewId) {
        override fun dispatch(rctEventEmitter: RCTEventEmitter) {
            val map = Arguments.createMap()
            map.putInt("index", index)
            map.putDouble("position", position.toDouble())
            rctEventEmitter.receiveEvent(viewTag, NAME, map)
        }

        companion object { const val NAME = "topAnimate" }
    }

    class Open(context: ReactContext, viewId: Int) : RnBottomSheetEvent(NAME, viewId) {
        companion object { const val NAME = "topOpen" }
    }

    class Close(context: ReactContext, viewId: Int) : RnBottomSheetEvent(NAME, viewId) {
        companion object { const val NAME = "topClose" }
    }

    class GestureStart(context: ReactContext, viewId: Int) : RnBottomSheetEvent(NAME, viewId) {
        companion object { const val NAME = "topGestureStart" }
    }

    class GestureEnd(context: ReactContext, viewId: Int) : RnBottomSheetEvent(NAME, viewId) {
        companion object { const val NAME = "topGestureEnd" }
    }
}
