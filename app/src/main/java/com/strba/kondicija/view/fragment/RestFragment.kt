package com.strba.kondicija.view.fragment

import BaseFragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.strba.kondicija.R
import com.strba.kondicija.presenter.MainPresenter
import com.strba.kondicija.service.TimerService
import com.strba.kondicija.service.TimerService.LocalBinder
import com.strba.kondicija.view.MainActivity
import com.strba.kondicija.view.viewmodel.RestViewModel

class RestFragment : BaseFragment() {
    private lateinit var timerTextView: TextView
    private lateinit var setsRemainingTextView: TextView
    private lateinit var fab: FloatingActionButton
    private val hideFabHandler = Handler(Looper.getMainLooper())
    private val hideFabRunnable = Runnable { fab.visibility = View.GONE }
    private lateinit var presenter: MainPresenter
    private val viewModel: RestViewModel by viewModels()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBinder
            presenter.bindService(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            presenter.unbindService()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rest, container, false)
        timerTextView = view.findViewById(R.id.timer_text_view_)
        setsRemainingTextView = view.findViewById(R.id.sets_remaining_text_view)
        fab = view.findViewById(R.id.fab)
        presenter = MainPresenter(activity as MainActivity)

        // Bind the TimerService
        val intent = Intent(activity, TimerService::class.java)
        activity?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        fab.setOnClickListener {
            if (viewModel.isTimerRunning) {
                presenter.pauseTraining()
            } else {
                presenter.resumeTraining()
            }
            viewModel.isTimerRunning = !viewModel.isTimerRunning
        }

        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                fab.visibility = View.VISIBLE
                hideFabHandler.removeCallbacks(hideFabRunnable)
                hideFabHandler.postDelayed(hideFabRunnable, 3000) // Hide after 3 seconds
                v.performClick()
            }
            true
        }

        return view
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_rest
    }

    fun updateTimerText(time: String, setsRemaining: Int) {
        if (::timerTextView.isInitialized && ::setsRemainingTextView.isInitialized) {
            timerTextView.text = time
            setsRemainingTextView.text = "$setsRemaining"
            viewModel.timerText = time
            viewModel.setsRemaining = setsRemaining
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unbindService(serviceConnection)
    }
}