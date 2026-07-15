package com.blurr.voice.utilities

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FreemiumManagerTest {

    @Test
    fun `premium access is fully unlocked in local builds`() = runBlocking {
        val manager = FreemiumManager()

        assertTrue(manager.isUserSubscribed())
        assertTrue(manager.canPerformTask())
        assertNull(manager.getTasksRemaining())
    }
}
