package com.example.plantea.presentacion.fragmentos

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ActividadActivity
import com.example.plantea.presentacion.viewModels.ActividadViewModel
import com.example.plantea.presentacion.viewModels.CountDownViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.MaterialSharedAxis
import java.util.Locale

class CountDownActividadFragment : Fragment() {

    private lateinit var vista: View
    private val viewModel by activityViewModels<CountDownViewModel>()
    private val viewModelActividad by activityViewModels<ActividadViewModel>()

    private var countDownTimer: CountDownTimer? = null

    private lateinit var timerTextView: TextView
    private lateinit var startButton: MaterialButton
    private lateinit var refreshButton: MaterialButton
    private lateinit var stopButton: MaterialButton
    private lateinit var expandButton: Button
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var cardViewReloj: CardView

    override fun onDestroy() {
        super.onDestroy()
        if(!viewModel.isRunning){
            viewModel.timeLeftInMillis = 0L
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = requireActivity().getSharedPreferences("actividad", 0)
        val editor = prefs.edit()
        editor.putLong("pausedTime", viewModel.timeLeftInMillis)
        editor.putLong("systemTime", System.currentTimeMillis())
        editor.putInt("selectedHour", viewModel.selectedHour)
        editor.putInt("selectedMin", viewModel.selectedMin)
        editor.apply()
        countDownTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        val prefs = requireActivity().getSharedPreferences("actividad", 0)
        val pausedTime = prefs.getLong("pausedTime", 0L)
        val systemTime = prefs.getLong("systemTime", 0L)
        viewModel.selectedHour = prefs.getInt("selectedHour", -1)
        viewModel.selectedMin = prefs.getInt("selectedMin", -1)
        val currentTime = System.currentTimeMillis()
        val timeLeft = pausedTime - (currentTime - systemTime)
        if (timeLeft > 0) {
            viewModel.timeLeftInMillis = timeLeft
            startTimer(timeLeft)
            startButton.setIconResource(R.drawable.svg_pause)
            showControls(true)
        } else {
            viewModel.timeLeftInMillis = 0L
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_countdown_actividad, container, false)

        setTransitions()
        setupViews()
        setupListeners()
        checkExistingTimer()

        return vista
    }

    private fun setTransitions() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    private fun setupViews() {
        with(vista) {
            timerTextView = findViewById(R.id.timerTextView)
            progressBar = findViewById(R.id.progressBar)
            cardViewReloj = findViewById(R.id.cardViewReloj)
            startButton = findViewById(R.id.startButton)
            refreshButton = findViewById(R.id.refreshButton)
            stopButton = findViewById(R.id.stopButton)
            expandButton = findViewById(R.id.expandBtn)
        }
    }

    private fun setupListeners() {
        cardViewReloj.setOnClickListener { onCardViewClick() }
        stopButton.setOnClickListener { stopAndEndTimer() }
        refreshButton.setOnClickListener { refreshTimer() }
        startButton.setOnClickListener { onStartButtonClick() }
        expandButton.setOnClickListener { showExpandedDialog() }
    }

    private fun onCardViewClick() {
        if (viewModel.isRunning) {
            (requireActivity() as ActividadActivity).stopVideo()
            endTimer()
        }

        val picker = viewModel.createReloj().apply {
            addOnPositiveButtonClickListener {
                if (hour != 0 || minute != 0) {
                    viewModel.selectedHour = hour
                    viewModel.selectedMin = minute
                    timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                }
            }
        }

        picker.show(requireFragmentManager(), "TimePicker")
    }

    private fun stopAndEndTimer() {
        (requireActivity() as ActividadActivity).stopVideo()
        endTimer()
    }

    private fun onStartButtonClick() {
        Log.i("CountDownFragment", "Timer started ${viewModel.timeLeftInMillis}")

        if (viewModel.isRunning) {
            pauseTimer()
            startButton.setIconResource(R.drawable.svg_play)
        } else if (viewModel.selectedHour != -1 && viewModel.selectedMin != -1) {
            if (viewModel.timeLeftInMillis == 0L) {
                startTimer(null)
            } else {
                Toast.makeText(requireContext(), "${viewModel.timeLeftInMillis}", Toast.LENGTH_SHORT).show()
                startTimer(viewModel.timeLeftInMillis)
            }
            startButton.setIconResource(R.drawable.svg_pause)
            viewModel.isRunning = true
            showControls(true)
        }
    }

    private fun showControls(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        stopButton.visibility = visibility
        refreshButton.visibility = visibility
        expandButton.visibility = visibility
    }

    private fun checkExistingTimer() {
        if (viewModel.isRunning && viewModel.timeLeftInMillis != 0L) {
            startButton.setIconResource(R.drawable.svg_pause)
            showControls(true)
            startTimer(viewModel.timeLeftInMillis)
            Log.i("CountDownFragment", "Timer started ${viewModel.timeLeftInMillis}")
        }
    }

    private fun showExpandedDialog() {
        val parentActivity = requireActivity() as ActividadActivity
        val dialog = Dialog(parentActivity).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_timer_expand)
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }

