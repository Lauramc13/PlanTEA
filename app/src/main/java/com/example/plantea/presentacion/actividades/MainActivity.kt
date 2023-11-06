package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var imagePlanificador: ImageView
    private lateinit var imageUsuarioTEA: ImageView
    private lateinit var nombrePlanificador: TextView
    private lateinit var nombreUsuarioTEA: TextView
    private lateinit var cardUsuarioTEA: CardView
    private lateinit var cardUsuarioPlanificador: CardView

    private lateinit var dialogLogout : Dialog
    private lateinit var iconoCerrar: ImageView
    private var navigationHandler = NavegacionUtils()

    var usuario = Usuario()
    private var infoUsuario = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Comprobamos la orientacion de la pantalla
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        configurarDatos()
    }

    fun configurarDatos(){
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

        //val imageUri = Uri.parse(prefs.getString("imagenPlanificador", ""))
        //preferencias.background = Drawable.createFromPath(imageUri.path)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val conectorBD = ConectorBD(this)
        conectorBD.abrir()
        conectorBD.cerrar()
        imagePlanificador = findViewById(R.id.image_RolPlanificador)
        imageUsuarioTEA = findViewById(R.id.image_RolTEA)
        nombrePlanificador = findViewById(R.id.lbl_nombrePlanificador)
        nombreUsuarioTEA = findViewById(R.id.lbl_nombreUsuarioTEA)
        cardUsuarioPlanificador = findViewById(R.id.cardViewPlanificador)
        cardUsuarioTEA = findViewById(R.id.cardViewUsuarioTEA)
        val preferencias: Button = findViewById(R.id.image_RolPlanificador2)
        val buttonLogout: Button = findViewById(R.id.btn_logout)
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Preferencias
        configurarDatos()

        //Este método se ejecutará al pinchar sobre la imagen del rol planificador
        cardUsuarioPlanificador.setOnClickListener {
            if (!infoUsuario) {
                val editor = prefs.edit()
                editor.putBoolean("PlanificadorLogged", true)
                editor.apply()
                //val intent = Intent(applicationContext, MenuActivity::class.java)
                val intent = Intent(applicationContext, PlanActivity::class.java)
                startActivity(intent)
            }else{
                navigationHandler.crearDialogoLogin(this)
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

        dialogLogout = Dialog(this)
        buttonLogout.setOnClickListener{
            dialogLogout.setContentView(R.layout.dialogo_logout)
            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val btnLogout: Button = dialogLogout.findViewById(R.id.btn_logout)
            iconoCerrar = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
            btnLogout.setOnClickListener {
                prefs.edit().clear().apply()
                val intent = Intent(this, PreLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finishAffinity()
            }
            iconoCerrar.setOnClickListener { dialogLogout.dismiss() }
            dialogLogout.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogLogout.isShowing) {
            dialogLogout.dismiss()
        }
    }

    fun crearDialogoNewPass() {
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        val dialogLogin = Dialog(this)
        dialogLogin.setContentView(R.layout.dialogo_crear_password)
        dialogLogin.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val password : TextInputLayout = dialogLogin.findViewById(R.id.txt_Password)
        val password2 :TextInputLayout = dialogLogin.findViewById(R.id.txt_Password2)
        val btnAcceder : Button = dialogLogin.findViewById(R.id.btn_login)
        iconoCerrar = dialogLogin.findViewById(R.id.icono_CerrarDialogo)
        btnAcceder.setOnClickListener {
            var isValid = true
            if (password.editText?.text.toString() == "" && password2.editText?.text.toString() == "") {
                Toast.makeText(applicationContext, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
                isValid = false
            }
            if (password.editText?.text.toString() != password2.editText?.text.toString()) {
                Toast.makeText(
                    applicationContext,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_LONG
                ).show()
                password.error = "ESTO ES UN ERROR"
                password2.error = "ESTO ES UN ERROR"
                isValid = false
            }
            if(isValid){
                val email = prefs.getString("email", "")
                if(email != null){
                    val passCifrada = navigationHandler.hashPassword(password.editText?.text.toString())
                    //usuario.crearPassword(email, passCifrada, this)
                    val editor = prefs.edit()
                    editor.putBoolean("PlanificadorLogged", true)
                    editor.apply()
                    startActivity(Intent(baseContext, PlanActivity::class.java))
                    finish()
                    finishAffinity()
                    dialogLogin.dismiss()
                }
            }
        }
        iconoCerrar.setOnClickListener { dialogLogin.dismiss() }
        dialogLogin.show()
    }

}