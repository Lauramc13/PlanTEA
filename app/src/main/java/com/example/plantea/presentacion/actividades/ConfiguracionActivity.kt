package com.example.plantea.presentacion.actividades


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.planificador.CreditsActivity
import com.google.android.material.textfield.TextInputLayout

import java.util.*

class ConfiguracionActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var imgUsuarioPlanificador: ImageView
    private lateinit var imgUsuarioTEA: ImageView
    private lateinit var imgObjeto: ImageView
    private lateinit var txtPlanificador : TextInputLayout
    private lateinit var txtUsernamePlanificador : TextInputLayout
    private lateinit var txtCorreoPlanificador : TextInputLayout
    private lateinit var txtUsuarioTEA : TextInputLayout
    private lateinit var txtObjeto : TextInputLayout

    private lateinit var iconEditUsuarioTEA : ImageView
    private lateinit var iconEditObjeto : ImageView

    var usuario = Usuario()

    override fun onResume() {
        super.onResume()
        configurarDatos()
    }

    companion object {
        const val EMAIL_KEY = "EMAIL_KEY"
        const val NAME = "NAME"
        const val USERNAME = "USERNAME"
        const val NAMETEA = "NAMETEA"
        const val NAMEOBJ = "NAMEOBJ"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EMAIL_KEY, txtCorreoPlanificador.editText?.text.toString())
        outState.putString(NAME, txtPlanificador.editText?.text.toString())
        outState.putString(USERNAME, txtUsernamePlanificador.editText?.text.toString())
        outState.putString(NAMETEA, txtUsuarioTEA.editText?.text.toString())
        outState.putString(NAMEOBJ, txtObjeto.editText?.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        txtCorreoPlanificador.editText?.setText(savedInstanceState.getString(EMAIL_KEY).toString())
        txtPlanificador.editText?.setText(savedInstanceState.getString(NAME).toString())
        txtUsernamePlanificador.editText?.setText(savedInstanceState.getString(USERNAME).toString())
        txtUsuarioTEA.editText?.setText(savedInstanceState.getString(NAMETEA).toString())
        txtObjeto.editText?.setText(savedInstanceState.getString(NAMEOBJ).toString())
    }

    private fun configurarDatos(){
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        if (prefs.getString("imagenPlanificador", "") == "") {
            imgUsuarioPlanificador.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
        } else {
            imgUsuarioPlanificador.background = null
            imgUsuarioPlanificador.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
        }
        if (prefs.getString("imagenUsuarioTEA", "") == "") {
            imgUsuarioTEA.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            iconEditUsuarioTEA.visibility = View.GONE
        } else {
            imgUsuarioTEA.background = null
            imgUsuarioTEA.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
            iconEditUsuarioTEA.visibility = View.VISIBLE
        }
        if (prefs.getString("imagenObjeto", "") == "") {
            imgObjeto.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            iconEditObjeto.visibility = View.GONE
        } else {
            imgObjeto.background = null
            imgObjeto.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
            iconEditObjeto.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        imgUsuarioPlanificador = findViewById(R.id.img_FotoPlanificador)
        imgUsuarioTEA = findViewById(R.id.img_FotoUsuarioTEA)
        imgObjeto = findViewById(R.id.img_objeto)

        txtPlanificador = findViewById(R.id.txt_nombrePlanificador)
        txtUsernamePlanificador = findViewById(R.id.txt_nombreUsuarioPlanificador)
        txtCorreoPlanificador = findViewById(R.id.txt_correoPlanificador)
        txtUsuarioTEA  = findViewById(R.id.txt_nombreUsuarioTEA)
        txtObjeto = findViewById(R.id.txt_nombreObjeto)

        val btnGuardar : Button = findViewById(R.id.btn_guardarConfiguracion)
        //val btnPassword : Button= findViewById(R.id.buttonContrasenia)
        val btnNotificacion : SwitchCompat = findViewById(R.id.switch_notificacion)
        val lblInfoUsuario : SwitchCompat = findViewById(R.id.lbl_infoUsuarioTEA)
        val lblObjeto : SwitchCompat = findViewById(R.id.lbl_objeto)
        val semana : CheckBox = findViewById(R.id.checkBox_semana)
        val dia : CheckBox = findViewById(R.id.checkBox_dia)
        val hora : CheckBox = findViewById(R.id.checkBox_hora)
        val backButton : Button = findViewById(R.id.goBackButton)
        val credits : TextView = findViewById(R.id.btn_credits)

        credits.paintFlags = credits.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        credits.setOnClickListener{
            val intent = Intent(applicationContext, CreditsActivity::class.java)
            startActivity(intent)
        }


        txtUsuarioTEA.isEnabled = false
        imgUsuarioTEA.isEnabled = false
        lblInfoUsuario.isChecked = false
        lblObjeto.isChecked = false

        iconEditUsuarioTEA = findViewById(R.id.id_editIconUsuario)
        iconEditObjeto = findViewById(R.id.id_editIconObjeto)

        //Preferencias
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Recuperamos la información cuando no es la primera vez de acceso.
        //val userAccount = prefs.getBoolean("userAccount", false)

        //Imagenes y nombres
        txtPlanificador.editText?.setText(prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault()))
        txtUsernamePlanificador.editText?.setText(prefs.getString("nombreUsuarioPlanificador", "")!!.uppercase(Locale.getDefault()))
        txtCorreoPlanificador.editText?.setText(prefs.getString("email", "")!!.lowercase(Locale.getDefault()))

        txtUsuarioTEA.editText?.setText(prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault()))
        txtObjeto.editText?.setText(prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault()))
        imgObjeto.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
        configurarDatos()

        //Notificaciones
        val notificacionActiva = prefs.getBoolean("notificaciones", false)
        semana.isChecked = prefs.getBoolean("notificacion_semana", false)
        dia.isChecked = prefs.getBoolean("notificacion_dia", false)
        hora.isChecked = prefs.getBoolean("notificacion_hora", false)

        btnNotificacion.isChecked = notificacionActiva
        semana.isEnabled = notificacionActiva
        dia.isEnabled = notificacionActiva
        hora.isEnabled = notificacionActiva

        val infoUsuario = prefs.getBoolean("info_usuario", false)
        lblInfoUsuario.isChecked = infoUsuario
        txtUsuarioTEA.isEnabled = infoUsuario
        imgUsuarioTEA.isEnabled = infoUsuario

        val infoObjeto = prefs.getBoolean("info_objeto", false)
        lblObjeto.isChecked = infoObjeto
        txtObjeto.isEnabled = infoObjeto
        imgObjeto.isEnabled = infoObjeto

        imgUsuarioPlanificador.setOnClickListener {
            val editor = prefs.edit()
            editor.putBoolean("editPreferences", true)
            editor.apply()
            val password = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
            startActivity(password)
        }

        imgUsuarioTEA.setOnClickListener {
            val editor = prefs.edit()
            editor.putBoolean("editPreferences", true)
            editor.apply()
            val password = Intent(applicationContext, MenuAvataresTEActivity::class.java)
            startActivity(password)
        }

        imgObjeto.setOnClickListener {
            val editor = prefs.edit()
            editor.putBoolean("editPreferences", true)
            editor.apply()
            val password = Intent(applicationContext, MenuObjetosActivity::class.java)
            startActivity(password)
        }

        btnNotificacion.setOnCheckedChangeListener { _, isChecked ->
            semana.isChecked = isChecked
            semana.isEnabled = isChecked
            dia.isEnabled = isChecked
            hora.isEnabled = isChecked
        }

        lblInfoUsuario.setOnCheckedChangeListener { _, isChecked ->
            txtUsuarioTEA.isEnabled = isChecked
            imgUsuarioTEA.isEnabled = isChecked
        }

        lblObjeto.setOnCheckedChangeListener { _, isChecked ->
            txtObjeto.isEnabled = isChecked
            imgObjeto.isEnabled = isChecked
        }

        btnGuardar.setOnClickListener {
            //Obtener nombres de los usuarios y objeto
            val nombreUsuarioPlanificador = txtPlanificador.editText?.text.toString()
            var nombreUsuarioTEA = txtUsuarioTEA.editText?.text.toString()
            val username = txtUsernamePlanificador.editText?.text.toString()
            var nombreObjeto = txtObjeto.editText?.text.toString()

            //if drawable doesnt exists, set it to null
            val isValid = comprobarCampos(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, imgUsuarioPlanificador.drawable, imgUsuarioTEA.drawable, imgObjeto.drawable, lblInfoUsuario.isChecked, lblObjeto.isChecked)

            if(isValid){
                val rutaPlanificador = CommonUtils.crearRuta(this, imgUsuarioPlanificador, "Planificador")
                var rutaUsuarioTEA= ""
                var rutaObjeto = ""
                if (lblInfoUsuario.isChecked) {
                    rutaUsuarioTEA = CommonUtils.crearRuta(this, imgUsuarioTEA, "Usuario")
                }
                if (lblObjeto.isChecked) {
                    rutaObjeto = CommonUtils.crearRuta(this, imgObjeto, "Objeto")
                }


                //Cambiamos el valor en preferencias para no acceder a configuracion en el siguiente inicio y guardamos datos de los usuarios
                val editor = prefs.edit()
                editor.putBoolean("userAccount", true)
                editor.putBoolean("iniciadaSesion", true)
                editor.putBoolean("notificaciones", btnNotificacion.isChecked)
                editor.putBoolean("notificacion_semana", semana.isChecked)
                editor.putBoolean("notificacion_dia", dia.isChecked)
                editor.putBoolean("notificacion_hora", hora.isChecked)
                editor.putString("nombrePlanificador", nombreUsuarioPlanificador)
                editor.putString("nombreUsuarioTEA", nombreUsuarioTEA)
                editor.putString("imagenPlanificador", rutaPlanificador)
                editor.putString("imagenUsuarioTEA", rutaUsuarioTEA)
                editor.putString("imagenObjeto", rutaObjeto)
                editor.putBoolean("info_objeto", lblObjeto.isChecked)
                editor.putBoolean("info_usuario", lblInfoUsuario.isChecked)
                if(lblObjeto.isChecked){
                    editor.putString("nombreObjeto", nombreObjeto)
                }else{
                    editor.putString("nombreObjeto", "")
                    nombreObjeto = ""
                }
                if(lblInfoUsuario.isChecked){
                    editor.putString("nombreUsuarioTEA", nombreUsuarioTEA)
                }else{
                    editor.putString("nombreUsuarioTEA", "")
                    nombreUsuarioTEA = ""
                }
                editor.apply()

                val idUsuario = prefs.getString("idUsuario", "")
                try {
                    usuario.guardarConfiguracion(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, rutaPlanificador, rutaUsuarioTEA, rutaObjeto, idUsuario, this)
                }catch (e: Exception){
                    Toast.makeText(applicationContext, "Error al guardar la configuración", Toast.LENGTH_LONG).show()
                    Log.d("pruebas", e.toString())
                }
                finish()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }


    fun comprobarCampos(txtPlanificadorText: String, txtUsernameText: String, txtUsuarioTEAText: String, txtObjetoText: String, imgPlanificador: Drawable?, imgUserTEA: Drawable?, imageObjeto: Drawable?, infoUserTEA: Boolean, infoObjeto: Boolean): Boolean {
        if (txtPlanificadorText.isEmpty() || txtUsernameText.isEmpty() || txtUsuarioTEAText.isEmpty() && infoUserTEA) {
           runOnUiThread { Toast.makeText(applicationContext, "Se necesita un nombre para cada usuario", Toast.LENGTH_LONG).show() }
            return false
        }

        if (imgPlanificador == null || (imgUserTEA == null && infoUserTEA)) {
            runOnUiThread { Toast.makeText(applicationContext, "Se necesita una imagen para cada usuario", Toast.LENGTH_LONG).show()}
            return false
        }

        if ((txtObjetoText.isEmpty() || imageObjeto == null) && infoObjeto) {
            runOnUiThread { Toast.makeText(applicationContext, "Se necesita una imagen y nombre del objeto tranquilizador", Toast.LENGTH_LONG).show()}
            return false
        }
        return true
    }


}