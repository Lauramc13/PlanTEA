package com.example.plantea.presentacion.actividades

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.example.plantea.presentacion.actividades.planificador.PasswordActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ConfiguracionActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var imgUsuarioPlanificador: ImageView
    private lateinit var imgUsuarioTEA: ImageView
    private lateinit var imgObjeto: ImageView

    private lateinit var iconEditUsuarioTEA : ImageView
    private lateinit var iconEditObjeto : ImageView

    var usuario = Usuario_Planificador()

    override fun onResume() {
        super.onResume()
        configurarDatos()
    }

    fun configurarDatos(){
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

        val txtPlanificador : TextView = findViewById(R.id.txt_nombrePlanificador)
        val txtUsuarioTEA : TextView  = findViewById(R.id.txt_nombreUsuarioTEA)
        val txtObjeto: TextView = findViewById(R.id.txt_nombreObjeto)
        val btnGuardar : Button = findViewById(R.id.btn_guardarConfiguracion)
        //val btnPassword : Button= findViewById(R.id.buttonContrasenia)
        val btnNotificacion : SwitchCompat = findViewById(R.id.switch_notificacion)
        val lblInfoUsuario : SwitchCompat = findViewById(R.id.lbl_infoUsuarioTEA)
        val lblObjeto : SwitchCompat = findViewById(R.id.lbl_objeto)
        val semana : CheckBox = findViewById(R.id.checkBox_semana)
        val dia : CheckBox = findViewById(R.id.checkBox_dia)
        val hora : CheckBox = findViewById(R.id.checkBox_hora)
        val backButton : Button = findViewById(R.id.goBackButton)

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
        txtPlanificador.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
        txtUsuarioTEA.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
        txtObjeto.text = prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())
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
            if (txtPlanificador.text.toString().isEmpty() || txtUsuarioTEA.text.toString().isEmpty() && lblInfoUsuario.isChecked
            ) {
                Toast.makeText(applicationContext, "Se necesita un nombre para cada usuario", Toast.LENGTH_LONG).show()
            } else if (imgUsuarioPlanificador.drawable == null || imgUsuarioTEA.drawable == null && lblInfoUsuario.isChecked) {
                Toast.makeText(applicationContext, "Se necesita una imagen para cada usuario", Toast.LENGTH_LONG).show()
            } else if ((txtObjeto.text.toString().isEmpty() || imgObjeto.drawable == null) && lblObjeto.isChecked) {
                Toast.makeText(applicationContext, "Se necesita una imagen y nombre del objeto tranquilizador", Toast.LENGTH_LONG).show()
            } else {

                //Obtener nombres de los usuarios y objeto
                val nombreUsuarioPlanificador = txtPlanificador.text.toString()
                val nombreUsuarioTEA = txtUsuarioTEA.text.toString()
                val nombreObjeto = txtObjeto.text.toString()
                val rutaPlanificador = crearRuta(imgUsuarioPlanificador, "Planificador")
                var rutaUsuarioTEA= ""
                var rutaObjeto = ""
                if (lblInfoUsuario.isChecked) {
                    rutaUsuarioTEA = crearRuta(imgUsuarioTEA, "Usuario")
                }
                if (lblObjeto.isChecked) {
                    rutaObjeto = crearRuta(imgObjeto, "Objeto")
                }

                //val intent = Intent(applicationContext, MainActivity::class.java)
               // startActivity(intent)

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
                }
                if(lblInfoUsuario.isChecked){
                    editor.putString("nombreUsuarioTEA", nombreUsuarioTEA)
                }else{
                    editor.putString("nombreUsuarioTEA", "")
                }
                editor.apply()

                val idUsuario = prefs.getString("idUsuario", "")
                usuario.guardarConfiguracion(nombreUsuarioPlanificador, nombreUsuarioTEA, nombreObjeto, rutaPlanificador, rutaUsuarioTEA, rutaObjeto, idUsuario, this)
                finish()
            }
        }

       /* btnPassword.setOnClickListener{
            val password = Intent(applicationContext, PasswordActivity::class.java)
            startActivity(password)
        }*/

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun crearRuta(imagen: ImageView?, nombreImagen: String): String {
        val image = (imagen!!.drawable as BitmapDrawable).bitmap

        //Escalar imagen
        val proporcion = 500 / image.width.toFloat()
        val imagenFinal = Bitmap.createScaledBitmap(image, 500, (image.height * proporcion).toInt(), false)

        //Guardar imagen
        return guardarImagen(applicationContext, nombreImagen, imagenFinal)
    }

    private fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
        val cw = ContextWrapper(context)
        val dirImages = cw.getDir("Imagenes", MODE_PRIVATE)
        val myPath = File(dirImages, "$nombre.png")
        val fos: FileOutputStream?
        try {
            fos = FileOutputStream(myPath)
            imagen.compress(Bitmap.CompressFormat.PNG, 10, fos) // calidad a 0 imagen mas pequeña
            fos.flush()
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return myPath.absolutePath
    }

}