package com.strba.kondicija.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.strba.kondicija.R

class RestFragment : Fragment() {
    private lateinit var timerTextView: TextView
    private lateinit var setsRemainingTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rest, container, false)
        timerTextView = view.findViewById(R.id.timer_text_view_)
        setsRemainingTextView = view.findViewById(R.id.sets_remaining_text_view)
        return view
    }

    fun updateTimerText(time: String, setsRemaining: Int) {
        if (::timerTextView.isInitialized && ::setsRemainingTextView.isInitialized) {
            timerTextView.text = time
            setsRemainingTextView.text = "Rests remaining: $setsRemaining"
        }
    }
}