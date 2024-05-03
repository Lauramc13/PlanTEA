package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.os.LocaleListCompat
import com.example.plantea.R
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var imagePlanificador: ImageView
    private lateinit var imageUsuarioTEA: ImageView
    private lateinit var nombrePlanificador: TextView
    private lateinit var nombreUsuarioTEA: TextView
    private lateinit var cardUsuarioTEA: CardView
    private lateinit var cardUsuarioPlanificador: CardView
    private lateinit var iconoCerrar: ImageView
    private lateinit var spinner : Spinner
    private lateinit var imageSpinner : ImageView

    private var infoUsuario = false
    private var navigationHandler = NavegacionUtils()
    private lateinit var dialogLogout : Dialog
    private lateinit var prefs: SharedPreferences
    override fun onResume() {
        super.onResume()
        configurarDatos()
    }

    private fun configurarDatos(){
        dialogLogout = Dialog(this)
        infoUsuario = prefs.getBoolean("info_usuario", false)
        if (!infoUsuario) {
            cardUsuarioTEA.visibility = View.GONE
        }else{
            cardUsuarioTEA.visibility = View.VISIBLE
        }
        nombrePlanificador.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
        nombreUsuarioTEA.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
        imageUsuarioTEA.setImageDrawable(null)
        imageUsuarioTEA.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
        imagePlanificador.setImageDrawable(null)
        imagePlanificador.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

       val accesoConfiguracion = prefs.getBoolean("userAccount", false)
        if (!accesoConfiguracion) {
            val intent = Intent(this@MainActivity, PreLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        imagePlanificador = findViewById(R.id.image_RolPlanificador)
        imageUsuarioTEA = findViewById(R.id.image_RolTEA)
        nombrePlanificador = findViewById(R.id.lbl_nombrePlanificador)
        nombreUsuarioTEA = findViewById(R.id.lbl_nombreUsuarioTEA)
        cardUsuarioPlanificador = findViewById(R.id.cardViewPlanificador)
        cardUsuarioTEA = findViewById(R.id.cardViewUsuarioTEA)
        spinner = findViewById(R.id.spinner_idiomas)
        imageSpinner = findViewById(R.id.image_idioma)

        val preferencias: Button = findViewById(R.id.image_RolPlanificador2)
        val buttonLogout: Button = findViewById(R.id.btn_logout)
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Preferencias
        configurarDatos()

        //Este método se ejecutará al pinchar sobre la imagen del rol planificador
        cardUsuarioPlanificador.setOnClickListener {
            if (!infoUsuario) {
                val editor = prefs.edit()
                editor.putBoolean("PlanificadorLogged", true)
                editor.apply()
                val intent = Intent(applicationContext, PlanActivity::class.java)
                startActivity(intent)
            }else{
                 navigationHandler.crearDialogoLogin(this, this)
            }
        }

        //Este método se ejecutará al pinchar sobre la imagen del rol niño
        cardUsuarioTEA.setOnClickListener {
            val editor = prefs.edit()
            editor.putBoolean("PlanificadorLogged", false)
            editor.apply()

            val intent = Intent(applicationContext, PlanActivity::class.java)
            startActivity(intent)
        }

        preferencias.setOnClickListener{
            startActivity(Intent(applicationContext, ConfiguracionActivity::class.java))
        }

        buttonLogout.setOnClickListener{
            dialogLogout()
        }

        val idiomas = ArrayList<String>()
        idiomas.add("Español")
        idiomas.add("English")
        val adapter = ArrayAdapter(applicationContext, R.layout.simple_spinner_item_idioma, idiomas)
        spinner.adapter = adapter

        val currentLanguage = Locale.getDefault().displayLanguage
        val position = adapter.getPosition(currentLanguage)
        spinner.setSelection(position)
        imageSpinner(currentLanguage)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(idiomas[position] == "English"){
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag("en")))
                }else{
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag("es")))
                }
                imageSpinner(idiomas[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }
        }
    }

    private fun imageSpinner(currntLanguage: String) {
        if (currntLanguage == "English") {
            imageSpinner.setImageResource(R.drawable.ic_en)
        } else {
            imageSpinner.setImageResource(R.drawable.ic_es)
        }
    }

    private fun dialogLogout(){
        dialogLogout.setContentView(R.layout.dialogo_logout)
        dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnLogout: Button = dialogLogout.findViewById(R.id.btn_logout)
        iconoCerrar = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
        btnLogout.setOnClickListener {
            val secretKey = prefs.getString("secret_key", "")
            val iv = prefs.getString("initialization_vector", "")
            prefs.edit().clear().apply()
            prefs.edit()
                .putString("secret_key", secretKey)
                .putString("initialization_vector", iv)
                .apply()
            val intent = Intent(this, PreLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finishAffinity()

        }
        iconoCerrar.setOnClickListener { dialogLogout.dismiss() }
        dialogLogout.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogLogout.isShowing) {
            dialogLogout.dismiss()
        }
    }
}