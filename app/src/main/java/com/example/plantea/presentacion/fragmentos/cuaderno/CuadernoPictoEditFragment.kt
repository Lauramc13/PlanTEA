package com.example.plantea.presentacion.fragmentos.cuaderno

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.AniadirPictoUtils
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class CuadernoPictoEditFragment : Fragment(), AdaptadorPictogramasCuaderno.OnItemSelectedListener{
    lateinit var actividad: Activity
    lateinit var lstPictogramas: RecyclerView
    private lateinit var imageCerrar: ImageView
    private lateinit var imageAtras: ImageView
    private lateinit var adaptador: AdaptadorPictogramasCuaderno
    lateinit var termometro: LinearLayout
    lateinit var searchBar: SearchView
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var seekbar: SeekBar

    private val viewModel: CuadernoViewModel by activityViewModels()

    private lateinit var vista: View

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas_edit, container, false)
        val bundle = arguments

        viewModel.listaPictogramas = (bundle?.get("key") as ArrayList<Pictograma>?)!!
        viewModel.isTermometro = (bundle?.get("termometro") as Boolean)

        if(viewModel.listaPictogramas?.isEmpty() == true){
            viewModel.listaPictogramas!!.add(Pictograma(getString(R.string.lbl_NuevoPicto).uppercase(), "archivo", 0, 0))
        }else{
            if(viewModel.listaPictogramas?.last()?.titulo != getString(R.string.lbl_NuevoPicto).uppercase() ) {
                viewModel.listaPictogramas!!.add(Pictograma(getString(R.string.lbl_NuevoPicto).uppercase(), "archivo", 0, 0))
            }
        }

        searchBar = vista.findViewById(R.id.searchViewPicto)
        imageCerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        imageAtras = vista.findViewById(R.id.icono_cuaderno_fragment_atras)
        lstPictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)
        val txtCuaderno = vista.findViewById<TextView>(R.id.titulo_cuaderno)
        txtCuaderno.text = viewModel.tituloCuaderno

        constraintLayout = vista.findViewById(R.id.frameLayout)
        CommonUtils.getGridValueCuaderno(vista, context, lstPictogramas, constraintLayout, 150, 200)

        val prefs = context?.getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.isPlanificador = prefs?.getBoolean("PlanificadorLogged", false) == true

        adaptador = viewModel.isPlanificador.let { context?.let { it1 -> AdaptadorPictogramasCuaderno(viewModel.listaPictogramas, it, this, it1) }}!!
        adaptador.isBusqueda = viewModel.isBusqueda
        adaptador.listaPictosAgregados = viewModel.listaPictosAgregados

        lstPictogramas.adapter = adaptador

        if(viewModel.isBusqueda){
            imageCerrar.visibility = View.INVISIBLE
            imageAtras.visibility = View.VISIBLE
            viewModel.listaPictogramas!!.removeAt(viewModel.listaPictogramas!!.lastIndex)
            adaptador.notifyDataSetChanged()
        }


        imageCerrar.setOnClickListener {
            viewModel._cerrarFragment.value = true
        }

        imageAtras.setOnClickListener {
            searchBar.setQuery("", false)
            searchBar.clearFocus()
            updateData(viewModel.originalPictogramas)
            viewModel.isBusqueda = false
        }

        busqueda()

        context?.let { CommonUtils.initializeTextToSpeech(it) }

        AniadirPictoUtils.createPickMedia(viewModel, this)

        return vista
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    private fun busqueda(){
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!CommonUtils.isNetworkAvailable(requireContext())) {
                    Toast.makeText(requireContext(), R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
                    searchBar.setQuery("", false)
                    searchBar.clearFocus()
                }else {
                    viewModel._queryBusqueda.value = query.trim()
                }
                context?.let { CommonUtils.hideKeyboard(it, searchBar) }
                return true

            }
            override fun onQueryTextChange(newText: String): Boolean {
                newText.trim()
                return true
            }
        })
    }

    fun updateDataRemove(pictograma: Pictograma){
        adaptador.isBusqueda = false
        if(!viewModel.isBusqueda){
            adaptador.notifyItemRemoved(viewModel.listaPictogramas!!.indexOf(pictograma))
        }
        viewModel.listaPictogramas!!.remove(pictograma)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateData(newPictogramasList: ArrayList<Pictograma>?){
        viewModel.listaPictogramas!!.clear()
        if (newPictogramasList != null) {
            viewModel.listaPictogramas!!.addAll(newPictogramasList)
        }

        if(viewModel.listaPictogramas!!.lastIndex != 0) {
            viewModel.listaPictogramas!!.add(Pictograma(getString(R.string.lbl_NuevoPicto).uppercase(), "archivo", 0, 0))
        }

        adaptador.isBusqueda = false
        activity?.runOnUiThread {
            adaptador.notifyDataSetChanged()
        }

        imageCerrar.visibility = View.VISIBLE
        imageAtras.visibility = View.INVISIBLE
    }

    private fun mostrarDialogoCrearPicto(){
        checkIfFragmentAttached {
            AniadirPictoUtils.initializeDialog(viewModel, requireActivity(), this@CuadernoPictoEditFragment, false, null)
        }
    }

    override fun pictogramaCuaderno(posicion: Int) {
        if(!viewModel.isBusqueda) {
            if (posicion == viewModel.listaPictogramas?.lastIndex && viewModel.isPlanificador) {
                mostrarDialogoCrearPicto()
            } else {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialogo_termometro)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
                val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
                val iconoEscuchar = dialog.findViewById<ImageView>(R.id.icono_escuchar)
                seekbar = dialog.findViewById(R.id.seekBar_termometro)
                termometro = dialog.findViewById(R.id.termometro)
                val identifier = context?.resources?.getIdentifier(
                    viewModel.listaPictogramas?.get(posicion)?.imagen,
                    "drawable",
                    requireContext().packageName
                )
                if (identifier == 0) {
                    pictograma.setImageURI(Uri.parse(viewModel.listaPictogramas?.get(posicion)?.imagen))
                } else {
                    if (identifier != null) {
                        pictograma.setImageResource(identifier)
                    }
                }
                tituloPictograma.text = viewModel.listaPictogramas?.get(posicion)?.titulo
                if (!viewModel.isTermometro) {
                    termometro.visibility = View.GONE
                }

                iconoEscuchar.setOnClickListener {
                    CommonUtils.textToSpeechWord(viewModel.listaPictogramas?.get(posicion)?.titulo)
                }

                //Botón cerrar
                imageCerrar = dialog.findViewById(R.id.icono_CerrarDialogoEvento)
                imageCerrar.setOnClickListener { dialog.dismiss() }

                //Funcionalidad termómetro: cambio de color según el progreso
                seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        // Write code to perform some action when progress is changed.
                        if (progress < 45) {
                            seekBar.progressTintList =
                                ColorStateList.valueOf(Color.rgb(138, 255, 126))
                        } else if (progress < 90) {
                            seekBar.progressTintList =
                                ColorStateList.valueOf(Color.rgb(255, 193, 79))
                        } else if (progress < 100) {
                            seekBar.progressTintList =
                                ColorStateList.valueOf(Color.rgb(239, 35, 60))
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        // Write code to perform some action when touch is started.
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        // Write code to perform some action when touch is stopped.
                    }
                })
                dialog.show()
            }
        }
    }

    override fun addPicto(pictograma: Pictograma) {
        viewModel._pictoBusquedaAdded.value = pictograma
    }

    override fun removePicto(pictograma: Pictograma, sourceAPI: Boolean, isBusqueda: Boolean) {
        viewModel.isBusqueda = isBusqueda
        viewModel.sourceAPI = sourceAPI
        viewModel._removePicto.value = pictograma
    }
}