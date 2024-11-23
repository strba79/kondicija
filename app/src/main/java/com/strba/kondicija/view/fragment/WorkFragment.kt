package com.strba.kondicija.view.fragment

import BaseFragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.strba.kondicija.R


class WorkFragment : BaseFragment() {
    private lateinit var timerTextView: TextView
    private lateinit var setsRemainingTextView: TextView
    private lateinit var fab: FloatingActionButton
    private val hideFabHandler = Handler(Looper.getMainLooper())
    private val hideFabRunnable = Runnable { fab.visibility = View.GONE }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_work, container, false)
        timerTextView = view.findViewById(R.id.timer_text_view_)
        setsRemainingTextView = view.findViewById(R.id.sets_remaining_text_view)
        fab = view.findViewById(R.id.fab)

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
        return R.layout.fragment_work
    }

    fun updateTimerText(time: String, setsRemaining: Int) {
        timerTextView.text = time
        setsRemainingTextView.text = "$setsRemaining"
    }
}