package com.strba.kondicija.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.strba.kondicija.Contract
import com.strba.kondicija.R
import com.strba.kondicija.presenter.MainPresenter
import com.strba.kondicija.service.TimerService
import com.strba.kondicija.view.fragment.EndFragment
import com.strba.kondicija.view.fragment.InputFragment
import com.strba.kondicija.view.fragment.PrepareFragment
import com.strba.kondicija.view.fragment.RestFragment
import com.strba.kondicija.view.fragment.WorkFragment

class MainActivity : AppCompatActivity(), Contract.View {
    private lateinit var presenter: Contract.Presenter
    private lateinit var inputFragment: InputFragment
    private lateinit var workFragment: WorkFragment
    private lateinit var prepareFragment: PrepareFragment
    private lateinit var restFragment: RestFragment
    private lateinit var endFragment: EndFragment
    private var timerService: TimerService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            (presenter as MainPresenter).bindService(timerService!!)
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            (presenter as MainPresenter).unbindService()
            timerService = null
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.text_icons))
        setSupportActionBar(toolbar)

        val streakText: TextView = findViewById(R.id.streak_text)
        val streak = getTrainingStreak()

        if (streak >= 1) {
            streakText.visibility = View.VISIBLE
            streakText.text = "Days in streak: $streak"
            streakText.setTextColor(resources.getColor(R.color.text_icons))
        } else {
            streakText.visibility = View.GONE
        }

        prepareFragment = PrepareFragment()
        inputFragment = InputFragment()
        inputFragment.setPresenter(presenter)
        workFragment = WorkFragment()
        restFragment = RestFragment()
        endFragment = EndFragment()
        endFragment.setPresenter(presenter)

        showFragment(inputFragment)
        Intent(this, TimerService::class.java).also { intent ->
            startForegroundService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun getTrainingStreak(): Int {
        val sharedPreferences = getSharedPreferences("training_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("training_streak", 0)
    }

    private fun updateTrainingStreak(newStreak: Int) {
        val sharedPreferences = getSharedPreferences("training_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("training_streak", newStreak)
            apply()
        }
    }

    private fun resetTrainingStreak() {
        updateTrainingStreak(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
        stopService(Intent(this, TimerService::class.java))
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    fun showPrepareFragment() {
        showFragment(prepareFragment)
    }

    fun showWorkFragment() {
        showFragment(workFragment)
    }

    fun showRestFragment() {
        showFragment(restFragment)
    }

    fun showEndFragment(sets: Int, duration: Long) {
        endFragment.displaySummary(sets, duration)
        val streak = getTrainingStreak() + 1
        updateTrainingStreak(streak)
        val streakText: TextView = findViewById(R.id.streak_text)
        streakText.text = "Days in streak: $streak"
        showFragment(endFragment)
    }

    fun showFragment(fragment: Fragment) {
        if (isFinishing || isDestroyed) {
            return
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        if (fragment is InputFragment) {
            toolbar.visibility = View.VISIBLE
        } else {
            toolbar.visibility = View.GONE
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    fun updateTimer(time: String, setsRemaining: Int, isWork: Boolean, isPrepare: Boolean) {
        when {
            isPrepare -> prepareFragment.updateTimerText(time)
            isWork -> workFragment.updateTimerText(time, setsRemaining)
            else -> restFragment.updateTimerText(time, setsRemaining)
        }
    }
    override fun showLogEntry(log: String) {
        TODO("Not yet implemented")
    }
}