package com.strba.kondicija.presenter

import com.strba.kondicija.Contract
import com.strba.kondicija.service.TimerListener
import com.strba.kondicija.view.fragment.InputFragment
import com.strba.kondicija.view.MainActivity
import com.strba.kondicija.view.fragment.PrepareFragment
import com.strba.kondicija.service.TimerService

class MainPresenter(private val view: MainActivity) : Contract.Presenter, TimerListener {
    private var timerService: TimerService? = null
    private var prepareFragment: PrepareFragment? = null

    override fun startTraining(sets: Int, workMinutes: Int, workSeconds: Int, restSeconds: Int) {
        timerService?.startTraining(sets, workMinutes, workSeconds, restSeconds)
    }

    override fun restartTraining() {
        timerService?.stopTraining()
        val inputFragment = InputFragment()
        inputFragment.setPresenter(this)
        view.showFragment(inputFragment)
    }

    fun bindService(service: TimerService) {
        timerService = service
        timerService?.setListener(this)
    }

    fun unbindService() {
        timerService?.setListener(null)
        timerService = null
    }

    override fun onPrepareStart() {
        prepareFragment = PrepareFragment()
        view.showPrepareFragment()
    }

    override fun onTimerUpdate(
        time: Int,
        setsRemaining: Int,
        isWork: Boolean,
        isPrepare: Boolean,
        nextState: String
    ) {
        val minutes = time / 60
        val seconds = time % 60
        view.updateTimer(minutes, seconds, setsRemaining, isWork, isPrepare, nextState)
    }

    override fun onWorkStart() {
        view.showWorkFragment()
    }

    override fun onRestStart() {
        view.showRestFragment()
    }

    override fun onTrainingComplete(sets: Int, duration: Long) {
        view.showEndFragment(sets, duration)
    }
}