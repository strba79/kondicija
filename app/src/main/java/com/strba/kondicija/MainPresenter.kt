package com.strba.kondicija

class MainPresenter(private val view: MainActivity, private val model: Contract.Model) : Contract.Presenter, TimerService.TimerListener {
    private var timerService: TimerService? = null

    override fun startTraining(sets: Int, workMinutes: Int, workSeconds: Int, restSeconds: Int) {
        timerService?.startTraining(sets, workMinutes, workSeconds, restSeconds)
    }

    override fun restartTraining() {
        view.showFragment(InputFragment())
    }

    fun bindService(service: TimerService) {
        timerService = service
        timerService?.setListener(this)
    }

    fun unbindService() {
        timerService?.setListener(null)
        timerService = null
    }

    override fun onTimerUpdate(time: String, setsRemaining: Int, isWork: Boolean) {
        view.updateTimer(time, setsRemaining, isWork)
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