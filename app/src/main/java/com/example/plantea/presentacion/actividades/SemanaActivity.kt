package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.DiaSemana
import com.example.plantea.presentacion.adaptadores.AdaptadorTablaSemana
import com.example.plantea.presentacion.adaptadores.AdaptadorTablaSemanaHeader
import com.example.plantea.presentacion.viewModels.SemanaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SemanaActivity: AppCompatActivity() {

    private val viewModel by viewModels<SemanaViewModel>()
    private var adaptador : AdaptadorTablaSemana? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semana)

        val buttonGuardar = findViewById<Button>(R.id.btnGuardar)
        val buttonEditar = findViewById<Button>(R.id.btnEditar)
        val buttonCancelar = findViewById<Button>(R.id.btnCancelar)
        val buttonSwitch = findViewById<Button>(R.id.btnSwitch)
        val layoutOptions = findViewById<LinearLayout>(R.id.optionsEditar)

        AniadirPictoUtils.createPickMedia(viewModel, this)
        viewModel.configureUser(getSharedPreferences("Preferencias", MODE_PRIVATE))

        val semana = DiaSemana()
        val recyclerViewHeader = findViewById<RecyclerView>(R.id.recycler_view_text)

        if(savedInstanceState == null){
            viewModel.configuration = semana.obtenerconfig(viewModel.idUsuario, this)
        }
        val listaText = viewModel.configurarDaysWeek(viewModel.configuration)
        val adaptadorHeader = AdaptadorTablaSemanaHeader(listaText, viewModel.configuration)
        recyclerViewHeader.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 7)
        recyclerViewHeader.adapter = adaptadorHeader

        val recylerView = findViewById<RecyclerView>(R.id.recycler_view_images)
        viewModel.week = viewModel.obtenerConfigDias(this)
        adaptador = AdaptadorTablaSemana(viewModel.week, viewModel.isEdit, viewModel)
        recylerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 7)
        recylerView.adapter = adaptador

        if(getSharedPreferences("Preferencias", MODE_PRIVATE).getBoolean("PlanificadorLogged", false)){
            changeButtons(layoutOptions, buttonEditar, buttonSwitch)
        }else{
            buttonEditar.visibility = Button.GONE
        }

        buttons(buttonEditar, buttonCancelar, buttonGuardar, buttonSwitch, layoutOptions, adaptador!!, adaptadorHeader, semana)
        observers(adaptador!!)
    }

    private fun changeButtons(layoutOptions: LinearLayout, buttonEditar: Button, buttonSwitch: Button){
        if(viewModel.isEdit){
            layoutOptions.visibility = LinearLayout.VISIBLE
            buttonEditar.visibility = Button.GONE
            buttonSwitch.visibility = Button.VISIBLE
        }else{
            layoutOptions.visibility = LinearLayout.GONE
            buttonEditar.visibility = Button.VISIBLE
            buttonSwitch.visibility = Button.GONE
        }
    }

    fun observers(adapter: AdaptadorTablaSemana) {
        viewModel._imageSelected.observe(this) {
            for(i in 0..6){
                if(viewModel.week[i].dia == viewModel.daySelected){
                    adapter.changeImage(i, it)
                }
            }
        }

        viewModel._itemBorrado.observe(this) {
            adapter.changeImage(it, null)
            viewModel.week[it].imagen = null
        }

        viewModel._itemColor.observe(this) {
            viewModel.week[it].color = viewModel.colorSelected
            adapter.changeColor(it, viewModel.colorSelected)
        }

        viewModel._diaClicked.observe(this) {
            // it to localdate
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val localDate = LocalDate.parse(it, formatter)

            val intent = Intent(this, EventosActivity::class.java)
            intent.putExtra("dia", localDate)
            intent.putExtra("isFromSemana", true)
            startActivity(intent)
        }
    }

    private fun buttons(buttonEditar: Button, buttonCancelar: Button, buttonGuardar: Button, buttonSwitch: Button, layoutOptions: LinearLayout, adaptador: AdaptadorTablaSemana, adaptadorHeader: AdaptadorTablaSemanaHeader, semana: DiaSemana){

        buttonEditar.setOnClickListener {
            viewModel.isEdit = true
            adaptador.isEdit = viewModel.isEdit
            adaptador.notifyDataSetChanged()
            changeButtons(layoutOptions, buttonEditar, buttonSwitch)
        }

        buttonCancelar.setOnClickListener {
            val listaSemana = viewModel.obtenerConfigDias(this)
            viewModel.configuration = semana.obtenerconfig(viewModel.idUsuario, this)
            adaptador.listaDiaSemana = listaSemana
            viewModel.week = listaSemana
            viewModel.isEdit = false
            adaptador.isEdit = viewModel.isEdit
            adaptador.notifyDataSetChanged() // probar a quitar una
            adaptador.notifyDataSetChanged()
            adaptador.currentPopupWindow?.dismiss()
            adaptadorHeader.updateList(viewModel.configurarDaysWeek(viewModel.configuration), viewModel.configuration)
            changeButtons(layoutOptions, buttonEditar, buttonSwitch)
        }

        buttonGuardar.setOnClickListener {
            for(i in 0..6){
                if(viewModel.week[i].imagen != null || viewModel.week[i].color != null) {
                    val imageBlob = CommonUtils.bitmapToByteArray(viewModel.week[i].imagen)
                    semana.guardarSemana(viewModel.idUsuario, imageBlob, viewModel.week[i].color, viewModel.week[i].dia, this)
                }
               if(viewModel.week[i].imagen == null){
                    semana.borrarImagen(viewModel.idUsuario, viewModel.week[i].dia!!, this)
               }

                if(viewModel.week[i].color == null){
                    semana.borrarColor(viewModel.idUsuario, viewModel.week[i].dia!!, this)
                }
            }

            semana.guardarConfiguracionWeek(viewModel.idUsuario, viewModel.configuration, this)

            viewModel.isEdit = false
            adaptador.isEdit = viewModel.isEdit
            adaptador.notifyDataSetChanged()
            changeButtons(layoutOptions, buttonEditar, buttonSwitch)
        }

        buttonSwitch.setOnClickListener {
            viewModel.configuration += 1
            if(viewModel.configuration == 4){
                viewModel.configuration = 1
            }
            adaptadorHeader.updateList(viewModel.configurarDaysWeek(viewModel.configuration), viewModel.configuration)
        }

    }
}