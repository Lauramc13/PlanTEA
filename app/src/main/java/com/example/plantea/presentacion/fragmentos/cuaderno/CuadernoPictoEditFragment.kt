package com.example.plantea.presentacion.fragmentos.cuaderno

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CuadernoInterface
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.google.android.material.imageview.ShapeableImageView

class CuadernoPictoEditFragment : Fragment(), AdaptadorPictogramasCuaderno.OnItemSelectedListener {
    lateinit var vista: View
    lateinit var actividad: Activity
    lateinit var listaPictogramas: ArrayList<Pictograma>
    private lateinit var interfaceCuaderno: CuadernoInterface
    private lateinit var lst_Pictogramas: RecyclerView
    private lateinit var imageCerrar: ImageView
    private lateinit var imageAtras: ImageView
    private lateinit var seekbar: SeekBar
    private lateinit var adaptador: AdaptadorPictogramasCuaderno
    lateinit var termometro: LinearLayout
    lateinit var searchbar: SearchView
    private var isTermometro: Boolean = true


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val gridValue = context?.let { CommonUtils.cambioOrientacion(it) }
        lst_Pictogramas.layoutManager = gridValue?.let { GridLayoutManager(context, it) }
    }

    fun mostrarPictogramasBusqueda(newPictogramasList: ArrayList<Pictograma>?){
        adaptador.isBusqueda = true
        adaptador.updateData(newPictogramasList)
        imageCerrar.visibility = View.INVISIBLE
        imageAtras.visibility = View.VISIBLE
    }

    fun updateData(newPictogramasList: ArrayList<Pictograma>?){
        adaptador.isBusqueda = false
        adaptador.updateData(newPictogramasList)
        imageCerrar.visibility = View.VISIBLE
        imageAtras.visibility = View.INVISIBLE
    }

    fun updatePictoBusqueda(newPictogramasList: ArrayList<Pictograma>?){
        adaptador.updateDataBusqueda(newPictogramasList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas_edit, container, false)
        val bundle = this.arguments
        listaPictogramas = (bundle!!["key"] as ArrayList<Pictograma>?)!!
        isTermometro = (bundle["termometro"] as Boolean)
        searchbar = vista.findViewById(R.id.searchViewPicto)
        imageCerrar = vista.findViewById(R.id.icono_cuaderno_fragment)
        imageAtras = vista.findViewById(R.id.icono_cuaderno_fragment_atras)
        lst_Pictogramas = vista.findViewById(R.id.lst_cuaderno_pictogramas)

        val gridValue = context?.let { CommonUtils.cambioOrientacion(it) }
        lst_Pictogramas.layoutManager = gridValue?.let { GridLayoutManager(context, it) }
        adaptador = AdaptadorPictogramasCuaderno(listaPictogramas, this)
        lst_Pictogramas.adapter = adaptador

        imageCerrar.setOnClickListener { interfaceCuaderno.cerrarFragment() }
        imageAtras.setOnClickListener {
            interfaceCuaderno.atrasFragment(listaPictogramas)
        }
        busqueda()

        context?.let { CommonUtils.initializeTextToSpeech(it) }
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            interfaceCuaderno = (actividad as CuadernoInterface?)!!
        }
    }

    override fun pictogramaCuaderno(posicion: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogo_termometro)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        val iconoEscuchar = dialog.findViewById<ImageView>(R.id.icono_escuchar)
        seekbar = dialog.findViewById(R.id.seekBar_termometro)
        termometro = dialog.findViewById(R.id.termometro)
        pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
        tituloPictograma.text = listaPictogramas[posicion].titulo
        if(!isTermometro){
            termometro.visibility = View.GONE
        }

        iconoEscuchar.setOnClickListener {
            CommonUtils.textToSpeechWord(listaPictogramas[posicion].titulo)
        }

        //Botón cerrar
        imageCerrar = dialog.findViewById(R.id.icono_CerrarDialogoEvento)
        imageCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()

        //Funcionalidad termómetro: cambio de color según el progreso
        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress < 45) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(138, 255, 126))
                } else if (progress < 90) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(255, 193, 79))
                } else if (progress < 100) {
                    seekBar.progressTintList = ColorStateList.valueOf(Color.rgb(239, 35, 60))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // No hacemos nada con esto
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // No hacemos nada con esto
            }
        })
        dialog.show()
    }

    override fun addPicto(pictograma: Pictograma) {
        interfaceCuaderno.addPictoFromBusqueda(pictograma)
    }

    private fun busqueda(){
        searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                interfaceCuaderno.mostrarPictogramasBusqueda(query.trim())
                context?.let { CommonUtils.hideKeyboard(it, searchbar) }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                newText.trim()
                return true
            }
        })
    }


}