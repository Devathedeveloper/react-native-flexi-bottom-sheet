package com.rnbottomsheet

import android.content.res.Resources
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Utility for normalizing and resolving snap points for the bottom sheet.
 */
internal class SnapHelper(
    private val resources: Resources,
) {
    private val _snapPoints = mutableListOf<Float>()
    val snapPoints: List<Float> get() = _snapPoints

    var minPoint: Float = 0f
        private set

    var maxPoint: Float = 0f
        private set

    fun setSnapPoints(points: List<Any>, containerHeight: Int) {
        require(points.isNotEmpty()) { "snapPoints must not be empty" }
        _snapPoints.clear()
        val resolved = points.map { value ->
            when (value) {
                is Number -> value.toFloat()
                is String -> resolvePercentage(value, containerHeight)
                else -> error("Unsupported snap point $value")
            }
        }.distinct().sorted()
        _snapPoints.addAll(resolved)
        minPoint = resolved.first()
        maxPoint = resolved.last()
    }

    private fun resolvePercentage(value: String, containerHeight: Int): Float {
        val numeric = value.trim().removeSuffix("%")
        val percent = numeric.toFloatOrNull()
            ?: error("Invalid percentage $value")
        return containerHeight * percent / 100f
    }

    fun findClosestSnap(target: Float, velocity: Float): Float {
        if (_snapPoints.isEmpty()) return target
        val direction = when {
            velocity > 400f -> 1
            velocity < -400f -> -1
            else -> 0
        }
        var bestPoint = _snapPoints.first()
        var bestDistance = Float.MAX_VALUE
        for (point in _snapPoints) {
            val distance = abs(point - target)
            if (distance < bestDistance) {
                bestDistance = distance
                bestPoint = point
            }
        }
        if (direction > 0) {
            bestPoint = _snapPoints.firstOrNull { it >= target } ?: _snapPoints.last()
        } else if (direction < 0) {
            bestPoint = _snapPoints.lastOrNull { it <= target } ?: _snapPoints.first()
        }
        return bestPoint
    }

    fun findIndexForPosition(position: Float): Int {
        if (_snapPoints.isEmpty()) return -1
        return _snapPoints.indexOfFirst { abs(it - position) < 1f }
            .takeIf { it >= 0 } ?: run {
            var candidate = 0
            var bestDistance = Float.MAX_VALUE
            _snapPoints.forEachIndexed { index, value ->
                val distance = abs(value - position)
                if (distance < bestDistance) {
                    bestDistance = distance
                    candidate = index
                }
            }
            candidate
        }
    }

    fun clamp(position: Float): Float {
        if (_snapPoints.isEmpty()) return position
        return min(max(position, minPoint), maxPoint)
    }
}
