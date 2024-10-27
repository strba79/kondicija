package com.strba.kondicija

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder

class TimerService : Service() {
    private val binder = TimerBinder()
    private var timer: CountDownTimer? = null
    private var totalDuration: Long = 0
    private var listener: TimerListener? = null

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun setListener(listener: TimerListener?) {
        this.listener = listener
    }

    fun startTraining(sets: Int, workMinutes: Int, workSeconds: Int, restSeconds: Int) {
        val workTimeInMillis = (workMinutes * 60 + workSeconds) * 1000L
        val restTimeInMillis = restSeconds * 1000L
        totalDuration = 0
        startWorkTimer(1, sets, workTimeInMillis, restTimeInMillis)
    }

    private fun startWorkTimer(currentSet: Int, sets: Int, workTimeInMillis: Long, restTimeInMillis: Long) {
        if (currentSet > sets) {
            listener?.onTrainingComplete(sets, totalDuration)
            return
        }
        listener?.onWorkStart()
        timer = object : CountDownTimer(workTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                listener?.onTimerUpdate("Work time remaining: ${millisUntilFinished / 1000} seconds", sets - currentSet + 1, true)
            }
            override fun onFinish() {
                totalDuration += workTimeInMillis
                listener?.onRestStart()
                startRestTimer(restTimeInMillis, currentSet + 1, sets, workTimeInMillis)
            }
        }.start()
    }

    private fun startRestTimer(restTimeInMillis: Long, currentSet: Int, sets: Int, workTimeInMillis: Long) {
        timer = object : CountDownTimer(restTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                listener?.onTimerUpdate("Rest time remaining: ${millisUntilFinished / 1000} seconds", sets - currentSet + 1, false)
            }
            override fun onFinish() {
                totalDuration += restTimeInMillis
                startWorkTimer(currentSet, sets, workTimeInMillis, restTimeInMillis)
            }
        }.start()
    }

    fun stopTraining() {
        timer?.cancel()
    }

    interface TimerListener {
        fun onTimerUpdate(time: String, setsRemaining: Int, isWork: Boolean)
        fun onWorkStart()
        fun onRestStart()
        fun onTrainingComplete(sets: Int, duration: Long)
    }
}