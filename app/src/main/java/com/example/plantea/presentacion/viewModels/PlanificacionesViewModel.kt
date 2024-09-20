package com.example.plantea.presentacion.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes

class PlanificacionesViewModel : ViewModel(){

    val plan = Planificacion()
    lateinit var idUsuario : String
    lateinit var planes : ArrayList<Planificacion>
    var counter: Int = 1
    var adaptador: AdaptadorListaPlanes? = null
    var posicionPlan = 0

    fun configureUser(prefs : android.content.SharedPreferences, context: Context){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
    }
}
