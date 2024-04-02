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
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CuadernoActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCategoriasCuaderno
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID


class PrincipalFragment : Fragment(){
    lateinit var actividad: Activity
    private lateinit var recyclerPictogramas: RecyclerView
    lateinit var adaptador : AdaptadorCategoriasCuaderno
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

        adaptador = AdaptadorCategoriasCuaderno(viewModel.listaPictoCuaderno, viewModel.isPlanificador, viewModel, requireContext(), this)
        recyclerPictogramas.adapter = adaptador

        viewModel.createPickMedia(this, requireContext(), vista)

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
        viewModel._image.observe(viewLifecycleOwner){
            viewModel.image.setImageURI(it)
            viewModel.image.background = null
        }

        viewModel._lastPictoClicked.observe(viewLifecycleOwner){
            mostrarDialogo()
        }

        viewModel._posicionPictoClicked.observe(viewLifecycleOwner){
            val activity = requireActivity() as CuadernoActivity
            viewModel.listaPictogramas = viewModel.picto.obtenerPictogramasCuaderno(activity, viewModel.idCuaderno) as ArrayList<Pictograma>?
            viewModel.originalPictogramas = viewModel.listaPictogramas?.let { ArrayList(it) }
            activity.iniciarFragment(viewModel.listaPictogramas, viewModel.listaPictoCuaderno[it].termometro, viewModel.listaPictoCuaderno[it].titulo!!)
        }
    }

    fun menuCuaderno(cuaderno: Cuaderno, anchorView: View) {
        val inflater = LayoutInflater.from(requireContext())
        val customView = inflater.inflate(R.layout.popup_cuaderno, null)
        val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
       // val position = viewModel.listaPictoCuaderno.indexOfFirst { it.id == cuaderno.id }

        customView.findViewById<TextView>(R.id.item_editar).setOnClickListener {
            editarCuaderno(cuaderno)
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
            viewModel.listaPictoCuaderno.remove(cuaderno)
            adaptador.notifyDataSetChanged()
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


    private fun editarCuaderno(cuaderno: Cuaderno){
        val dialogo = context?.let { Dialog(it) }
        dialogo!!.setContentView(R.layout.dialogo_crear_categoria_cuaderno)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val title : TextInputLayout = dialogo.findViewById(R.id.txt_title)
        val termometro : SwitchCompat =  dialogo.findViewById(R.id.switch_termometro)
        viewModel.image = dialogo.findViewById(R.id.img)

        val btnCrear : Button = dialogo.findViewById(R.id.btn_create)
        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

        viewModel.image.setOnClickListener {
            viewModel.abrirGaleria()
        }

        title.editText?.setText(cuaderno.titulo)
        viewModel.image.setImageURI(Uri.parse(cuaderno.imagen))
        viewModel.image.background = null
        termometro.isChecked = cuaderno.termometro == true
        btnCrear.text = getString(R.string.str_editarCategoria)
        val textView = dialogo.findViewById<TextView>(R.id.lbl_cuaderno)
        textView.text = getString(R.string.str_editarCuaderno)

        btnCrear.setOnClickListener{
            title.error = null
            if (title.editText?.text.toString().isEmpty() || viewModel.image.drawable == null) {
                title.error = "Obligatorio"
                CommonUtils.showSnackbar(dialogo.findViewById(android.R.id.content), requireContext(), "Tienes que rellenar todos los campos")
            }else{
                crearEditarCuaderno(title, termometro, true, cuaderno)
                dialogo.dismiss()
            }
        }
        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

    private fun mostrarDialogo(){
        val dialogo = context?.let { Dialog(it) }
        dialogo!!.setContentView(R.layout.dialogo_crear_categoria_cuaderno)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val title : TextInputLayout = dialogo.findViewById(R.id.txt_title)
        val termometro : SwitchCompat =  dialogo.findViewById(R.id.switch_termometro)
        viewModel.image = dialogo.findViewById(R.id.img)

        val btnCrear : Button = dialogo.findViewById(R.id.btn_create)
        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)

        viewModel.image.setOnClickListener {
            viewModel.abrirGaleria()
        }

        //el termometro por ahora no hace nada
        btnCrear.setOnClickListener{
            title.error = null
            if (title.editText?.text.toString().isEmpty() || viewModel.image.drawable == null) {
                title.error = "Obligatorio"
                CommonUtils.showSnackbar(dialogo.findViewById(android.R.id.content), requireContext(), "Tienes que rellenar todos los campos")
            }else{
                crearEditarCuaderno(title, termometro, false, cuaderno = Cuaderno())
                dialogo.dismiss()
            }
        }

        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

    private fun crearEditarCuaderno(title: TextInputLayout, termometro: SwitchCompat, isEditar: Boolean, cuaderno: Cuaderno){
        val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)
        val idUsuario = prefs?.getString("idUsuario", "")
        val numero = UUID.randomUUID()
        val imagen = context?.let { it1 -> CommonUtils.crearRuta(it1, viewModel.image, "ImgCuaderno$numero") }

        val isTermometro = if (termometro.isChecked) 1 else 0
        var index = -1

        if (idUsuario != null) {
            val id = if(isEditar){
                cuaderno.editarCuaderno(activity, idUsuario, cuaderno.id.toString(), title.editText?.text.toString(), imagen, isTermometro)
                cuaderno.id
            }else{
                cuaderno.crearCuaderno(activity, idUsuario, title.editText?.text.toString(), imagen, isTermometro)
            }

            cuaderno.id = id
            cuaderno.titulo = title.editText?.text.toString()
            cuaderno.imagen = imagen
            cuaderno.termometro = termometro.isChecked

            if(isEditar){
                index = viewModel.listaPictoCuaderno.indexOfFirst { it.id == cuaderno.id }
                viewModel.listaPictoCuaderno[index] = cuaderno
            }else{
                index = viewModel.listaPictoCuaderno.size - 1
                viewModel.listaPictoCuaderno.add(index, cuaderno)
            }
        }

        adaptador.notifyItemChanged(index)
        //adaptador.notifyDataSetChanged()
    }


}