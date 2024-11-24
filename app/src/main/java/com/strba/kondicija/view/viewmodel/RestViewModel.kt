package com.strba.kondicija.view.viewmodel

import androidx.lifecycle.ViewModel

class RestViewModel : ViewModel() {
    var isTimerRunning: Boolean = true
    var timerText: String = ""
    var setsRemaining: Int = 0
}