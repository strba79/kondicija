package com.strba.kondicija.service.viewmodel

import androidx.lifecycle.ViewModel
import com.strba.kondicija.TrainingStep

class TimerViewModel : ViewModel() {
    var _remainingTime: Long = 0
    val remainingTime: Long
        get() = _remainingTime

    var _isPaused: Boolean = false
    val isPaused: Boolean
        get() = _isPaused

    var _totalDuration: Long = 0
    val totalDuration: Long
        get() = _totalDuration

    var _currentStepIndex: Int = 0
    val currentStepIndex: Int
        get() = _currentStepIndex

    val _trainingSequence = mutableListOf<TrainingStep>()
    val trainingSequence: List<TrainingStep>
        get() = _trainingSequence
}