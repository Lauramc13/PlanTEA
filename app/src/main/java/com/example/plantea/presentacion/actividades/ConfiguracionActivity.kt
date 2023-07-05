package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var img_usuarioPlanificador: ImageView
    private lateinit var img_usuarioTEA: ImageView
    private lateinit var img_objeto: ImageView
    private lateinit var txt_Planificador: TextView
    private lateinit var txt_UsuarioTEA: TextView
    private lateinit var txt_objeto: TextView
    private lateinit var btn_guardar: Button
    private lateinit var btn_password: Button
    private lateinit var btn_notificacion: SwitchCompat
    private lateinit var lbl_infoUsuario: SwitchCompat
    private lateinit var lbl_objeto: SwitchCompat
    private lateinit var semana: CheckBox
    private lateinit var dia: CheckBox
    private lateinit var hora: CheckBox
    private var es_planificador = false
    private var es_objeto = false
    private var notificacion_activa = false
    private var info_usuario = false
    private var info_objeto = false

    lateinit var btn_logout: Button
    private lateinit var icono_cerrar_login: ImageView

    var usuario = Usuario_Planificador()



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
        lbl_objeto = findViewById(R.id.lbl_objeto)
        semana = findViewById(R.id.checkBox_semana)
        dia = findViewById(R.id.checkBox_dia)
        hora = findViewById(R.id.checkBox_hora)
        txt_UsuarioTEA.isEnabled = false
        img_usuarioTEA.isEnabled = false
        lbl_infoUsuario.isChecked = false
        lbl_objeto.isChecked = false

        //Preferencias
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Recuperamos la información cuando no es la primera vez de acceso.
        val userAccount = prefs.getBoolean("userAccount", false)

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
            info_objeto = prefs.getBoolean("info_objeto", false)
            if(info_objeto){
                lbl_objeto.isChecked = true
                txt_objeto.isEnabled = true
                img_objeto.isEnabled = true
            }else{
                lbl_objeto.isChecked = false
                txt_objeto.isEnabled = false
                img_objeto.isEnabled = false
            }
        img_usuarioPlanificador.setOnClickListener {
            es_planificador = true
            es_objeto = false
            val editor = prefs.edit()
            editor.putBoolean("editPreferences", true)
            editor.apply()
            val password = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
            startActivity(password)
        }
        img_usuarioTEA.setOnClickListener {
            es_planificador = false
            es_objeto = false
            val editor = prefs.edit()
            editor.putBoolean("editPreferences", true)
            editor.putBoolean("info_usuario", lbl_infoUsuario.isChecked)
            editor.apply()
            val password = Intent(applicationContext, MenuAvataresTEActivity::class.java)
            startActivity(password)
        }
        img_objeto.setOnClickListener {
            es_objeto = true
            es_planificador = false
            val editor = prefs.edit()
            editor.putBoolean("editPreferences", true)
            editor.putBoolean("info_objeto", lbl_objeto.isChecked)
            editor.apply()
            val password = Intent(applicationContext, MenuObjetosActivity::class.java)
            startActivity(password)
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

        lbl_objeto.setOnCheckedChangeListener { _, isChecked ->
            txt_objeto.isEnabled = isChecked
            img_objeto.isEnabled = isChecked
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
            } else if ((txt_objeto.text.toString().isEmpty() || img_objeto.drawable == null) && lbl_objeto.isChecked) {
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
                val rutaPlanificador = crearRuta(img_usuarioPlanificador, "Planificador")
                var rutaUsuarioTEA= ""
                var rutaObjeto = ""
                if (lbl_infoUsuario.isChecked) {
                    rutaUsuarioTEA = crearRuta(img_usuarioTEA, "Usuario")
                }
                if (lbl_objeto.isChecked) {
                    rutaObjeto = crearRuta(img_objeto, "Objeto")
                }

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)

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
                editor.putBoolean("info_objeto", lbl_objeto.isChecked)
                editor.putBoolean("info_usuario", lbl_infoUsuario.isChecked)
                editor.apply()

                val idUsuario = prefs.getString("idUsuario", "")
                usuario.guardarConfiguracion(nombreUsuarioPlanificador, nombreUsuarioTEA, nombreObjeto, idUsuario, this)
            }
        }

        btn_password.setOnClickListener{
            val password = Intent(applicationContext, PasswordActivity::class.java)
            startActivity(password)
        }
    }

    //Menu principal
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
            R.id.item_perfil -> {
                val popupMenu = PopupMenu(this@ConfiguracionActivity, findViewById(R.id.item_ayuda) )
                popupMenu.inflate(R.menu.popup_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_1 -> {
                            val perfil = Intent(applicationContext, ConfiguracionActivity::class.java)
                            startActivity(perfil)
                            true
                        }
                        // R.id.option_2 -> {
                        //     val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                        //     val isPlanificadorLogged = prefs.getBoolean("PlanificadorLogged", false)
                        //     if(isPlanificadorLogged){
                        //         val editor = prefs.edit()
                        //         editor.putBoolean("PlanificadorLogged", false)
                        //         editor.commit()
                        //         val plan = Intent(applicationContext, PlanActivity::class.java)
                        //         startActivity(plan)
                        //     }else{
                        //         crearDialogoLogin()
                        //     }
                        //     true
                        // }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                prefs.edit().clear().commit()
                                // val editor = prefs.edit()
                                // editor.putBoolean("userAccount", false)
                                // editor.apply()
                                val login = Intent(applicationContext, PreLoginActivity::class.java)
                                startActivity(login)
                            }
                            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                            dialogLogout.show()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            android.R.id.home -> finish()
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