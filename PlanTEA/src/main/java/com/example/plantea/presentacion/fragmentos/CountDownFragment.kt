package com.example.plantea.presentacion.fragmentos

import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.viewModels.CountDownViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.MaterialSharedAxis
import java.util.Locale


class CountDownFragment: Fragment() {
    lateinit var vista: View
    private val viewModel by viewModels<CountDownViewModel>()

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView
    private lateinit var startButton: MaterialButton
    private lateinit var closeButton: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var temporizadorButton: Button
    private lateinit var contrainLayout: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_countdown, container, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        timerTextView = vista.findViewById(R.id.timerTextView)
        progressBar = vista.findViewById(R.id.progressBar)
        startButton = vista.findViewById(R.id.startButton)
        closeButton = vista.findViewById(R.id.closeButton)
        temporizadorButton = vista.findViewById(R.id.showTemporizador)
        contrainLayout = vista.findViewById(R.id.timer)

        temporizadorButton.setOnClickListener {
            contrainLayout.alpha = 0f
            contrainLayout.translationY = contrainLayout.height.toFloat() / 6
            temporizadorButton.visibility = View.GONE
            val interpolator = PathInterpolator(0.05f, 0.6f, 0.15f, 1f)

            contrainLayout.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(interpolator)
                .withStartAction {
                    contrainLayout.visibility = View.VISIBLE
                }
                .start()
        }

        startButton.setOnClickListener {
            if(viewModel.isRunning) {
                endTimer()
            } else {
                val picker = viewModel.createReloj()
                picker.addOnPositiveButtonClickListener {
                    if(picker.hour == 0 && picker.minute == 0){
                        return@addOnPositiveButtonClickListener
                    }else{
                        viewModel.selectedHour = picker.hour
                        viewModel.selectedMin = picker.minute
                        timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.selectedHour, viewModel.selectedMin)
                        startButton.setIconResource(R.drawable.svg_close)
                        startTimer()
                        viewModel.isRunning = true
                        closeButton.visibility = View.INVISIBLE
                    }

                }

                picker.show(requireFragmentManager(), "TimePicker")
            }
        }

        closeButton.setOnClickListener {
            contrainLayout.visibility = View.INVISIBLE
            temporizadorButton.alpha = 0f
            temporizadorButton.translationY = temporizadorButton.height.toFloat()/ 6
            temporizadorButton.visibility = View.VISIBLE

            temporizadorButton.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .start()

        }
        return vista
    }

    private fun startTimer() {
        val time = viewModel.selectedHour.toLong() * 3600000 + viewModel.selectedMin.toLong() * 60000

        countDownTimer = object : CountDownTimer(time, 1000) {

            override fun onTick(millisUntilFinished: Long) {
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
        contrainLayout.visibility = View.INVISIBLE
        temporizadorButton.alpha = 0f
        temporizadorButton.translationY = temporizadorButton.height.toFloat()/ 6
        temporizadorButton.visibility = View.VISIBLE
        closeButton.visibility = View.VISIBLE

        temporizadorButton.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(400)
            .start()

        countDownTimer.cancel()
        viewModel.isRunning = false
        timerTextView.text = "00:00:00"
        startButton.setIconResource(R.drawable.svg_play)
        progressBar.progress = 0
    }

    /*private fun stopTimer(){
        countDownTimer.cancel()
        viewModel.isRunning = false
        timerTextView.text = "00:00:00"
        startButton.setIconResource(R.drawable.svg_play)
        progressBar.progress = 0
    }*/
}