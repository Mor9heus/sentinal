package com.sentinal.app.util

import android.content.Context
import com.sentinal.app.service.WatchService

fun Context.startWatchService() = WatchService.start(this)
fun Context.stopWatchService()  = WatchService.stop(this)
