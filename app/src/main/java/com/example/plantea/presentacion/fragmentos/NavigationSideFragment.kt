package com.example.plantea.presentacion.fragmentos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.google.android.material.navigation.NavigationView

class NavigationSideFragment: Fragment() {
    lateinit var vista: View
    private var navigationHandler = NavegacionUtils()

    override fun onStop() {
        super.onStop()
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        val hostingActivityClass = activity?.javaClass
        if (hostingActivityClass != null) {
            val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
            val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
            navigationHandler.inicializarVariables(vista, this, hostingActivityClass, navigationHandler.hostingId(hostingActivityClass), infoUsuario)
        }
        navigationHandler.restoreNavigationItemClicked(navigationHandler.hostingId(activity?.javaClass!!))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_navigation_side, container, false)
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
        val rol : LinearLayout = vista.findViewById(R.id.accountButton)

        val navigationView = vista.findViewById<NavigationView>(R.id.navigationView)
        navigationView.inflateMenu(R.menu.navigation_rail_menu)

        rol.setOnClickListener {
            navigationHandler.menuUsuario(this, it)
        }

        return vista
    }

}