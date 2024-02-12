package com.example.plantea.presentacion.fragmentos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationBottomFragment: Fragment() {
    lateinit var vista: View
    private var navigationHandler = NavegacionUtils()

    override fun onStop() {
        super.onStop()
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        navigationHandler.restoreNavigationItemClickedBottom(navigationHandler.hostingId(activity?.javaClass!!))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_navigation_bottom, container, false)
        val bottomNavigation = vista.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
        bottomNavigation.menu.clear()
        if (infoUsuario) {
            bottomNavigation.inflateMenu(R.menu.navigation_rail_bottom)
        } else {
            bottomNavigation.inflateMenu(R.menu.navigation_rail_bottom_tea)
        }

        val hostingActivityClass = activity?.javaClass

        if (hostingActivityClass != null) {
            navigationHandler.inicializarVariablesBottom(vista, this, hostingActivityClass, navigationHandler.hostingId(hostingActivityClass))
        }
        return vista
    }
}