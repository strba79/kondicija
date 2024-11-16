package com.strba.kondicija.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.timessquare.CalendarPickerView
import com.strba.kondicija.R
import java.util.*

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val calendarView: CalendarPickerView = view.findViewById(R.id.calendar_view)
        val closeButton: FloatingActionButton = view.findViewById(R.id.close_button)

        val nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 1)

        val today = Date()
        calendarView.init(today, nextYear.time)
            .withSelectedDate(today)

        closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}