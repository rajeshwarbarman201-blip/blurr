package com.blurr.voice.utilities

class FreemiumManager {

    companion object {
        const val DAILY_TASK_LIMIT = 15L
    }

    suspend fun getDeveloperMessage(): String = ""

    suspend fun isUserSubscribed(): Boolean = false

    suspend fun provisionUserIfNeeded() {
        // Local-only build: no remote user provisioning.
    }

    suspend fun getTasksRemaining(): Long? = DAILY_TASK_LIMIT

    suspend fun canPerformTask(): Boolean = true

    suspend fun decrementTaskCount() {
        // Local-only build: no subscription or quota tracking.
    }
}