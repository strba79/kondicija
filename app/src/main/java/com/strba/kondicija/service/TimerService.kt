package com.strba.kondicija.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.strba.kondicija.R
import com.strba.kondicija.StepType
import com.strba.kondicija.TrainingStep
import com.strba.kondicija.service.viewmodel.TimerViewModel

class TimerService : Service() {
    private lateinit var viewModel: TimerViewModel
    private var timer: CountDownTimer? = null
    private var listener: TimerListener? = null

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun setViewModel(viewModel: TimerViewModel) {
        this.viewModel = viewModel
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, getNotification()) // Ensure this is called immediately

        val action = intent?.getStringExtra(TimerActionReceiver.EXTRA_ACTION)
        if (!::viewModel.isInitialized) {
            Log.e("TimerService", "viewModel is not initialized")
            stopSelf()
            return START_NOT_STICKY
        }
        when (action) {
            TimerActionReceiver.ACTION_PAUSE -> pauseTraining()
            TimerActionReceiver.ACTION_RESUME -> resumeTraining()
        }
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
        val pauseIntent = Intent(this, TimerActionReceiver::class.java).apply {
            action = TimerActionReceiver.ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val resumeIntent = Intent(this, TimerActionReceiver::class.java).apply {
            action = TimerActionReceiver.ACTION_RESUME
        }
        val resumePendingIntent = PendingIntent.getBroadcast(this, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, "TimerServiceChannel")
            .setContentTitle("Training Timer")
            .setContentText("Training in progress")
            .setSmallIcon(R.drawable.ic_timer)
            .addAction(R.drawable.ic_timer, "Pause", pausePendingIntent)
            .addAction(R.drawable.ic_timer, "Resume", resumePendingIntent)
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

        viewModel._trainingSequence.clear()
        for (i in 1..sets) {
            viewModel._trainingSequence.add(TrainingStep(StepType.PREPARE, prepareTimeInMillis))
            viewModel._trainingSequence.add(TrainingStep(StepType.WORK, workTimeInMillis))
            viewModel._trainingSequence.add(TrainingStep(StepType.PREPARE, prepareTimeInMillis))
            if (restTimeInMillis > 0) {
                viewModel._trainingSequence.add(TrainingStep(StepType.REST, restTimeInMillis))
            }
        }

        viewModel._currentStepIndex = 0
        startNextStep()
    }

    private fun startNextStep() {
        if (viewModel.currentStepIndex < viewModel.trainingSequence.size) {
            val step = viewModel.trainingSequence[viewModel.currentStepIndex]
            val nextStep = if (viewModel.currentStepIndex + 1 < viewModel.trainingSequence.size) {
                viewModel.trainingSequence[viewModel.currentStepIndex + 1]
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
        } else {
            listener?.onTrainingComplete(
                viewModel.trainingSequence.size / 3,
                viewModel.totalDuration
            )
            stopForeground(STOP_FOREGROUND_DETACH)
        }
    }

    private fun startPrepareTimer(duration: Long, nextState: String) {
        if (nextState.isEmpty()) {
            if (!viewModel.isPaused) {
                viewModel._currentStepIndex++
            }
            startNextStep()
            return
        }

        listener?.onPrepareStart()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (viewModel.isPaused) {
                    viewModel._remainingTime = millisUntilFinished
                    cancel()
                } else {
                    viewModel._remainingTime = millisUntilFinished
                    listener?.onTimerUpdate(
                        (millisUntilFinished / 1000).toInt(),
                        viewModel.trainingSequence.size / 3 - viewModel.currentStepIndex / 3,
                        isWork = false,
                        isPrepare = true,
                        nextState = nextState
                    )
                }
            }

            override fun onFinish() {
                if (!viewModel.isPaused) {
                    viewModel._currentStepIndex++
                    startNextStep()
                }
            }
        }.start()
    }

    private fun startWorkTimer(duration: Long) {
        listener?.onWorkStart()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (viewModel.isPaused) {
                    viewModel._remainingTime = millisUntilFinished
                    cancel()
                } else {
                    viewModel._remainingTime = millisUntilFinished
                    listener?.onTimerUpdate(
                        (millisUntilFinished / 1000).toInt(),
                        viewModel.trainingSequence.size / 3 - viewModel.currentStepIndex / 3,
                        isWork = true,
                        isPrepare = false,
                        nextState = if (viewModel.currentStepIndex + 1 < viewModel.trainingSequence.size && viewModel.trainingSequence[viewModel.currentStepIndex + 1].type == StepType.REST) "Rest" else ""
                    )
                }
            }

            override fun onFinish() {
                if (!viewModel.isPaused) {
                    viewModel._totalDuration += duration
                    viewModel._currentStepIndex++
                    startNextStep()
                }
            }
        }.start()
    }

    private fun startRestTimer(duration: Long) {
        listener?.onRestStart()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (viewModel.isPaused) {
                    viewModel._remainingTime = millisUntilFinished
                    cancel()
                } else {
                    viewModel._remainingTime = millisUntilFinished
                    listener?.onTimerUpdate(
                        (millisUntilFinished / 1000).toInt(),
                        viewModel.trainingSequence.size / 3 - viewModel.currentStepIndex / 3,
                        isWork = false,
                        isPrepare = false,
                        nextState = if (viewModel.currentStepIndex + 1 < viewModel.trainingSequence.size && viewModel.trainingSequence[viewModel.currentStepIndex + 1].type == StepType.WORK) "Work" else ""
                    )
                }
            }

            override fun onFinish() {
                if (!viewModel.isPaused) {
                    viewModel._totalDuration += duration
                    viewModel._currentStepIndex++
                    startNextStep()
                }
            }
        }.start()
    }

    fun pauseTraining() {
        if (timer != null) {
            timer?.cancel()
            viewModel._isPaused = true
        } else {
            Log.w("TimerService", "Timer is already null")
        }
    }

    fun resumeTraining() {
        if (!::viewModel.isInitialized) {
            Log.e("TimerService", "viewModel is not initialized")
            return
        }
        if (viewModel.isPaused) {
            viewModel._isPaused = false
            startTimer(viewModel.remainingTime)
        }
    }

    private fun startTimer(duration: Long) {
        if (viewModel.currentStepIndex < viewModel.trainingSequence.size) {
            val step = viewModel.trainingSequence[viewModel.currentStepIndex]
            when (step.type) {
                StepType.PREPARE -> startPrepareTimer(duration, getNextState())
                StepType.WORK -> startWorkTimer(duration)
                StepType.REST -> startRestTimer(duration)
                else -> throw IllegalStateException("Unknown step type")
            }
        } else {
            Log.e(
                "TimerService",
                "Index out of bounds: ${viewModel.currentStepIndex}, Size: ${viewModel.trainingSequence.size}"
            )
        }
    }

    fun stopTraining() {
        timer?.cancel()
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun getNextState(): String {
        val nextStep = if (viewModel.currentStepIndex + 1 < viewModel.trainingSequence.size) {
            viewModel.trainingSequence[viewModel.currentStepIndex + 1]
        } else {
            null
        }
        return when (nextStep?.type) {
            StepType.WORK -> "next: Work"
            StepType.REST -> "next: Rest"
            else -> ""
        }
    }
}

interface TimerListener {
    fun onPrepareStart()
    fun onWorkStart()
    fun onRestStart()
    fun onTimerUpdate(
        time: Int,
        setsRemaining: Int,
        isWork: Boolean,
        isPrepare: Boolean,
        nextState: String
    )

    fun onTrainingComplete(sets: Int, duration: Long)
}