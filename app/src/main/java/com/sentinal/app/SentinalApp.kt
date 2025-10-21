package com.sentinal.app

import android.app.Application
import com.sentinal.app.util.NotificationUtils

class SentinalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Do light, one-time setup here (off the first Activity draw):
        NotificationUtils.ensureChannels(this)
        // add other cheap singletons later if needed
    }
}
