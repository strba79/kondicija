package com.strba.kondicija.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val serviceIntent = Intent(context, TimerService::class.java)
        when (action) {
            ACTION_PAUSE -> {
                serviceIntent.putExtra(EXTRA_ACTION, ACTION_PAUSE)
            }
            ACTION_RESUME -> {
                serviceIntent.putExtra(EXTRA_ACTION, ACTION_RESUME)
            }
        }
        context.startService(serviceIntent)
    }

    companion object {
        const val ACTION_PAUSE = "com.strba.kondicija.ACTION_PAUSE"
        const val ACTION_RESUME = "com.strba.kondicija.ACTION_RESUME"
        const val EXTRA_ACTION = "com.strba.kondicija.EXTRA_ACTION"
    }
}