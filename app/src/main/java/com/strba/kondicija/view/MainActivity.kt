package com.strba.kondicija.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_calendar -> {
                // Handle calendar icon click
                // For example, start a new activity or show a dialog
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    fun updateTimer(
        minutes: Int,
        seconds: Int,
        setsRemaining: Int,
        isWork: Boolean,
        isPrepare: Boolean,
        nextState: String
    ) {
        val displayTime = String.format("%02d:%02d", minutes, seconds)

        when {
            isPrepare -> prepareFragment.updateTimerText(displayTime, nextState)
            isWork -> workFragment.updateTimerText(displayTime, setsRemaining)
            else -> restFragment.updateTimerText(displayTime, setsRemaining)
        }
    }

    override fun showLogEntry(log: String) {
        TODO("Not yet implemented")
    }
}