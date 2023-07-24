package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.google.android.material.textfield.TextInputLayout
import java.security.MessageDigest
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var icono_ayuda: ImageView
    private lateinit var image_Planificador: ImageView
    private lateinit var image_UsuarioTEA: ImageView
    private lateinit var conectorBD: ConectorBD
    private lateinit var password: TextInputLayout
    private lateinit var nombrePlanificador: TextView
    private lateinit var nombreUsuarioTEA: TextView
    private lateinit var btn_acceder: Button
    private lateinit var cardUsuarioTEA: CardView
    private lateinit var cardUsuarioPlanificador: CardView
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
        Log.d("asf", "HOLAA")
        configurarDatos()
    }

    fun configurarDatos(){
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        info_usuario = prefs.getBoolean("info_usuario", false)
        Log.d("asf", info_usuario.toString())
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conectorBD = ConectorBD(this)
        conectorBD.abrir()
        conectorBD.cerrar()
        image_Planificador = findViewById(R.id.image_RolPlanificador)
        image_UsuarioTEA = findViewById(R.id.image_RolTEA)
        icono_ayuda = findViewById(R.id.image_Manual)
        nombrePlanificador = findViewById(R.id.lbl_nombrePlanificador)
        nombreUsuarioTEA = findViewById(R.id.lbl_nombreUsuarioTEA)
        cardUsuarioPlanificador = findViewById(R.id.cardViewPlanificador)
        cardUsuarioTEA = findViewById(R.id.cardViewUsuarioTEA)

        //Preferencias
        configurarDatos()

        //Este método se ejecutará al pinchar sobre la imagen del rol planificador
        cardUsuarioPlanificador.setOnClickListener {
            if (!info_usuario) {
                val intent = Intent(applicationContext, MenuActivity::class.java)
                startActivity(intent)
            }else{
                crearDialogoLogin()
            }
        }

        //Este método se ejecutará al pinchar sobre la imagen del rol niño
        cardUsuarioTEA.setOnClickListener {
            val intent = Intent(applicationContext, PlanActivity::class.java)
            startActivity(intent)
        }
        icono_ayuda.setOnClickListener {
            val intent = Intent(applicationContext, ManualActivity::class.java)
            startActivity(intent)
        }
    }

    fun crearDialogoLogin() {
        val dialogLogin = Dialog(this)
        dialogLogin.setContentView(R.layout.dialogo_login)
        dialogLogin.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        password = dialogLogin.findViewById(R.id.txt_Password)
        btn_acceder = dialogLogin.findViewById(R.id.btn_login)
        icono_cerrar_login = dialogLogin.findViewById(R.id.icono_CerrarDialogo)
        btn_acceder.setOnClickListener {
            if (password.editText?.text.toString() == "") {
                Toast.makeText(applicationContext, "Introduce la contraseña", Toast.LENGTH_LONG)
                    .show()
            } else {
                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                val username = prefs.getString("username", "")
                if(username != null){
                    val passCifrada = hashPassword(password.editText?.text.toString())
                    val passCorrecta = usuario.comprobarPass(username, passCifrada, this@MainActivity)
                    if (passCorrecta) {
                        val editor = prefs.edit()
                        editor.putBoolean("PlanificadorLogged", true)
                        editor.apply()
                        //val intent = Intent(applicationContext, TutorialActivity::class.java)
                        val intent = Intent(applicationContext, MenuActivity::class.java)
                        startActivity(intent)
                        dialogLogin.dismiss()
                    } else {
                        Toast.makeText(applicationContext, "Error en la contraseña", Toast.LENGTH_LONG)
                            .show()
                    }
                }

            }
        }
        icono_cerrar_login.setOnClickListener { dialogLogin.dismiss() }
        dialogLogin.show()
    }

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
                val popupMenu = PopupMenu(this@MainActivity, findViewById(R.id.item_ayuda) )
                popupMenu.inflate(R.menu.popup_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_1 -> {
                            val perfil = Intent(applicationContext, ConfiguracionActivity::class.java)
                            startActivity(perfil)
                            true
                        }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                prefs.edit().clear().apply()
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

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}