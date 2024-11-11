package com.example.plantea.presentacion.viewModels

import android.content.Context
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.Evento
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment

class EventosPlanificadorViewModel: ViewModel() {

    lateinit var idUsuario : String
    val evento = Evento()
    var posicionEvento = 0
    var fragment = NuevoEventoFragment()

    lateinit var eventos : ArrayList<Evento>

    fun backCallBack(context: Context): OnBackPressedCallback {
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                if ((context as AppCompatActivity).supportFragmentManager.backStackEntryCount == 0) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    (context).supportFragmentManager.popBackStack()
                }
                context.finish()
            }
        }
        return callback
    }

    fun configureUser(prefs : android.content.SharedPreferences){
        val userId = if(prefs.getString("idUsuarioTEA", "") == null || prefs.getString("idUsuarioTEA", "") == ""){
            prefs.getString("idUsuario", "")
        } else{
            prefs.getString("idUsuarioTEA", "")
        }
        idUsuario = userId.toString()
    }

}