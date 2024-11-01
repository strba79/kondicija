package com.strba.kondicija

import MainPresenter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity(), Contract.View {
    private lateinit var presenter: Contract.Presenter
    private lateinit var inputFragment: InputFragment
    private lateinit var workFragment: WorkFragment
    private lateinit var restFragment: RestFragment
    private lateinit var endFragment: EndFragment
    private var timerService: TimerService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            (presenter as MainPresenter).bindService(timerService!!)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            (presenter as MainPresenter).unbindService()
            timerService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this)

        inputFragment = InputFragment()
        inputFragment.setPresenter(presenter)
        workFragment = WorkFragment()
        restFragment = RestFragment()
        endFragment = EndFragment()
        endFragment.setPresenter(presenter)

        showFragment(inputFragment)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, TimerService::class.java))
    }

    override fun showLogEntry(log: String) {
        // Not used in this context
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
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    fun updateTimer(time: String, setsRemaining: Int, isWork: Boolean) {
        if (isWork) {
            workFragment.updateTimerText(time, setsRemaining)
        } else {
            restFragment.updateTimerText(time, setsRemaining)
        }
    }
}