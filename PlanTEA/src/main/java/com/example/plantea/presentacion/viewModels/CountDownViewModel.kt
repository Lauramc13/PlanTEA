package com.example.plantea.presentacion.viewModels

import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Locale

class CountDownViewModel: ViewModel() {
    var isRunning = false
    var selectedHour = -1
    var selectedMin = -1
    var timeLeftInMillis: Long = 0

    fun createReloj(): MaterialTimePicker {

        return MaterialTimePicker.Builder()
            .setTheme(R.style.TimePicker)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("Temporizador")
            .build()
    }

    fun tick(millisUntilFinished: Long): String{
        val secondsLeft = millisUntilFinished / 1000
        val hours = secondsLeft / 3600
        return if(hours == 0L){
            String.format(Locale.getDefault(), "%02d:%02d", secondsLeft / 60 % 60, secondsLeft % 60)
        }else{
            String.format(Locale.getDefault(), "%02d:%02d", hours, secondsLeft / 60 % 60, secondsLeft % 60)
        }
    }
}