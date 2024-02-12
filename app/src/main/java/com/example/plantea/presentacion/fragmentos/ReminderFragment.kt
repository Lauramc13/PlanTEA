package com.example.plantea.presentacion.fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.plantea.R
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.google.android.material.transition.MaterialSharedAxis
import java.util.Locale

class ReminderFragment : Fragment() {
    lateinit var vista: View

    private val viewModel by viewModels<CalendarioViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.dialog_reminder, container, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        val checkBoxPersonalizar = vista.findViewById<SwitchCompat>(R.id.checkBox_personalizar)
        val checkboxMin = vista.findViewById<SwitchCompat>(R.id.checkBox_min)
        val checkboxHora = vista.findViewById<SwitchCompat>(R.id.checkBox_hora)
        val checkboxDia = vista.findViewById<SwitchCompat>(R.id.checkBox_dia)

        checkBoxPersonalizar.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxPer = isChecked
            if (isChecked) {
                val picker = viewModel.createReloj()
                picker.addOnPositiveButtonClickListener {
                    viewModel.selectedHour = picker.hour
                    viewModel.selectedMin = picker.minute
                    checkBoxPersonalizar.text = "A las " + String.format(Locale.getDefault(), "%02d:%02d", viewModel.selectedHour, viewModel.selectedMin)
                }
                picker.show(requireFragmentManager(), "TimePicker")
            }
        }

        checkboxMin.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxMin = isChecked
        }

        checkboxHora.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxHora = isChecked
        }

        checkboxDia.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxDia = isChecked
        }


        return vista
    }

}