package com.example.plantea.presentacion.fragmentos

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.presentacion.actividades.ActividadActivity
import com.example.plantea.presentacion.actividades.EventosActivity
import com.example.plantea.presentacion.actividades.TraductorActivity
import com.example.plantea.presentacion.actividades.CalendarioActivity
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedByteArray
import com.example.plantea.presentacion.actividades.CreditsActivity
import com.example.plantea.presentacion.actividades.EventosPlanificadorActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PasswordActivity
import com.example.plantea.presentacion.actividades.PlanificacionesActivity
import com.example.plantea.presentacion.actividades.PoliticaActivity
import com.example.plantea.presentacion.actividades.SemanaActivity

class NavigationTopFragment: Fragment() {
    lateinit var vista: View
    private var navigationHandler = NavegacionUtils()
    lateinit var prefs: SharedPreferences
    private lateinit var iconoRol: ImageView
    private var infoUsuario = false

    override fun onResume() {
        super.onResume()
        prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val iconoTEA = prefs.getString("imagenUsuarioTEA", "")
        if (iconoTEA != "") {
            iconoRol.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenUsuarioTEA", "")!!.toPreservedByteArray))
        }else{
            iconoRol.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenPlanificador", "")!!.toPreservedByteArray))
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_navigation_top, container, false)
        val backButton: Button = vista.findViewById(R.id.goBackButton)
        iconoRol  = vista.findViewById(R.id.iconoRol)
        val rol: CardView = vista.findViewById(R.id.roundedImageView)
        val title: TextView = vista.findViewById(R.id.titleActivity)
        val hostingActivityClass = activity?.javaClass

        title.text = when (hostingActivityClass) {
            EventosActivity::class.java -> getString(R.string.pantalla_eventos)
            EventosPlanificadorActivity::class.java -> getString(R.string.pantalla_eventos)
            PlanificacionesActivity::class.java -> getString(R.string.pantalla_planificaciones)
            TraductorActivity::class.java -> getString(R.string.pantalla_traductor)
            CalendarioActivity::class.java -> getString(R.string.pantalla_calendario)
            ActividadActivity::class.java -> getString(R.string.actividades)
            // CuadernoActivity::class.java -> getString(R.string.pantalla_cuaderno)
            CreditsActivity::class.java -> getString(R.string.pantalla_acercade)
            ConfiguracionActivity::class.java -> getString(R.string.pantalla_configuracion)
            ManualActivity::class.java -> getString(R.string.pantalla_manual)
            PoliticaActivity::class.java -> getString(R.string.pantalla_politica)
            PasswordActivity::class.java -> getString(R.string.btn_CambiarPass)
            SemanaActivity::class.java -> getString(R.string.semana)
            else -> getString(R.string.app_name)
        }

        if(hostingActivityClass == CreditsActivity::class.java || hostingActivityClass == ConfiguracionActivity::class.java || hostingActivityClass == ManualActivity::class.java || hostingActivityClass == PoliticaActivity::class.java || hostingActivityClass == PasswordActivity::class.java){
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

        return vista
    }

}