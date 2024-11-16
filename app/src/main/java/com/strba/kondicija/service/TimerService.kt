package com.strba.kondicija.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.strba.kondicija.R
import com.strba.kondicija.StepType
import com.strba.kondicija.TrainingStep

class TimerService : Service() {
    private val binder = TimerBinder()
    private var timer: CountDownTimer? = null
    private var totalDuration: Long = 0
    private var listener: TimerListener? = null
    private val trainingSequence = mutableListOf<TrainingStep>()
    private var currentStepIndex = 0

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, getNotification())
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "TimerServiceChannel",
            "Timer Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun getNotification(): Notification {
        return NotificationCompat.Builder(this, "TimerServiceChannel")
            .setContentTitle("Training Timer")
            .setContentText("Training in progress")
            .setSmallIcon(R.drawable.ic_timer)
            .build()
    }

    fun setListener(listener: TimerListener?) {
        this.listener = listener
    }

    fun startTraining(sets: Int, workMinutes: Int, workSeconds: Int, restSeconds: Int) {
        val workTimeInMillis = (workMinutes * 60 + workSeconds) * 1000L
        if (workTimeInMillis <= 0) {
            listener?.onTrainingComplete(0, 0)
            return
        }

        val restTimeInMillis = restSeconds * 1000L
        val prepareTimeInMillis = 3000L // 3 seconds preparation time

        trainingSequence.clear()
        for (i in 1..sets) {
            trainingSequence.add(TrainingStep(StepType.PREPARE, prepareTimeInMillis))
            trainingSequence.add(TrainingStep(StepType.WORK, workTimeInMillis))
            trainingSequence.add(TrainingStep(StepType.PREPARE, prepareTimeInMillis))
            if (restTimeInMillis > 0) {
                trainingSequence.add(TrainingStep(StepType.REST, restTimeInMillis))
            }
        }

        currentStepIndex = 0
        startNextStep()
    }

    private fun startNextStep() {
        if (currentStepIndex < trainingSequence.size) {
            val step = trainingSequence[currentStepIndex]
            val nextStep = if (currentStepIndex + 1 < trainingSequence.size) {
                trainingSequence[currentStepIndex + 1]
            } else {
                null
            }
            val nextState = when (nextStep?.type) {
                StepType.WORK -> "next: Work"
                StepType.REST -> "next: Rest"
                else -> ""
            }

            when (step.type) {
                StepType.PREPARE -> startPrepareTimer(step.duration, nextState)
                StepType.WORK -> startWorkTimer(step.duration)
                StepType.REST -> startRestTimer(step.duration)
                StepType.END -> TODO()
            }
            currentStepIndex++
        } else {
            listener?.onTrainingComplete(trainingSequence.size / 3, totalDuration)
            stopForeground(STOP_FOREGROUND_DETACH)
        }
    }

    private fun startPrepareTimer(duration: Long, nextState: String) {
        if (nextState.isEmpty()) {
            currentStepIndex++
            startNextStep()
            return
        }

        listener?.onPrepareStart()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                listener?.onTimerUpdate(
                    (millisUntilFinished / 1000).toInt(),
                    trainingSequence.size / 3 - currentStepIndex / 3,
                    isWork = false,
                    isPrepare = true,
                    nextState = nextState
                )
            }

            override fun onFinish() {
                startNextStep()
            }
        }.start()
    }

    private fun startWorkTimer(duration: Long) {
        listener?.onWorkStart()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                listener?.onTimerUpdate(
                    (millisUntilFinished / 1000).toInt(),
                    trainingSequence.size / 3 - currentStepIndex / 3,
                    isWork = true,
                    isPrepare = false,
                    nextState = if (currentStepIndex + 1 < trainingSequence.size && trainingSequence[currentStepIndex + 1].type == StepType.REST) "Rest" else ""
                )
            }

            override fun onFinish() {
                totalDuration += duration
                startNextStep()
            }
        }.start()
    }

    private fun startRestTimer(duration: Long) {
        listener?.onRestStart()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                listener?.onTimerUpdate(
                    (millisUntilFinished / 1000).toInt(),
                    trainingSequence.size / 3 - currentStepIndex / 3,
                    isWork = false,
                    isPrepare = false,
                    nextState = if (currentStepIndex + 1 < trainingSequence.size && trainingSequence[currentStepIndex + 1].type == StepType.WORK) "Work" else ""
                )
            }

            override fun onFinish() {
                totalDuration += duration
                startNextStep()
            }
        }.start()
    }

    fun stopTraining() {
        timer?.cancel()
        stopForeground(STOP_FOREGROUND_DETACH)
    }
}

interface TimerListener {
    fun onPrepareStart()
    fun onWorkStart()
    fun onRestStart()
    fun onTimerUpdate(time: Int, setsRemaining: Int, isWork: Boolean, isPrepare: Boolean, nextState: String)
    fun onTrainingComplete(sets: Int, duration: Long)
}