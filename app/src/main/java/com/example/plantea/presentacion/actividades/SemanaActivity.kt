package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.DiaSemana
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorTablaSemana
import com.example.plantea.presentacion.adaptadores.AdaptadorTablaSemanaHeader
import com.example.plantea.presentacion.viewModels.SemanaViewModel
import java.util.Locale

class SemanaActivity: AppCompatActivity() {

    val viewModel by viewModels<SemanaViewModel>()
    private var adaptador : AdaptadorTablaSemana? = null
    private var adaptadorHeader : AdaptadorTablaSemanaHeader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semana)

        val buttonGuardar = findViewById<Button>(R.id.btnGuardar)
        val buttonEditar = findViewById<Button>(R.id.btnEditar)
        val buttonCancelar = findViewById<Button>(R.id.btnCancelar)
        val buttonSwitch = findViewById<Button>(R.id.btnSwitch)
        val layoutOptions = findViewById<LinearLayout>(R.id.optionsEditar)

        viewModel.createPickMedia(viewModel, this)
        viewModel.configureUser(getSharedPreferences("Preferencias", MODE_PRIVATE))

        val semana = DiaSemana()
        val recyclerViewHeader = findViewById<RecyclerView>(R.id.recycler_view_text)

        if(savedInstanceState == null){
            viewModel.configuration = semana.obtenerconfig(viewModel.idUsuario, this)
            viewModel.colorsHeader = semana.obtenerColoresHeader(viewModel.idUsuario,this)
        }
        val listaText = viewModel.configurarDaysWeek(viewModel.configuration)
        adaptadorHeader = AdaptadorTablaSemanaHeader(listaText, viewModel.configuration, viewModel.colorsHeader!!)
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
            buttonEditar.visibility = Button.INVISIBLE
        }

        buttons(buttonEditar, buttonCancelar, buttonGuardar, buttonSwitch, layoutOptions, adaptador!!, adaptadorHeader!!, semana)
        observers()
    }

    private fun changeButtons(layoutOptions: LinearLayout, buttonEditar: Button, buttonSwitch: Button){
        if(viewModel.isEdit){
            layoutOptions.visibility = LinearLayout.VISIBLE
            buttonEditar.visibility = Button.INVISIBLE
            buttonSwitch.visibility = Button.VISIBLE
        }else{
            layoutOptions.visibility = LinearLayout.GONE
            buttonEditar.visibility = Button.VISIBLE
            buttonSwitch.visibility = Button.GONE
        }
    }

//    private fun configurarFrame(spinner: Spinner, prefs: SharedPreferences){
//        //populate spinner
//        val usuario = Usuario()
//        val usuarios = usuario.obtenerUsuariosTEA(prefs.getString("idUsuario", ""), this)
//        val adapter = ArrayAdapter(applicationContext, R.layout.simple_spinner_item_idioma,  usuarios.map { it.name })
//        spinner.adapter = adapter
//    }

    fun observers() {
        viewModel.seimageSelected.observe(this) {
            for(i in 0..6){
                if(viewModel.week[i].dia == viewModel.daySelected){
                    adaptador!!.changeImage(i, it)
                }
            }
        }

        viewModel.mdItemBorrado.observe(this) {
            adaptador!!.changeImage(it, null)
            viewModel.week[it].imagen = null
        }

        viewModel.mdItemColor.observe(this) { posicion ->
            viewModel.week[posicion].color = viewModel.colorSelected
            adaptador!!.changeColor(posicion, viewModel.colorSelected)
            adaptadorHeader!!.changeColor(posicion, viewModel.colorsHeader!![posicion])
        }

        viewModel.mdDiaClicked.observe(this) {
            val idEvento = viewModel.week[it!!.toInt()].idEvento
            val evento = Evento()
            val eventoSelected = evento.obtenerInfoEvento(idEvento!!.toInt(), this)

            val plan = Planificacion()
            val pictogramas = plan.obtenerPictogramasPlanificacionEvento(this, eventoSelected.idPlan,eventoSelected.id, Locale.getDefault().language, viewModel.idUsuario)

            val intent = Intent(this, EventosActivity::class.java)
            intent.putExtra("titulo", eventoSelected.nombre)
            intent.putExtra("pictogramas", pictogramas)
            pictogramas.forEachIndexed { index, pictogram ->
                intent.putExtra("imagen_$index", CommonUtils.bitmapToByteArray(pictogram.imagen))
            }
            intent.putExtra("fecha", eventoSelected.fecha)

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
            adaptadorHeader.updateList(viewModel.configurarDaysWeek(viewModel.configuration), viewModel.configuration)
            changeButtons(layoutOptions, buttonEditar, buttonSwitch)
        }

        buttonGuardar.setOnClickListener {
            for(i in 0..6){
                if(viewModel.week[i].imagen != null || viewModel.week[i].color != null) {
                    val imageBlob = CommonUtils.bitmapToByteArray(viewModel.week[i].imagen)
                    semana.guardarSemana(viewModel.idUsuario, imageBlob, viewModel.week[i].color, viewModel.week[i].dia, viewModel.week[i].idEvento, this)
                }

               if(viewModel.week[i].imagen == null){
                    semana.borrarImagen(viewModel.idUsuario, viewModel.week[i].dia!!, this)
                    viewModel.week[i].idEvento = null
               }

                if(viewModel.week[i].color == null){
                    semana.borrarColor(viewModel.idUsuario, viewModel.week[i].dia!!, this)
                }
            }

            semana.guardarConfiguracionWeek(viewModel.idUsuario, viewModel.configuration, this)
            semana.guardarColorsHeader(viewModel.idUsuario, viewModel.colorsHeader!!, this)

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