        val dialogTimerText = dialog.findViewById<TextView>(R.id.timerTextView)
        val dialogProgressBar = dialog.findViewById<CircularProgressIndicator>(R.id.progressBar)

        val btnStart = dialog.findViewById<MaterialButton>(R.id.startButton)

        btnStart.setOnClickListener {
            if (viewModel.isRunning) {
                pauseTimer()
                btnStart.setIconResource(R.drawable.svg_play)
            } else if (viewModel.selectedHour != -1 && viewModel.selectedMin != -1) {
                startTimer(viewModel.timeLeftInMillis.takeIf { it != 0L })
                btnStart.setIconResource(R.drawable.svg_pause)
                viewModel.isRunning = true
            }
        }

        dialog.findViewById<MaterialButton>(R.id.stopButton).setOnClickListener {
            parentActivity.stopVideo()
            timerTextView = vista.findViewById(R.id.timerTextView)
            progressBar = vista.findViewById(R.id.progressBar)
            endTimer()
            dialog.dismiss()
        }

        dialog.findViewById<MaterialButton>(R.id.refreshButton).setOnClickListener {
            refreshTimer()
        }

        dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo).setOnClickListener {
            dialog.dismiss()
            timerTextView = vista.findViewById(R.id.timerTextView)
            progressBar = vista.findViewById(R.id.progressBar)
        }

        // Redirecciona las referencias temporales a los del dialog
        timerTextView = dialogTimerText
        progressBar = dialogProgressBar

        dialog.show()
    }

    private fun startTimer(timeLeftInMillis: Long?) {
        countDownTimer?.cancel()

        val totalMillis = (viewModel.selectedHour * 3600000L) + (viewModel.selectedMin * 60000L)
        val time = timeLeftInMillis ?: totalMillis

        countDownTimer = object : CountDownTimer(time, 1000) {
            var isShownToast = false

            override fun onTick(millisUntilFinished: Long) {
                viewModel.timeLeftInMillis = millisUntilFinished
                timerTextView.text = viewModel.tick(millisUntilFinished)
                progressBar.progress = ((millisUntilFinished / 1000f) / (totalMillis / 1000f) * 100).toInt()

                if (millisUntilFinished < 61000L && !isShownToast) {
                    context?.let {
                        Toast.makeText(it, getString(R.string.toast_queda_un_minuto), Toast.LENGTH_LONG).show()
                        isShownToast = true
                    }
                }
            }

            override fun onFinish() {
                try{
                    if (isAdded) {
                        (requireActivity() as ActividadActivity).stopVideo()
                    }
                    endTimer()
                    Toast.makeText(requireContext(), getString(R.string.toast_se_termino_tiempo), Toast.LENGTH_SHORT).show()
                }catch (e: IllegalStateException){
                    Log.e("CountDownFragment", "Error stopping video: ${e.message}")
                }

            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        viewModel.isRunning = false
    }

    private fun endTimer() {
        countDownTimer?.cancel()
        viewModel.apply {
            timeLeftInMillis = 0L
            isRunning = false
            selectedHour = -1
            selectedMin = -1
        }
        timerTextView.text = "00:00"
        progressBar.progress = 100
        startButton.setIconResource(R.drawable.svg_play)
        showControls(false)

        Log.i("CountDownFragment", "Timer ended ${viewModel.timeLeftInMillis}")
    }

    private fun refreshTimer() {
        countDownTimer?.cancel()
        progressBar.progress = 100
        if (viewModel.selectedHour != -1 && viewModel.selectedMin != -1) {
            startTimer(null)
            startButton.setIconResource(R.drawable.svg_pause)
        }
    }
}