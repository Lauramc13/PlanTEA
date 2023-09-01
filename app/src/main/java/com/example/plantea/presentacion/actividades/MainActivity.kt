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
import com.example.plantea.dominio.GestionNavegacion
import com.example.plantea.dominio.Usuario_Planificador
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var image_Planificador: ImageView
    private lateinit var image_UsuarioTEA: ImageView
    private lateinit var conectorBD: ConectorBD
    private lateinit var password: TextInputLayout
    private lateinit var password2: TextInputLayout
    private lateinit var nombrePlanificador: TextView
    private lateinit var nombreUsuarioTEA: TextView
    private lateinit var btn_acceder: Button
    private lateinit var cardUsuarioTEA: CardView
    private lateinit var cardUsuarioPlanificador: CardView
    private lateinit var preferencias: Button
    private lateinit var buttonLogout : Button
    private lateinit var dialogLogout : Dialog
    lateinit var prefs: SharedPreferences
    private var navigationHandler = GestionNavegacion()

    var usuario = Usuario_Planificador()
    private var info_usuario = false

    lateinit var btn_logout: Button
    private lateinit var icono_cerrar_login: ImageView
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
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
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        info_usuario = prefs.getBoolean("info_usuario", false)
        if (!info_usuario) {
            cardUsuarioTEA.visibility = View.GONE
        }else{
            cardUsuarioTEA.visibility = View.VISIBLE
        }
        nombrePlanificador.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
        nombreUsuarioTEA.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
        image_UsuarioTEA.setImageDrawable(null)
        image_UsuarioTEA.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
        image_Planificador.setImageDrawable(null)
        image_Planificador.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))

        //val imageUri = Uri.parse(prefs.getString("imagenPlanificador", ""))
        //preferencias.background = Drawable.createFromPath(imageUri.path)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conectorBD = ConectorBD(this)
        conectorBD.abrir()
        conectorBD.cerrar()
        image_Planificador = findViewById(R.id.image_RolPlanificador)
        image_UsuarioTEA = findViewById(R.id.image_RolTEA)
        nombrePlanificador = findViewById(R.id.lbl_nombrePlanificador)
        nombreUsuarioTEA = findViewById(R.id.lbl_nombreUsuarioTEA)
        cardUsuarioPlanificador = findViewById(R.id.cardViewPlanificador)
        cardUsuarioTEA = findViewById(R.id.cardViewUsuarioTEA)
        preferencias = findViewById(R.id.image_RolPlanificador2)
        buttonLogout = findViewById(R.id.btn_logout)
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Preferencias
        configurarDatos()

        //Este método se ejecutará al pinchar sobre la imagen del rol planificador
        cardUsuarioPlanificador.setOnClickListener {
            if (!info_usuario) {
                val editor = prefs.edit()
                editor.putBoolean("PlanificadorLogged", true)
                editor.apply()
                //val intent = Intent(applicationContext, MenuActivity::class.java)
                val intent = Intent(applicationContext, PlanActivity::class.java)
                startActivity(intent)
            }else{
                val email: String = prefs.getString("email", "") ?: ""
                if(usuario.comprobarPass(email, "", this)){
                    crearDialogoNewPass()
                }else{
                    navigationHandler.crearDialogoLogin(this)
                }
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
            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
            btn_logout.setOnClickListener {
                prefs.edit().clear().apply()
                val intent = Intent(this, PreLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finishAffinity()
            }
            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
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
        password = dialogLogin.findViewById(R.id.txt_Password)
        password2 = dialogLogin.findViewById(R.id.txt_Password2)
        btn_acceder = dialogLogin.findViewById(R.id.btn_login)
        icono_cerrar_login = dialogLogin.findViewById(R.id.icono_CerrarDialogo)
        btn_acceder.setOnClickListener {
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
                    usuario.crearPassword(email, passCifrada, this)
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
        icono_cerrar_login.setOnClickListener { dialogLogin.dismiss() }
        dialogLogin.show()
    }

}