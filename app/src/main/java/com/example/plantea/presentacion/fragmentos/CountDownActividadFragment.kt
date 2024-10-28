package com.example.plantea.presentacion.fragmentos

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ActividadActivity
import com.example.plantea.presentacion.viewModels.CountDownViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.MaterialSharedAxis
import java.util.Locale


class CountDownActividadFragment: Fragment() {
    lateinit var vista: View
    private val viewModel by viewModels<CountDownViewModel>()

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView
    private lateinit var startButton: MaterialButton
    private lateinit var pauseButton: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator

    override fun onDestroy() {
        super.onDestroy()
        if (viewModel.isRunning) {
            countDownTimer.cancel()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_countdown_actividad, container, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        timerTextView = vista.findViewById(R.id.timerTextView)
        progressBar = vista.findViewById(R.id.progressBar)
        startButton = vista.findViewById(R.id.startButton)
        pauseButton = vista.findViewById(R.id.pauseButton)

        startButton.setOnClickListener {
            if (viewModel.isRunning) {
                endTimer()
                pauseButton.isClickable = false
            } else {
                pauseButton.isClickable = true
                val picker = viewModel.createReloj()
                picker.addOnPositiveButtonClickListener {
                    if (picker.hour == 0 && picker.minute == 0) {
                        return@addOnPositiveButtonClickListener
                    } else {
                        viewModel.selectedHour = picker.hour
                        viewModel.selectedMin = picker.minute

                        if(picker.hour == 0){
                            timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.selectedMin, 0)
                        }else{
                            timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.selectedHour, viewModel.selectedMin)
                        }

                        startButton.setIconResource(R.drawable.svg_close_thick)
                        startTimer(null)
                        viewModel.isRunning = true
                    }

                }

                picker.show(requireFragmentManager(), "TimePicker")
            }
        }

        pauseButton.setOnClickListener {
            if (viewModel.isRunning) {
                countDownTimer.cancel()
                viewModel.isRunning = false
                pauseButton.setIconResource(R.drawable.svg_play)
            }else{
                startTimer(viewModel.timeLeftInMillis)
                viewModel.isRunning = true
                pauseButton.setIconResource(R.drawable.svg_stop)
            }
        }

        return vista
    }

    private fun startTimer(timeLeftInMillis: Long?) {

        val time = timeLeftInMillis
            ?: (viewModel.selectedHour.toLong() * 3600000 + viewModel.selectedMin.toLong() * 60000)

        countDownTimer = object : CountDownTimer(time, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                viewModel.timeLeftInMillis = millisUntilFinished
                timerTextView.text = viewModel.tick(millisUntilFinished)
                progressBar.progress = ((millisUntilFinished / 1000).toFloat() / (time / 1000).toFloat() * 100).toInt()
            }

            override fun onFinish() {
                val parentActivity = requireActivity() as ActividadActivity
                parentActivity.stopVideo()
                endTimer()
                Toast.makeText(requireContext(), getString(R.string.toast_se_termino_tiempo), Toast.LENGTH_SHORT).show()
            }
        }
        countDownTimer.start()
    }

    private fun endTimer(){
        countDownTimer.cancel()
        viewModel.timeLeftInMillis = 0
        viewModel.isRunning = false
        timerTextView.text = "00:00"
        startButton.setIconResource(R.drawable.svg_play)
        progressBar.progress = 100
    }

}