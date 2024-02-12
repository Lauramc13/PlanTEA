package com.example.plantea.presentacion.fragmentos

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.PlanActivity
import com.example.plantea.presentacion.viewModels.CountDownViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.MaterialSharedAxis

class CountDownPlanFragment : Fragment() {
    lateinit var vista: View
    private val viewModel by viewModels<CountDownViewModel>()

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView
    private lateinit var startButton: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var temporizadorButton: Button
    private lateinit var contrainLayout: ConstraintLayout

    var time: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_countdown, container, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        timerTextView = vista.findViewById(R.id.timerTextView)
        progressBar = vista.findViewById(R.id.progressBar)
        startButton = vista.findViewById(R.id.startButton)
        temporizadorButton = vista.findViewById(R.id.showTemporizador)
        contrainLayout = vista.findViewById(R.id.timer)

        temporizadorButton.visibility = View.GONE
        contrainLayout.visibility = View.VISIBLE

        val parentActivity = requireActivity() as PlanActivity
        showTemporizador(parentActivity)

        startButton.setOnClickListener {
            if(viewModel.isRunning) {
                stopTimer()
            } else {
                startTimer()
                viewModel.isRunning = true
                startButton.setIconResource(R.drawable.svg_close)

            }
        }

        parentActivity.viewModel._tituloLiveData.observe(viewLifecycleOwner) {
            showTemporizador(parentActivity)
        }

        return vista
    }

    fun showTemporizador(parentActivity : PlanActivity){
        if(parentActivity.viewModel.evento.duracion == "" || parentActivity.viewModel.evento.duracion == null){
            contrainLayout.visibility = View.GONE
        }else{
            contrainLayout.visibility = View.VISIBLE
            time = parentActivity.viewModel.evento.duracion + ":00"
            timerTextView.text = time
        }
    }

    private fun startTimer() {
        val timeCountDown = time!!.split(":")
        val timeMill = timeCountDown[0].toLong() * 3600000 + timeCountDown[1].toLong() * 60000

        countDownTimer = object : CountDownTimer(timeMill, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = viewModel.tick(millisUntilFinished)
                progressBar.progress = ((millisUntilFinished / 1000).toFloat() / (timeMill / 1000).toFloat() * 100).toInt()
            }

            override fun onFinish() {
                stopTimer()
                Toast.makeText(requireContext(), "Se ha acabado el tiempo!", Toast.LENGTH_SHORT).show()
            }
        }
        countDownTimer.start()
    }

    private fun stopTimer(){
        countDownTimer.cancel()
        viewModel.isRunning = false
        timerTextView.text = time
        startButton.setIconResource(R.drawable.svg_play)
        progressBar.progress = 0
    }
}