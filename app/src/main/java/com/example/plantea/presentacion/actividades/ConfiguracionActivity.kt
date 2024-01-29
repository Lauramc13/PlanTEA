package com.example.plantea.presentacion.actividades


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.example.plantea.presentacion.viewModels.ConfiguracionViewModel
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

    private val viewModel by viewModels<ConfiguracionViewModel>()

    override fun onResume() {
        super.onResume()
        configurarDatos()
    }

    override fun onStop() {
        super.onStop()
        viewModel.email = txtCorreoPlanificador.editText?.text.toString()
        viewModel.name = txtPlanificador.editText?.text.toString()
        viewModel.username = txtUsernamePlanificador.editText?.text.toString()
        viewModel.nameTEA = txtUsuarioTEA.editText?.text.toString()
        viewModel.nameObj = txtObjeto.editText?.text.toString()
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
        val credits : TextView = findViewById(R.id.btn_credits)
        iconEditUsuarioTEA = findViewById(R.id.id_editIconUsuario)
        iconEditObjeto = findViewById(R.id.id_editIconObjeto)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        txtUsuarioTEA.isEnabled = false
        imgUsuarioTEA.isEnabled = false
        lblInfoUsuario.isChecked = false
        lblObjeto.isChecked = false

        if(savedInstanceState != null){
            txtCorreoPlanificador.editText?.setText(viewModel.email)
            txtPlanificador.editText?.setText(viewModel.name)
            txtUsernamePlanificador.editText?.setText(viewModel.username)
            txtUsuarioTEA.editText?.setText(viewModel.nameTEA)
            txtObjeto.editText?.setText(viewModel.nameObj)
        }else{
            txtPlanificador.editText?.setText(prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault()))
            txtUsernamePlanificador.editText?.setText(prefs.getString("nombreUsuarioPlanificador", "")!!.uppercase(Locale.getDefault()))
            txtCorreoPlanificador.editText?.setText(prefs.getString("email", "")!!.lowercase(Locale.getDefault()))

            txtUsuarioTEA.editText?.setText(prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault()))
            txtObjeto.editText?.setText(prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault()))
        }

        configurarDatos()

        credits.paintFlags = credits.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        credits.setOnClickListener{
            val intent = Intent(applicationContext, CreditsActivity::class.java)
            startActivity(intent)
        }

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
            imgClick(MenuAvataresPlanActivity::class.java)
        }

        imgUsuarioTEA.setOnClickListener {
            imgClick(MenuAvataresTEActivity::class.java)
        }

        imgObjeto.setOnClickListener {
            imgClick(MenuObjetosActivity::class.java)
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
            guardarConfiguracion(lblInfoUsuario, lblObjeto, btnNotificacion, semana, dia, hora)
        }

        observers()
    }

    fun observers(){
        viewModel._toast.observe(this, androidx.lifecycle.Observer {
            Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
        })
    }

    fun imgClick(activity: Class<*>){
        val editor = prefs.edit()
        editor.putBoolean("editPreferences", true)
        editor.apply()
        val intent = Intent(applicationContext, activity)
        startActivity(intent)
    }

    private fun configurarDatos(){
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

    fun guardarConfiguracion(lblInfoUsuario : SwitchCompat, lblObjeto : SwitchCompat, btnNotificacion : SwitchCompat, semana : CheckBox, dia : CheckBox, hora : CheckBox){
        //Obtener nombres de los usuarios y objeto
        val nombreUsuarioPlanificador = txtPlanificador.editText?.text.toString()
        var nombreUsuarioTEA = txtUsuarioTEA.editText?.text.toString()
        val username = txtUsernamePlanificador.editText?.text.toString()
        var nombreObjeto = txtObjeto.editText?.text.toString()

        //if drawable doesnt exists, set it to null
        val isValid = viewModel.comprobarCampos(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, imgUsuarioPlanificador.drawable, imgUsuarioTEA.drawable, imgObjeto.drawable, lblInfoUsuario.isChecked, lblObjeto.isChecked)

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
                val usuario = Usuario()
                usuario.guardarConfiguracion(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, rutaPlanificador, rutaUsuarioTEA, rutaObjeto, idUsuario, this)
            }catch (e: Exception){
                Toast.makeText(applicationContext, "Error al guardar la configuración", Toast.LENGTH_LONG).show()
            }
            finish()
        }
    }


}