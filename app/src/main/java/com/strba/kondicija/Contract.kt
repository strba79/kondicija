package com.strba.kondicija

interface Contract {
    interface View {
        fun showLogEntry(log: String)
    }

    interface Presenter {
        fun startTraining(sets: Int, workMinutes: Int, workSeconds: Int, restSeconds: Int)
        fun restartTraining()
    }

    interface Model
}