package com.example.plantea.presentacion.actividades

import 	androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.planificador.PasswordActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ConfiguracionActivity : AppCompatActivity() {
    private lateinit var img_usuarioPlanificador: ImageView
    private lateinit var img_usuarioTEA: ImageView
    private lateinit var img_objeto: ImageView
    private lateinit var txt_Planificador: TextView
    private lateinit var txt_UsuarioTEA: TextView
    private lateinit var txt_objeto: TextView
    private lateinit var btn_guardar: Button
    private lateinit var btn_password: Button
    private lateinit var btn_notificacion: Switch
    private lateinit var lbl_infoUsuario: Switch
    private lateinit var semana: CheckBox
    private lateinit var dia: CheckBox
    private lateinit var hora: CheckBox
    private var es_planificador = false
    private var es_objeto = false
    private var notificacion_activa = false
    private var info_usuario = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        img_usuarioPlanificador = findViewById(R.id.img_FotoPlanificador)
        img_usuarioTEA = findViewById(R.id.img_FotoUsuarioTEA)
        img_objeto = findViewById(R.id.img_objeto)
        txt_Planificador = findViewById(R.id.txt_nombrePlanificador)
        txt_UsuarioTEA = findViewById(R.id.txt_nombreUsuarioTEA)
        txt_objeto = findViewById(R.id.txt_nombreObjeto)
        btn_guardar = findViewById(R.id.btn_guardarConfiguracion)
        btn_password = findViewById(R.id.buttonContrasenia)
        btn_notificacion = findViewById(R.id.switch_notificacion)
        lbl_infoUsuario = findViewById(R.id.lbl_infoUsuarioTEA)
        semana = findViewById(R.id.checkBox_semana)
        dia = findViewById(R.id.checkBox_dia)
        hora = findViewById(R.id.checkBox_hora)
        txt_UsuarioTEA.isEnabled = false
        img_usuarioTEA.isEnabled = false
        lbl_infoUsuario.isChecked = false

        //Preferencias
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Recuperamos la información cuando no es la primera vez de acceso.
        val userAccount = prefs.getBoolean("userAccount", false)
        if (userAccount) {
            //Imagenes y nombres
            txt_Planificador.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
            txt_UsuarioTEA.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
            txt_objeto.text = prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())
            img_objeto.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            if (prefs.getString("imagenPlanificador", "") === "") {
                img_usuarioPlanificador.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            } else {
                img_usuarioPlanificador.background = null
                img_usuarioPlanificador.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
            }
            if (prefs.getString("imagenUsuarioTEA", "") === "") {
                img_usuarioTEA.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            } else {
                img_usuarioTEA.background = null
                img_usuarioTEA.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
            }
            if (prefs.getString("imagenObjeto", "") === "") {
                img_objeto.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            } else {
                img_objeto.background = null
                img_objeto.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
            }

            //Notificaciones
            notificacion_activa = prefs.getBoolean("notificaciones", false)
            semana.isChecked = prefs.getBoolean("notificacion_semana", false)
            dia.isChecked = prefs.getBoolean("notificacion_dia", false)
            hora.isChecked = prefs.getBoolean("notificacion_hora", false)
            if (notificacion_activa) {
                btn_notificacion.isChecked = true
                semana.isEnabled = true
                dia.isEnabled = true
                hora.isEnabled = true
            } else {
                btn_notificacion.isChecked = false
                semana.isEnabled = false
                dia.isEnabled = false
                hora.isEnabled = false
            }
            info_usuario = prefs.getBoolean("info_usuario", false)
            if (info_usuario) {
                lbl_infoUsuario.isChecked = true
                txt_UsuarioTEA.isEnabled = true
                img_usuarioTEA.isEnabled = true
            } else {
                lbl_infoUsuario.isChecked = false
                txt_UsuarioTEA.isEnabled = false
                img_usuarioTEA.isEnabled = false
            }
        }
        img_usuarioPlanificador.setOnClickListener {
            abrirGaleria()
            es_planificador = true
            es_objeto = false
        }
        img_usuarioTEA.setOnClickListener {
            abrirGaleria()
            es_planificador = false
            es_objeto = false
        }
        img_objeto.setOnClickListener {
            abrirGaleria()
            es_objeto = true
            es_planificador = false
        }
        btn_notificacion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //ON
                semana.isChecked = true
                semana.isEnabled = true
                dia.isEnabled = true
                hora.isEnabled = true
            } else {
                //OFF
                semana.isEnabled = false
                dia.isEnabled = false
                hora.isEnabled = false
            }
        }
        lbl_infoUsuario.setOnCheckedChangeListener { _, isChecked ->
            txt_UsuarioTEA.isEnabled = isChecked
            img_usuarioTEA.isEnabled = isChecked
        }
        btn_guardar.setOnClickListener {
            if (txt_Planificador.text.toString().isEmpty() || txt_UsuarioTEA.text.toString().isEmpty() && lbl_infoUsuario.isChecked
            ) {
                Toast.makeText(
                    applicationContext,
                    "Se necesita un nombre para cada usuario",
                    Toast.LENGTH_LONG
                ).show()
            } else if (img_usuarioPlanificador.drawable == null || img_usuarioTEA.drawable == null && lbl_infoUsuario.isChecked) {
                Toast.makeText(
                    applicationContext,
                    "Se necesita una imagen para cada usuario",
                    Toast.LENGTH_LONG
                ).show()
            } else if (txt_objeto.text.toString().isEmpty() || img_objeto.drawable == null
            ) {
                Toast.makeText(
                    applicationContext,
                    "Se necesita una imagen y nombre del objeto tranquilizador",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //Obtener nombres de los usuarios y objeto
                val nombreUsuarioPlanificador = txt_Planificador.text.toString()
                val nombreUsuarioTEA = txt_UsuarioTEA.text.toString()
                val nombreObjeto = txt_objeto.text.toString()
                var rutaUsuarioTEA = ""
                val rutaPlanificador = crearRuta(img_usuarioPlanificador, "Planificador")
                if (lbl_infoUsuario.isChecked) {
                    rutaUsuarioTEA = crearRuta(img_usuarioTEA, "Usuario")
                }
                val rutaObjeto = crearRuta(img_objeto, "Objeto")
                if (userAccount) {
                    finish()
                } else {
                    //Abre la pantalla inicio porque es la primera vez
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }

                //Cambiamos el valor en preferencias para no acceder a configuracion en el siguiente inicio y guardamos datos de los usuarios
                val editor = prefs.edit()
                editor.putBoolean("userAccount", true)
                editor.putBoolean("iniciadaSesion", true)
                editor.putBoolean("notificaciones", btn_notificacion.isChecked)
                editor.putBoolean("notificacion_semana", semana.isChecked)
                editor.putBoolean("notificacion_dia", dia.isChecked)
                editor.putBoolean("notificacion_hora", hora.isChecked)
                editor.putString("nombrePlanificador", nombreUsuarioPlanificador)
                editor.putString("nombreUsuarioTEA", nombreUsuarioTEA)
                editor.putString("nombreObjeto", nombreObjeto)
                editor.putString("imagenPlanificador", rutaPlanificador)
                editor.putString("imagenUsuarioTEA", rutaUsuarioTEA)
                editor.putString("imagenObjeto", rutaObjeto)
                editor.putBoolean("info_usuario", lbl_infoUsuario.isChecked)
                editor.commit()
            }
        }

        btn_password.setOnClickListener{
            val password = Intent(applicationContext, PasswordActivity::class.java)
            startActivity(password)
        }
    }

    //Menu principal
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_ayuda, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.item_ayuda_menu -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
        }
        return true
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
        var fos: FileOutputStream?
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

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri: Uri? ->
        // Handle the returned URI here
        if (uri != null) {
            if (es_planificador) {
                img_usuarioPlanificador.background = null
                img_usuarioPlanificador.setImageURI(uri)
            } else if (es_objeto) {
                img_objeto.background = null
                img_objeto.setImageURI(uri)
            } else {
                img_usuarioTEA.background = null
                img_usuarioTEA.setImageURI(uri)
            }
        } else {
            Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show()
        }
    }
}