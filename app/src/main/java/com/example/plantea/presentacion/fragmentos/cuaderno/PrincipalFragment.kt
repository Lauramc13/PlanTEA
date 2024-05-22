package com.example.plantea.presentacion.fragmentos.cuaderno

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.AniadirPictoUtils
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CuadernoActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCategoriasCuaderno
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale
import java.util.UUID


class PrincipalFragment : Fragment(){
    lateinit var actividad: Activity
    private lateinit var recyclerPictogramas: RecyclerView
    private lateinit var constraintLayout: ConstraintLayout

    private val viewModel: CuadernoViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_cuaderno_principal, container, false)
        val bundle = this.arguments
        viewModel.listaPictoCuaderno = (bundle!!["key"] as ArrayList<Cuaderno>?)!!
        viewModel.isPlanificador = (bundle["isPlan"] as Boolean)
        recyclerPictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)

        constraintLayout = vista.findViewById(R.id.frameLayout)
        context?.let { CommonUtils.getGridValueCuaderno(vista, context, recyclerPictogramas, constraintLayout, 150, 200) }

        viewModel.adaptadorCuaderno = AdaptadorCategoriasCuaderno(viewModel.listaPictoCuaderno, viewModel.isPlanificador, viewModel, requireContext(), this)
        recyclerPictogramas.adapter = viewModel.adaptadorCuaderno

        AniadirPictoUtils.createPickMedia(viewModel, this)

        observers()

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    private fun observers(){
        viewModel._lastPictoClicked.observe(viewLifecycleOwner){
            AniadirPictoUtils.initializeDialog(viewModel, requireActivity(), this, false, null)
        }

        viewModel._posicionPictoClicked.observe(viewLifecycleOwner){
            val activity = requireActivity() as CuadernoActivity
            viewModel.listaPictogramas = viewModel.picto.obtenerPictogramasCuaderno(activity, viewModel.idCuaderno, Locale.getDefault().language) as ArrayList<Pictograma>?
            viewModel.originalPictogramas = viewModel.listaPictogramas?.let { ArrayList(it) }
            activity.iniciarFragment(viewModel.listaPictogramas, viewModel.listaPictoCuaderno[it].termometro, viewModel.listaPictoCuaderno[it].titulo!!, it)
        }
    }

    fun menuCuaderno(cuaderno: Cuaderno, anchorView: View) {
        val inflater = LayoutInflater.from(requireContext())
        val customView = inflater.inflate(R.layout.popup_cuaderno, null)
        val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
       // val position = viewModel.listaPictoCuaderno.indexOfFirst { it.id == cuaderno.id }

        customView.findViewById<TextView>(R.id.item_editar).setOnClickListener {
            AniadirPictoUtils.initializeDialog(viewModel, requireActivity(), this, false, cuaderno)
            popupWindow.dismiss()
        }

        customView.findViewById<TextView>(R.id.item_borrar).setOnClickListener {
            eliminarCuaderno(cuaderno)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchorView)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun eliminarCuaderno(cuaderno: Cuaderno) {
        //crear dialogo estas seguro que quiere borrar el cuaderno
        val dialogo = Dialog(requireContext())
        dialogo.setContentView(R.layout.dialogo_borrar_cuaderno)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnBorrar : Button = dialogo.findViewById(R.id.btn_borrar)
        val btnCancelar : Button = dialogo.findViewById(R.id.btn_cancelar)
        val btnCerrar : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

        btnBorrar.setOnClickListener {
            cuaderno.eliminarCuaderno(actividad, cuaderno.id)
            viewModel.adaptadorCuaderno.notifyItemRemoved(viewModel.listaPictoCuaderno.indexOf(cuaderno))
            viewModel.listaPictoCuaderno.remove(cuaderno)
            dialogo.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialogo.dismiss()
        }

        btnCerrar.setOnClickListener {
            dialogo.dismiss()
        }
        dialogo.show()
    }

}