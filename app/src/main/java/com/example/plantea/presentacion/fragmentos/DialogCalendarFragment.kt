package com.example.plantea.presentacion.fragmentos

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.viewModels.PlanViewModel
import java.time.LocalDate

class DialogCalendarFragment : DialogFragment() {
    private lateinit var fechaActual: TextView
    private lateinit var btnSiguienteMes: ImageView
    private lateinit var btnAnteriorMes: ImageView
    private lateinit var calendario: RecyclerView
    private lateinit var cerrarDialog: Button
    private lateinit var adaptadorCalendario: AdaptadorCalendario
    var viewModel: PlanViewModel = PlanViewModel()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogo_calendario)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        fechaActual = dialog.findViewById(R.id.lbl_mes2)
        btnSiguienteMes = dialog.findViewById(R.id.image_calendar_siguiente2)
        btnAnteriorMes = dialog.findViewById(R.id.image_calendar_anterior2)
        calendario = dialog.findViewById(R.id.recycler_calendario)
        cerrarDialog = dialog.findViewById(R.id.icono_CerrarDialogoEvento)

        btnAnteriorMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
            viewModel.obtenerVistaMes()
        }

        btnSiguienteMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
            viewModel.obtenerVistaMes()
        }

        cerrarDialog.setOnClickListener {
            dialog.dismiss()
            viewModel._dismissDialog.value = false
        }

        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
        viewModel.obtenerVistaMes()
        context?.let { viewModel.obtenerEventosFecha(it) }

        viewModel._fechaActual.observe(this) { fechaActual.text = it }
        viewModel._diasMes.observe(this) {
            calendario.layoutManager = GridLayoutManager(context, 7)
            adaptadorCalendario = AdaptadorCalendario(it, viewModel.planificaciones, viewModel)
            calendario.adapter = adaptadorCalendario
        }

        viewModel._dismissDialog.observe(this) {
            if (it) {
                dialog.dismiss()
            }
        }
        return dialog
    }

    fun notificarCambio() {
        adaptadorCalendario.notifyDataSetChanged()
    }


}