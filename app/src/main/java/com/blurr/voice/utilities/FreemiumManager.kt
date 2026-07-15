package com.blurr.voice.utilities

class FreemiumManager {

    suspend fun getDeveloperMessage(): String = ""

    suspend fun isUserSubscribed(): Boolean = true

    suspend fun provisionUserIfNeeded() {
        // Local-only build: no remote user provisioning required.
    }

    suspend fun getTasksRemaining(): Long? = null

    suspend fun canPerformTask(): Boolean = true

    suspend fun decrementTaskCount() {
        // Local-only build: no task quota tracking.
    }
}