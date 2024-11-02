package com.strba.kondicija.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.strba.kondicija.Contract
import com.strba.kondicija.R

class InputFragment : Fragment() {

    private lateinit var startButton: Button
    private lateinit var setsEditText: EditText
    private lateinit var workMinutesEditText: EditText
    private lateinit var workSecondsEditText: EditText
    private lateinit var restEditText: EditText
    private lateinit var presenter: Contract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)
        startButton = view.findViewById(R.id.start_button)
        setsEditText = view.findViewById(R.id.sets)
        workMinutesEditText = view.findViewById(R.id.work_minutes)
        workSecondsEditText = view.findViewById(R.id.work_seconds)
        restEditText = view.findViewById(R.id.rest)

        view.findViewById<Button>(R.id.sets_minus).setOnClickListener { decreaseValue(setsEditText) }
        view.findViewById<Button>(R.id.sets_plus).setOnClickListener { increaseValue(setsEditText) }
        view.findViewById<Button>(R.id.work_minutes_minus).setOnClickListener { decreaseValue(workMinutesEditText) }
        view.findViewById<Button>(R.id.work_minutes_plus).setOnClickListener { increaseValue(workMinutesEditText) }
        view.findViewById<Button>(R.id.work_seconds_minus).setOnClickListener { decreaseValue(workSecondsEditText) }
        view.findViewById<Button>(R.id.work_seconds_plus).setOnClickListener { increaseValue(workSecondsEditText) }
        view.findViewById<Button>(R.id.rest_minus).setOnClickListener { decreaseValue(restEditText) }
        view.findViewById<Button>(R.id.rest_plus).setOnClickListener { increaseValue(restEditText) }

        startButton.setOnClickListener {
            val sets = setsEditText.text.toString().toIntOrNull() ?: 0
            val workMinutes = workMinutesEditText.text.toString().toIntOrNull() ?: 0
            val workSeconds = workSecondsEditText.text.toString().toIntOrNull() ?: 0
            val restSeconds = restEditText.text.toString().toIntOrNull() ?: 0
            presenter.startTraining(sets, workMinutes, workSeconds, restSeconds)
        }
        return view
    }

    private fun increaseValue(editText: EditText) {
        val value = editText.text.toString().toIntOrNull() ?: 0
        editText.setText((value + 1).toString())
    }

    private fun decreaseValue(editText: EditText) {
        val value = editText.text.toString().toIntOrNull() ?: 0
        if (value > 0) {
            editText.setText((value - 1).toString())
        }
    }

    fun setPresenter(presenter: Contract.Presenter) {
        this.presenter = presenter
    }
}