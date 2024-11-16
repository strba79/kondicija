package com.strba.kondicija.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.strba.kondicija.R

class PrepareFragment : Fragment() {
    private lateinit var timerTextView: TextView
    private lateinit var nextStepTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_prepare, container, false)
        timerTextView = view.findViewById(R.id.timer_text_view)
        nextStepTextView = view.findViewById(R.id.tvNextStep)
        return view
    }

    fun updateTimerText(time: String, nextStep: String) {
        timerTextView.text = time
        nextStepTextView.text = nextStep
    }
}