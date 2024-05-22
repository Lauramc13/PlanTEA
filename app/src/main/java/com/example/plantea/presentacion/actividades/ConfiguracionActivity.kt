package com.example.plantea.presentacion.actividades


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
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

    private var restart = false

    private lateinit var iconEditUsuarioTEA : ImageView
    private lateinit var iconEditObjeto : ImageView

    private lateinit var lblInfoUsuario : SwitchCompat
    private lateinit var lblObjeto : SwitchCompat


    private val viewModel by viewModels<ConfiguracionViewModel>()

    override fun onResume() {
        super.onResume()
        configurarDatos()
        getImages()
        restart = false
    }

    override fun onRestart() {
        super.onRestart()
        restart = true
    }

    override fun onStop() {
        super.onStop()
        //viewModel.email = txtCorreoPlanificador.editText?.text.toString()
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
        val btnPassword : Button= findViewById(R.id.buttonContrasenia)
        lblInfoUsuario = findViewById(R.id.lbl_infoUsuarioTEA)
        lblObjeto = findViewById(R.id.lbl_objeto)
        val credits : TextView = findViewById(R.id.btn_credits)
        iconEditUsuarioTEA = findViewById(R.id.id_editIconUsuario)
        iconEditObjeto = findViewById(R.id.id_editIconObjeto)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        txtUsuarioTEA.isEnabled = false
        imgUsuarioTEA.isEnabled = false
        lblInfoUsuario.isChecked = false
        lblObjeto.isChecked = false

        if(savedInstanceState != null){
            txtPlanificador.editText?.setText(viewModel.name)
            txtUsernamePlanificador.editText?.setText(viewModel.username)
            txtUsuarioTEA.editText?.setText(viewModel.nameTEA)
            txtObjeto.editText?.setText(viewModel.nameObj)
        }else {
            txtPlanificador.editText?.setText(prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault()))
            txtUsernamePlanificador.editText?.setText(prefs.getString("nombreUsuarioPlanificador", "")!!.uppercase(Locale.getDefault()))
            txtUsuarioTEA.editText?.setText(prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault()))
            txtObjeto.editText?.setText(prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault()))
        }

        txtCorreoPlanificador.editText?.setText(prefs.getString("email", "")!!.lowercase(Locale.getDefault()))

        credits.paintFlags = credits.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        credits.setOnClickListener{
            val intent = Intent(applicationContext, CreditsActivity::class.java)
            startActivity(intent)
        }

        btnPassword.setOnClickListener {
            val intent = Intent(applicationContext, PasswordActivity::class.java)
            startActivity(intent)
        }

        val infoUsuario = prefs.getBoolean("info_usuario", false)
        lblInfoUsuario.isChecked = infoUsuario
        txtUsuarioTEA.isEnabled = infoUsuario
        imgUsuarioTEA.isEnabled = infoUsuario

        val infoObjeto = prefs.getBoolean("info_objeto", false)
        lblObjeto.isChecked = infoObjeto
        txtObjeto.isEnabled = infoObjeto
        imgObjeto.isEnabled = infoObjeto

        imgUsuarioPlanificador.setOnClickListener {
            val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
            intent.putExtra("editPreferences", true)
            startActivity(intent)
        }

        imgUsuarioTEA.setOnClickListener {
            val intent = Intent(applicationContext, MenuAvataresTEActivity::class.java)
            intent.putExtra("editPreferences", true)
            startActivity(intent)
        }

        imgObjeto.setOnClickListener {
            val intent = Intent(applicationContext, MenuObjetosActivity::class.java)
            intent.putExtra("editPreferences", true)
            startActivity(intent)
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
           guardarConfiguracion(lblInfoUsuario, lblObjeto)
        }

        observers()
    }

    fun observers(){
        viewModel._toast.observe(this) {
            Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarDatos(){
        if (prefs.getString("imagenPlanificador", "") == "") {
            imgUsuarioPlanificador.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
        } else {
            imgUsuarioPlanificador.background = null
            //Glide.with(this).load("file://" + prefs.getString("imagenPlanificador", "")).into(imgUsuarioPlanificador)
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

    private fun guardarConfiguracion(lblInfoUsuario : SwitchCompat, lblObjeto : SwitchCompat){
        //Obtain values from the fields
        val nombreUsuarioPlanificador = txtPlanificador.editText?.text.toString()
        var nombreUsuarioTEA = txtUsuarioTEA.editText?.text.toString()
        val username = txtUsernamePlanificador.editText?.text.toString()
        var nombreObjeto = txtObjeto.editText?.text.toString()

        //if drawable doesnt exists, set it to null
        val isValid = viewModel.comprobarCampos(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, imgUsuarioPlanificador.drawable, imgUsuarioTEA.drawable, imgObjeto.drawable, lblInfoUsuario.isChecked, lblObjeto.isChecked)

        if(isValid){
            //val rutaPlanificador = CommonUtils.crearRuta(this, (imgUsuarioPlanificador.drawable as BitmapDrawable).bitmap, )
            val rutaPlanificador = CommonUtils.guardarImagen(this, "Planificador", (imgUsuarioPlanificador.drawable as BitmapDrawable).bitmap)
            var rutaUsuarioTEA= ""
            var rutaObjeto = ""

            //Cambiamos el valor en preferencias para no acceder a configuracion en el siguiente inicio y guardamos datos de los usuarios
            val editor = prefs.edit()
            //editor.putBoolean("userAccount", true)
            editor.putString("nombrePlanificador", nombreUsuarioPlanificador)
            editor.putString("nombreUsuarioTEA", nombreUsuarioTEA)
            editor.putString("imagenPlanificador", rutaPlanificador)
            editor.putBoolean("info_objeto", lblObjeto.isChecked)
            editor.putBoolean("info_usuario", lblInfoUsuario.isChecked)

            editor.putString("imagenPlanificadorConfig", null)
            editor.putString("imageUsuarioTEAConfig", null)
            editor.putString("imageObjetoConfig", null)

            if(lblObjeto.isChecked){
                //rutaObjeto = CommonUtils.crearRuta(this, (imgObjeto.drawable as BitmapDrawable).bitmap, "Objeto")
                rutaObjeto = CommonUtils.guardarImagen(this, "Objeto", (imgObjeto.drawable as BitmapDrawable).bitmap)
                editor.putString("imagenObjeto", rutaObjeto)
                editor.putString("nombreObjeto", nombreObjeto)
            }else{
                editor.putString("nombreObjeto", "")
                editor.putString("imagenObjeto", "")
                nombreObjeto = ""
            }

            if(lblInfoUsuario.isChecked){
                //rutaUsuarioTEA = CommonUtils.crearRuta(this, (imgUsuarioTEA.drawable as BitmapDrawable).bitmap, "Usuario")
                rutaUsuarioTEA = CommonUtils.guardarImagen(this, "Usuario", (imgUsuarioTEA.drawable as BitmapDrawable).bitmap)
                editor.putString("imagenUsuarioTEA", rutaUsuarioTEA)
                editor.putString("nombreUsuarioTEA", nombreUsuarioTEA)
            }else{
                editor.putString("nombreUsuarioTEA", "")
                editor.putString("imagenUsuarioTEA", "")
                nombreUsuarioTEA = ""
            }
            editor.apply()

            val idUsuario = prefs.getString("idUsuario", "")
            try {
                val usuario = Usuario()
                usuario.guardarConfiguracion(nombreUsuarioPlanificador, username, nombreUsuarioTEA, nombreObjeto, rutaPlanificador, rutaUsuarioTEA, rutaObjeto, idUsuario, this)
            }catch (e: Exception){
                Toast.makeText(this, getString(R.string.toast_error_guardar_configuracion), Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    private fun getImages(){
        val value = prefs.getString("imagenPlanificadorConfig", "")
        if (value != "" && value != "null") {
            imgUsuarioPlanificador.background = null
            imgUsuarioPlanificador.setImageURI(Uri.parse(value))
        }

        val value2 = prefs.getString("imageUsuarioTEAConfig", "")
        if (value2 != "" && value2 != "null") {
            lblInfoUsuario.isChecked = true
            imgUsuarioTEA.background = null
            imgUsuarioTEA.setImageURI(Uri.parse(value2))
        }

        val value3 = prefs.getString("imageObjetoConfig", "")
        if (value3 != "" && value3 != "null") {
            lblObjeto.isChecked = true
            imgObjeto.background = null
            imgObjeto.setImageURI(Uri.parse(value3))
        }
    }
}