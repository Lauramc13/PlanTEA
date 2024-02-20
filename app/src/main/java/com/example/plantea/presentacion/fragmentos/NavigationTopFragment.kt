package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.presentacion.actividades.ActividadActivity
import com.example.plantea.presentacion.actividades.CuadernoActivity
import com.example.plantea.presentacion.actividades.PlanActivity
import com.example.plantea.presentacion.actividades.TraductorActivity
import com.example.plantea.presentacion.actividades.CalendarioActivity
import com.example.plantea.presentacion.actividades.CreditsActivity

class NavigationTopFragment: Fragment() {
    lateinit var vista: View
    private var navigationHandler = NavegacionUtils()
    lateinit var prefs: SharedPreferences
    lateinit var customView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_navigation_top, container, false)
        val backButton: Button = vista.findViewById(R.id.goBackButton)
        val iconoRol : ImageView = vista.findViewById(R.id.iconoRol)
        val rol: CardView = vista.findViewById(R.id.roundedImageView)
        val title: TextView = vista.findViewById(R.id.titleActivity)
        val hostingActivityClass = activity?.javaClass

        title.text = when (hostingActivityClass) {
            PlanActivity::class.java -> "Eventos"
            TraductorActivity::class.java -> "Traductor"
            CalendarioActivity::class.java -> "Calendario"
            ActividadActivity::class.java -> "Actividades"
            CuadernoActivity::class.java -> "Cuaderno"
            CreditsActivity::class.java -> "Acerca de la aplicación"
            ConfiguracionActivity::class.java -> "Configuración"
            else -> "PlanTEA"
        }

        if(hostingActivityClass == CreditsActivity::class.java || hostingActivityClass == ConfiguracionActivity::class.java){
            rol.visibility = View.GONE
        }

        backButton.setOnClickListener {
          /*  if (parentFragmentManager.backStackEntryCount == 0) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
            } else {
                parentFragmentManager.popBackStack()
            }*/
            activity?.finish()
        }

        iconoRol.setOnClickListener {
            menuUsuario(it)
        }

        prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
        if (infoUsuario) {
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
        } else {
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
        }

    return vista
    }

    private fun menuUsuario(anchorView: View) {
        val inflater = LayoutInflater.from(requireContext())
        val isUsuarioTEA = prefs.getBoolean("info_usuario", false)
        val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
        customView = inflater.inflate(R.layout.popup_menu_usuario, null)
        val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(anchorView, Gravity.END  or Gravity.TOP, 20, 120)

        //Si estamos en el usuarioTEA
        if(!infoUsuario) {
            customView.findViewById<LinearLayout>(R.id.item_user).visibility = View.GONE
            customView.findViewById<LinearLayout>(R.id.item_cerarSesion).visibility = View.GONE
            customView.findViewById<View>(R.id.divider).visibility = View.GONE
            customView.findViewById<View>(R.id.divider2).visibility = View.GONE
        }

        //Si no existe usuario TEA
        if(!isUsuarioTEA && infoUsuario) {
            customView.findViewById<LinearLayout>(R.id.item_cuenta).visibility = View.GONE
            customView.findViewById<View>(R.id.divider).visibility = View.GONE
        }

        customView.findViewById<LinearLayout>(R.id.item_cuenta).setOnClickListener {
                if(isUsuarioTEA && infoUsuario){
                    val editor = prefs.edit()
                    editor.putBoolean("PlanificadorLogged", false)
                    editor.apply()
                    requireContext().startActivity(Intent(activity?.baseContext, PlanActivity::class.java))
                    activity?.finish()
                    activity?.finishAffinity()
                }else{
                    navigationHandler.crearDialogoLogin(this.requireContext(), this.requireActivity())
            }
        }

        customView.findViewById<LinearLayout>(R.id.item_cerarSesion).setOnClickListener {
            navigationHandler.cerrarSesion(this)
        }
        customView.findViewById<LinearLayout>(R.id.item_user).setOnClickListener {
            startActivity(Intent(requireContext().applicationContext, ConfiguracionActivity::class.java))
        }

        popupWindow.showAsDropDown(anchorView)
    }

}