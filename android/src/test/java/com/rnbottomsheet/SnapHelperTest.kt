package com.rnbottomsheet

import android.content.res.Resources
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SnapHelperTest {
    private lateinit var resources: Resources
    private lateinit var helper: SnapHelper

    @Before
    fun setup() {
        resources = Mockito.mock(Resources::class.java)
        helper = SnapHelper(resources)
    }

    @Test
    fun `resolves percent snap points`() {
        helper.setSnapPoints(listOf("50%", 100), 1000)
        assertTrue(helper.snapPoints.contains(500f))
    }

    @Test
    fun `find closest snap respects velocity`() {
        helper.setSnapPoints(listOf(0, 200, 400), 1000)
        val snap = helper.findClosestSnap(150f, 1000f)
        assertEquals(200f, snap)
    }

    @Test
    fun `find index for position`() {
        helper.setSnapPoints(listOf(0, 200, 400), 1000)
        assertEquals(1, helper.findIndexForPosition(210f))
    }
}
