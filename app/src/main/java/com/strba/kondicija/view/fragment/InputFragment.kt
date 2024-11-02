package com.strba.kondicija.view.fragment

import com.strba.kondicija.presenter.MainPresenter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.strba.kondicija.Contract
import com.strba.kondicija.view.MainActivity
import com.strba.kondicija.R

class InputFragment : Fragment() {
    private lateinit var presenter: Contract.Presenter
    private lateinit var startButton: Button
    private lateinit var setsInput: EditText
    private lateinit var workMinutesInput: EditText
    private lateinit var workSecondsInput: EditText
    private lateinit var restInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startButton = view.findViewById(R.id.start_button)
        setsInput = view.findViewById(R.id.sets)
        workMinutesInput = view.findViewById(R.id.work_minutes)
        workSecondsInput = view.findViewById(R.id.work_seconds)
        restInput = view.findViewById(R.id.rest)

        startButton.setOnClickListener {
            val sets = setsInput.text.toString().toIntOrNull() ?: 0
            val workMinutes = workMinutesInput.text.toString().toIntOrNull() ?: 0
            val workSeconds = workSecondsInput.text.toString().toIntOrNull() ?: 0
            val restSeconds = restInput.text.toString().toIntOrNull() ?: 0
            presenter.startTraining(sets, workMinutes, workSeconds, restSeconds)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::presenter.isInitialized) {
            presenter = MainPresenter(requireActivity() as MainActivity)
        }
    }

    fun setPresenter(presenter: Contract.Presenter) {
        this.presenter = presenter
    }
}