package com.strba.kondicija.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.strba.kondicija.Contract
import com.strba.kondicija.R
import com.strba.kondicija.view.MainActivity
import java.util.Date

class EndFragment : Fragment() {
    private lateinit var summaryTextView: TextView
    private lateinit var restartButton: Button
    private lateinit var presenter: Contract.Presenter
    private var sets: Int = 0
    private var duration: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_end, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        summaryTextView = view.findViewById(R.id.summary_text_view)
        restartButton = view.findViewById(R.id.restart_button)
        val addButton: Button = view.findViewById(R.id.add_to_calendar_button)
        addButton.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.addTrainingSession(Date())
        }

        restartButton.setOnClickListener {
            presenter.restartTraining()
        }

        displaySummary(sets, duration)
    }

    fun setPresenter(presenter: Contract.Presenter) {
        this.presenter = presenter
    }

    fun displaySummary(sets: Int, duration: Long) {
        this.sets = sets
        this.duration = duration
        if (this::summaryTextView.isInitialized) {
            val durationMinutes = duration / 60000
            val durationSeconds = (duration % 60000) / 1000
            summaryTextView.text = "Training completed!\nSets: $sets\nDuration: ${durationMinutes}m ${durationSeconds}s"
        }
    }
}