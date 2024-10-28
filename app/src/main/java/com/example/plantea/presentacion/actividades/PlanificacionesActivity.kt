package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasPlan
import com.example.plantea.presentacion.viewModels.PlanificacionesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PlanificacionesActivity : AppCompatActivity(), AdaptadorListaPlanes.OnItemSelectedListener{

    private val viewModel by viewModels<PlanificacionesViewModel>()
    private lateinit var adaptador: AdaptadorListaPlanes
    private lateinit var listaPlanificaciones: RecyclerView

    private lateinit var startForResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planificaciones)
        val atras = findViewById<Button>(R.id.atras)
        val nuevoPlan = findViewById<Button>(R.id.btn_planificar)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs, this)

        listaPlanificaciones = findViewById(R.id.recycler_planificaciones)

        atras?.setOnClickListener {
            finish()
        }

        nuevoPlan.setOnClickListener {
            val intent = Intent(this, CrearPlanActivity::class.java)
            startForResult.launch(intent)
        }

        iniciarListaPlanificaciones()

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val idPlan = intent?.getIntExtra("idPlan", 0)
                if (idPlan != null) {
                    if(!intent.getBooleanExtra("isNuevo", false)){
                        viewModel.posicionPlan = viewModel.planes.size
                    }else{
                        for (i in viewModel.planes.indices) {
                            if (viewModel.planes[i].getId() == idPlan) {
                                viewModel.planes[i].setTitulo(intent.getStringExtra("tituloPlan")!!)
                                viewModel.posicionPlan = i
                                break
                            }
                        }
                        adaptador.notifyItemChanged(viewModel.posicionPlan)

                    }
                }
            }
        }
    }

    private fun iniciarListaPlanificaciones() {
        listaPlanificaciones.layoutManager = LinearLayoutManager(this)
        viewModel.planes = viewModel.plan.mostrarPlanificacionesDisponibles(viewModel.idUsuario, this) as ArrayList<Planificacion>
        if(viewModel.planes.isEmpty()){
            findViewById<LinearLayout>(R.id.layout_no_planificaciones).visibility = View.VISIBLE
        }else{
            findViewById<LinearLayout>(R.id.layout_no_planificaciones).visibility = View.GONE
        }
        adaptador = AdaptadorListaPlanes(viewModel.planes, this)
        listaPlanificaciones.adapter = adaptador
    }

    override fun deleteClick(posicion: Int, context: Context) {
        val dialogPlan = Dialog(context)
        dialogPlan.setContentView(R.layout.dialogo_eliminar_planificacion)
        dialogPlan.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnEliminar = dialogPlan.findViewById<Button>(R.id.btn_eliminarPlan)
        val iconCerrar = dialogPlan.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val btnCancelar = dialogPlan.findViewById<Button>(R.id.btn_cancelarPlan)

        btnEliminar.setOnClickListener {
            Toast.makeText(context, R.string.toast_planificacion_eliminada, Toast.LENGTH_SHORT).show()
            viewModel.plan.eliminarPlanificacion(this, viewModel.planes[posicion].getId())
            adaptador.notifyItemRemoved(posicion)
            viewModel.planes.removeAt(posicion)
            if(viewModel.planes.isEmpty()){
                findViewById<LinearLayout>(R.id.layout_no_planificaciones).visibility = View.VISIBLE
            }
            dialogPlan.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialogPlan.dismiss()
        }

        iconCerrar.setOnClickListener { dialogPlan.dismiss() }
        dialogPlan.show()
    }

    override fun editClick(posicion: Int, context: Context) {
        val intent = Intent(this, CrearPlanActivity::class.java)
        intent.putExtra("idPlan", viewModel.planes[posicion].getId())
        intent.putExtra("tituloPlan", viewModel.planes[posicion].getTitulo())
        val pictogramas = viewModel.plan.obtenerPictogramasPlanificacion(this, viewModel.planes[posicion].getId(), Locale.getDefault().language, viewModel.idUsuario)
        intent.putExtra("pictogramas", pictogramas)
        pictogramas.forEachIndexed { index, pictogram ->
            intent.putExtra("imagen_$index", CommonUtils.bitmapToByteArray(pictogram.imagen))
        }
        startForResult.launch(intent)
    }

    override fun duplicateClick(posicion: Int, context: Context) {
         val pictogramas = viewModel.plan.obtenerPictogramasPlanificacion(this, viewModel.planes[posicion].getId(), Locale.getDefault().language, viewModel.idUsuario)
        val title =  searchLastTitle(viewModel.planes[posicion].getTitulo())
         val creada = viewModel.plan.crearPlanificacion(this, viewModel.idUsuario, title)
        viewModel.plan.addPictogramasPlan(creada, this, pictogramas)

        if (creada != 0) {
            Toast.makeText(this, R.string.toast_planificacion_duplicada, Toast.LENGTH_SHORT).show()
            val planificacion = viewModel.planes[posicion]
            val newPlanificacion = Planificacion(title, planificacion.getId())
            viewModel.planes.add(newPlanificacion)
            adaptador.notifyItemInserted(viewModel.planes.size)
            listaPlanificaciones.scrollToPosition(adaptador.itemCount?.minus(1) ?: 0)

        } else {
            Toast.makeText(this, R.string.toast_error_planificacion_duplicada, Toast.LENGTH_SHORT).show()

        }
    }

    override fun downloadPDFClick(posicion: Int, context: Context) {
        viewModel.dialogoTraduccion(this, viewModel.planes[posicion].getId())
    }

    private fun searchLastTitle(title: String): String{
        val listaTitulos = viewModel.planificacion.obtenerTitulosPlanificaciones(viewModel.idUsuario, this)

        var i = 1
        var newTitle = title
        while(listaTitulos.contains(newTitle)){
            newTitle = "$title ($i)"
            i++
        }
        return newTitle

    }

    override fun planSeleccionado(posicion: Int, recyclerPictogramas: RecyclerView, context: Context) {
        viewModel.posicionPlan = posicion
        val pictosPlaninificacion = viewModel.plan.obtenerPictogramasPlanificacion(this, viewModel.planes[viewModel.posicionPlan].getId(), Locale.getDefault().language, viewModel.idUsuario) as ArrayList<Pictograma>

        for (pictogram in pictosPlaninificacion) {
            if (pictogram.idAPI != 0)
                pictogram.imagen = BitmapFactory.decodeResource(resources, R.drawable.loading_placeholder)
        }

        val adaptadorPictogramas = AdaptadorPictogramasPlan(pictosPlaninificacion)
        recyclerPictogramas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerPictogramas.adapter = adaptadorPictogramas

        CoroutineScope(Dispatchers.Main).launch {
            pictosPlaninificacion.forEach { pictogram ->
                if (pictogram.idAPI != 0) {
                    pictogram.imagen = withContext(Dispatchers.IO) {
                        CommonUtils.getImagenAPI(pictogram.idAPI)
                    }
                }
                adaptadorPictogramas.notifyItemChanged(pictosPlaninificacion.indexOf(pictogram))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configurarPlanes() {
        iniciarListaPlanificaciones()
        viewModel.planes.clear()
        viewModel.planes.addAll( viewModel.plan.mostrarPlanificacionesDisponibles(viewModel.idUsuario, this) as ArrayList<Planificacion>)
        adaptador.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        configurarPlanes()
    }
}