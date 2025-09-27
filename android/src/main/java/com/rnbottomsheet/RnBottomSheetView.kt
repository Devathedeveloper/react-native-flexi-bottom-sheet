package com.rnbottomsheet

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.accessibility.AccessibilityEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.graphics.Insets
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ViewDragHelper
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerHelper
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Custom view implementing a bottom sheet with snap points and gesture support.
 */
class RnBottomSheetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), NestedScrollingParent3, NestedScrollingChild3 {

    private val dragHelper: ViewDragHelper
    private val snapHelper = SnapHelper(resources)
    private val handleView: View
    private val contentContainer: FrameLayout
    private val sheetContainer: FrameLayout
    private val dimOverlay: DimOverlayView

    private var enableHandlePanningGesture = true
    private var enableContentPanningGesture = true
    private var enablePanDownToClose = false
    private var enableDynamicSizing = false
    private var overDragResistance = 0.2f

    private var currentIndex = -1
    private var currentOffset = 0f
    private var isDragging = false

    private var windowInsets: Insets = Insets.NONE

    private var keyboardBehavior: KeyboardBehavior = KeyboardBehavior.INTERACTIVE

    private val velocityTracker = VelocityTracker.obtain()

    private val animator = ValueAnimator().apply {
        interpolator = DecelerateInterpolator()
        duration = 240
        addUpdateListener { valueAnimator ->
            updateSheetOffset((valueAnimator.animatedValue as Float))
            dispatchOnAnimate()
        }
        addListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animation: ValueAnimator) {
                settleCompleted()
            }
        })
    }

    private var reactBackgroundColor = Color.WHITE
    private var handleHeight = dpToPx(24f)
    private var handleIndicatorColor = Color.LTGRAY
    private var elevationPx = dpToPx(8f)

    init {
        clipToPadding = false
        clipChildren = false
        isFocusable = true
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES

        dimOverlay = DimOverlayView(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            visibility = INVISIBLE
            setOnClickListener { if (enablePanDownToClose) close() }
        }
        addView(dimOverlay)

        sheetContainer = FrameLayout(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
        contentContainer = FrameLayout(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setBackgroundColor(reactBackgroundColor)
            elevation = elevationPx
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                }
            }
            clipToOutline = true
        }

        handleView = LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_handle, sheetContainer, false)
        sheetContainer.addView(handleView)
        sheetContainer.addView(contentContainer)
        super.addView(sheetContainer)

        dragHelper = ViewDragHelper.create(this, dragCallback)

        ViewCompat.setNestedScrollingEnabled(this, true)
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            windowInsets = insets.getInsets(WindowInsetsCompat.Type.ime() or WindowInsetsCompat.Type.systemBars())
            updateInsetOffset()
            insets
        }
    }

    private var cornerRadius = dpToPx(16f)

    fun getContentContainer(): ViewGroup = contentContainer

    fun setSnapPoints(points: List<Any>) {
        post {
            snapHelper.setSnapPoints(points, height)
            if (enableDynamicSizing) {
                val contentHeight = measureContentHeight()
                if (contentHeight > 0) {
                    val dynamicPoints = snapHelper.snapPoints.toMutableList()
                    dynamicPoints.add(contentHeight.toFloat())
                    snapHelper.setSnapPoints(dynamicPoints, height)
                }
            }
            if (currentIndex >= 0) {
                val target = snapHelper.snapPoints.getOrNull(currentIndex) ?: return@post
                animateToOffset(target)
            }
        }
    }

    fun setIndex(index: Int) {
        currentIndex = index
        val target = if (index >= 0) snapHelper.snapPoints.getOrNull(index) ?: 0f else height.toFloat()
        animateToOffset(target)
    }

    fun setEnableContentPanningGesture(enabled: Boolean) {
        enableContentPanningGesture = enabled
    }

    fun setEnableHandlePanningGesture(enabled: Boolean) {
        enableHandlePanningGesture = enabled
        handleView.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun setEnablePanDownToClose(enabled: Boolean) {
        enablePanDownToClose = enabled
        dimOverlay.isClickable = enabled
    }

    fun setEnableDynamicSizing(enabled: Boolean) {
        enableDynamicSizing = enabled
    }

    fun setKeyboardBehavior(value: String?) {
        keyboardBehavior = when (value) {
            "extend" -> KeyboardBehavior.EXTEND
            "fillParent" -> KeyboardBehavior.FILL_PARENT
            else -> KeyboardBehavior.INTERACTIVE
        }
    }

    fun setBackgroundColor(color: Int) {
        reactBackgroundColor = color
        contentContainer.setBackgroundColor(color)
    }

    fun setHandleHeight(height: Int) {
        handleHeight = height.toFloat()
        handleView.layoutParams = handleView.layoutParams.apply { this.height = height }
        handleView.requestLayout()
    }

    fun setHandleIndicatorColor(color: Int) {
        handleIndicatorColor = color
        val indicator = handleView.findViewById<View>(R.id.handle_indicator)
        indicator?.setBackgroundColor(color)
    }

    fun setElevationDp(elevation: Float) {
        elevationPx = elevation
        contentContainer.elevation = elevation
    }

    fun setOverDragResistance(resistance: Float) {
        overDragResistance = resistance.coerceIn(0f, 1f)
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        contentContainer.invalidateOutline()
    }

    fun setAdjustSoftInputMode(mode: String?) {
        val activity = context.findActivity() ?: return
        val window = activity.window ?: return
        when (mode) {
            "adjustResize" -> window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            "adjustPan" -> window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val indicator = handleView.findViewById<View>(R.id.handle_indicator)
        indicator?.setBackgroundColor(handleIndicatorColor)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!enableHandlePanningGesture && !enableContentPanningGesture) return super.onInterceptTouchEvent(ev)
        val shouldIntercept = dragHelper.shouldInterceptTouchEvent(ev)
        if (shouldIntercept) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return shouldIntercept
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        velocityTracker.addMovement(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                velocityTracker.clear()
                dispatchGestureStart()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker.computeCurrentVelocity(1000)
                isDragging = false
                dispatchGestureEnd()
            }
        }
        return true
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun updateSheetOffset(offset: Float) {
        currentOffset = offset
        contentContainer.translationY = offset
        handleView.translationY = offset
        dimOverlay.alpha = (height - offset) / height
        dimOverlay.visibility = if (offset < height) View.VISIBLE else View.INVISIBLE
    }

    private fun animateToOffset(offset: Float) {
        animator.cancel()
        animator.setFloatValues(currentOffset, offset)
        animator.start()
    }

    private fun settleCompleted() {
        val index = snapHelper.findIndexForPosition(currentOffset)
        if (index != currentIndex) {
            val wasClosed = currentIndex == -1
            currentIndex = index
            dispatchChange(index)
            if (wasClosed && index >= 0) dispatchOpen() else if (index < 0) dispatchClose()
        }
    }

    private fun dispatchChange(index: Int) {
        val reactContext = context as? ReactContext ?: return
        UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)?.dispatchEvent(
            RnBottomSheetEvent.Change(reactContext, id, index)
        )
    }

    private fun dispatchOnAnimate() {
        val reactContext = context as? ReactContext ?: return
        UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)?.dispatchEvent(
            RnBottomSheetEvent.Animate(reactContext, id, currentIndex, currentOffset)
        )
    }

    private fun dispatchOpen() {
        val reactContext = context as? ReactContext ?: return
        UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)?.dispatchEvent(
            RnBottomSheetEvent.Open(reactContext, id)
        )
        announceForAccessibility(resources.getString(R.string.bottom_sheet_opened))
    }

    private fun dispatchClose() {
        val reactContext = context as? ReactContext ?: return
        UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)?.dispatchEvent(
            RnBottomSheetEvent.Close(reactContext, id)
        )
        announceForAccessibility(resources.getString(R.string.bottom_sheet_closed))
    }

    private fun dispatchGestureStart() {
        val reactContext = context as? ReactContext ?: return
        UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)?.dispatchEvent(
            RnBottomSheetEvent.GestureStart(reactContext, id)
        )
    }

    private fun dispatchGestureEnd() {
        val reactContext = context as? ReactContext ?: return
        UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)?.dispatchEvent(
            RnBottomSheetEvent.GestureEnd(reactContext, id)
        )
    }

    private val dragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return (child == handleView && enableHandlePanningGesture) || (child == contentContainer && enableContentPanningGesture)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val min = snapHelper.minPoint.toInt()
            val max = height
            val target = (top + dy).coerceIn(min, max)
            return (currentOffset + dy).toInt().coerceIn(min, max)
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            val target = (currentOffset + dy).coerceIn(snapHelper.minPoint, height.toFloat())
            updateSheetOffset(target)
            dispatchOnAnimate()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val target = snapHelper.findClosestSnap(currentOffset + yvel / 4f, yvel)
            animateToOffset(target)
        }
    }

    private fun measureContentHeight(): Int {
        val widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        contentContainer.measure(widthSpec, heightSpec)
        return contentContainer.measuredHeight
    }

    private fun updateInsetOffset() {
        if (keyboardBehavior == KeyboardBehavior.INTERACTIVE) {
            val offset = windowInsets.bottom
            contentContainer.translationY = min(currentOffset, height - offset.toFloat())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (snapHelper.snapPoints.isEmpty()) return
        if (currentIndex >= 0) {
            val target = snapHelper.snapPoints[currentIndex]
            updateSheetOffset(target)
        } else {
            updateSheetOffset(height.toFloat())
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (snapHelper.snapPoints.isNotEmpty()) {
            val target = snapHelper.snapPoints.getOrNull(currentIndex)?.coerceAtMost(h.toFloat())
                ?: snapHelper.snapPoints.lastOrNull() ?: 0f
            updateSheetOffset(target)
        }
    }

    override fun addView(child: View?) {
        when (child) {
            dimOverlay, sheetContainer, handleView, contentContainer -> super.addView(child)
            else -> contentContainer.addView(child)
        }
    }

    override fun addView(child: View?, index: Int) {
        if (child == null) return
        when (child) {
            dimOverlay, sheetContainer, handleView, contentContainer -> super.addView(child, index)
            else -> contentContainer.addView(child, index)
        }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if (child == null) return
        when (child) {
            dimOverlay, sheetContainer, handleView, contentContainer -> super.addView(child, width, height)
            else -> contentContainer.addView(child, width, height)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (child == null) return
        when (child) {
            dimOverlay, sheetContainer, handleView, contentContainer -> super.addView(child, params)
            else -> contentContainer.addView(child, params)
        }
    }

    override fun removeView(view: View?) {
        if (view == null) return
        if (view == dimOverlay || view == sheetContainer || view == handleView || view == contentContainer) {
            super.removeView(view)
        } else {
            contentContainer.removeView(view)
        }
    }

    override fun removeAllViews() {
        contentContainer.removeAllViews()
    }

    // Nested scroll implementation (minimal for interop)
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {}

    override fun onStopNestedScroll(target: View, type: Int) {
        if (!isDragging) return
        isDragging = false
        dispatchGestureEnd()
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        if (dyUnconsumed == 0) return
        val newOffset = (currentOffset + dyUnconsumed).coerceIn(snapHelper.minPoint, height.toFloat())
        updateSheetOffset(newOffset)
        dispatchOnAnimate()
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val direction = dy
        if (direction > 0 && currentOffset > snapHelper.minPoint) {
            val delta = min(direction, (currentOffset - snapHelper.minPoint).toInt())
            updateSheetOffset(currentOffset - delta)
            consumed[1] = delta
            dispatchOnAnimate()
        } else if (direction < 0 && currentOffset < height) {
            val delta = min(abs(direction), (height - currentOffset).toInt())
            updateSheetOffset(currentOffset + delta)
            consumed[1] = delta
            dispatchOnAnimate()
        }
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {}

    override fun onStopNestedScroll(target: View) {}

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean = false

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean = false

    override fun startNestedScroll(axes: Int, type: Int): Boolean = false

    override fun stopNestedScroll(type: Int) {}

    override fun hasNestedScrollingParent(type: Int): Boolean = false

    override fun setNestedScrollingEnabled(enabled: Boolean) {}

    override fun isNestedScrollingEnabled(): Boolean = true

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        event?.text?.add(resources.getString(R.string.bottom_sheet_accessibility_label))
        return super.dispatchPopulateAccessibilityEvent(event)
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = resources.getString(R.string.bottom_sheet_accessibility_label)
    }

    fun expand() {
        if (snapHelper.snapPoints.isEmpty()) return
        currentIndex = snapHelper.snapPoints.lastIndex
        animateToOffset(snapHelper.snapPoints.last())
    }

    fun collapse() {
        if (snapHelper.snapPoints.isEmpty()) return
        currentIndex = 0
        animateToOffset(snapHelper.snapPoints.first())
    }

    fun close() {
        currentIndex = -1
        animateToOffset(height.toFloat())
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    enum class KeyboardBehavior { EXTEND, FILL_PARENT, INTERACTIVE }
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private abstract class SimpleAnimatorListener : ValueAnimator.AnimatorListener {
    override fun onAnimationStart(animation: ValueAnimator) {}
    override fun onAnimationEnd(animation: ValueAnimator) {}
    override fun onAnimationCancel(animation: ValueAnimator) {}
    override fun onAnimationRepeat(animation: ValueAnimator) {}
}
