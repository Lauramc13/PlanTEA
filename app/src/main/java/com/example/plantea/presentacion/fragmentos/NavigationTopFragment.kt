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
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PasswordActivity
import com.example.plantea.presentacion.actividades.PoliticaActivity

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
            ManualActivity::class.java -> "Manual de usuario"
            PoliticaActivity::class.java -> "Política de privacidad"
            PasswordActivity::class.java -> "Cambiar contraseña"
            else -> "PlanTEA"
        }

        if(hostingActivityClass == CreditsActivity::class.java || hostingActivityClass == ConfiguracionActivity::class.java){
            rol.visibility = View.GONE
        }

        backButton.setOnClickListener {
            if(hostingActivityClass == ConfiguracionActivity::class.java){
                val editor = prefs.edit()
                editor.putString("imagenPlanificadorConfig", null)
                editor.putString("imageUsuarioTEAConfig", null)
                editor.putString("imageObjetoConfig", null)
                editor.apply()
            }
            activity?.finish()
        }

        iconoRol.setOnClickListener {
            navigationHandler.menuUsuario(this, vista)
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

}