package com.ovasta.logisticsapp.base.ext

import org.junit.Assert.assertEquals
import org.junit.Test

class DimensExtTest {

    // Expected values are _8sdp from the sdp-android 1.1.1 resource folders
    private fun eightSdpOn(smallestWidthDp: Int) = 8 * sdpScale(smallestWidthDp)

    @Test
    fun `matches the sdp library buckets`() {
        assertEquals(8.0f, eightSdpOn(280), 0.001f)   // values/ (below 300dp)
        assertEquals(8.0f, eightSdpOn(320), 0.001f)   // values-sw300dp
        assertEquals(8.8f, eightSdpOn(359), 0.001f)   // values-sw330dp
        assertEquals(9.6f, eightSdpOn(360), 0.001f)   // values-sw360dp
        assertEquals(10.4f, eightSdpOn(411), 0.001f)  // values-sw390dp
        assertEquals(11.2f, eightSdpOn(420), 0.001f)  // values-sw420dp
        assertEquals(16.0f, eightSdpOn(600), 0.001f)  // values-sw600dp
        assertEquals(28.8f, eightSdpOn(1280), 0.001f) // values-sw1080dp
    }

    @Test
    fun `scales other sizes the same way`() {
        assertEquals(15.6f, 13 * sdpScale(360), 0.001f)  // _13sdp
        assertEquals(480f, 400 * sdpScale(360), 0.01f)   // _400sdp
        assertEquals(18.2f, 14 * sdpScale(390), 0.001f)  // _14ssp
    }
}
