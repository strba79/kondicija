package com.strba.kondicija

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class PrepareFragment : Fragment() {
    private lateinit var prepareTextView: TextView
    private lateinit var secondsTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_prepare, container, false)
        prepareTextView = view.findViewById(R.id.prepare_text_view)
        secondsTextView = view.findViewById(R.id.seconds_text_view)
        return view
    }

    fun updatePrepareText(message: String) {
        prepareTextView.text = message
    }

    fun updateSeconds(seconds: Long) {
        if (this::secondsTextView.isInitialized) {
            secondsTextView.text = "Prepare time remaining: $seconds seconds"
        }
    }
}