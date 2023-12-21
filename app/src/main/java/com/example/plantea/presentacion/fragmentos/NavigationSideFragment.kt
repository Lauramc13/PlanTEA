package com.example.plantea.presentacion.fragmentos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.presentacion.actividades.ninio.ActividadActivity
import com.example.plantea.presentacion.actividades.ninio.CuadernoActivity
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.example.plantea.presentacion.actividades.ninio.TraductorActivity
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity
import com.google.android.material.navigation.NavigationView

class NavigationSideFragment: Fragment() {
    lateinit var vista: View
    private var navigationHandler = NavegacionUtils()

    override fun onStop() {
        super.onStop()
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_navigation_side, container, false)
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)

        val navigationView = vista.findViewById<NavigationView>(R.id.navigationView)
        if (infoUsuario) {
            navigationView.inflateMenu(R.menu.navigation_rail_menu)
        } else {
            navigationView.inflateMenu(R.menu.navigation_rail_menu_tea)
        }

        val hostingActivityClass = activity?.javaClass
        val idActivity = when (hostingActivityClass) {
            MainActivity::class.java -> R.id.home
            PlanActivity::class.java -> R.id.planificacion
            TraductorActivity::class.java -> R.id.traductor
            CalendarioActivity::class.java -> R.id.calendar
            ActividadActivity::class.java -> R.id.actividades
            CuadernoActivity::class.java -> R.id.cuaderno
            else -> R.id.planificacion
        }

        if (hostingActivityClass != null) {
            navigationHandler.inicializarVariables(vista, this, hostingActivityClass, idActivity)
        }

        return vista
    }
}