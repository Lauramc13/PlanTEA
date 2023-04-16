package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Intent
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
import com.example.plantea.dominio.Usuario_Planificador
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var icono_cerrar_login: ImageView
    private lateinit var icono_ayuda: ImageView
    private lateinit var image_Planificador: ImageView
    private lateinit var image_UsuarioTEA: ImageView
    private lateinit var conectorBD: ConectorBD
    private lateinit var password: TextView
    private lateinit var nombrePlanificador: TextView
    private lateinit var nombreUsuarioTEA: TextView
    private lateinit var btn_acceder: Button
    private lateinit var cardUsuarioTEA: CardView
    private lateinit var cardUsuarioPlanificador: CardView
    var usuario = Usuario_Planificador()
    private var info_usuario = false
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
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
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        info_usuario = prefs.getBoolean("info_usuario", false)
        if (!info_usuario) {
            cardUsuarioTEA.visibility = View.GONE
        }
        val rutaUsuarioTEA = prefs.getString("imagenUsuarioTEA", "")
        val rutaPlanificador = prefs.getString("imagenPlanificador", "")
        nombrePlanificador.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
        nombreUsuarioTEA.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
        image_UsuarioTEA.setImageURI(Uri.parse(rutaUsuarioTEA))
        image_Planificador.setImageURI(Uri.parse(rutaPlanificador))

        //Este método se ejecutará al pinchar sobre la imagen del rol planificador
        cardUsuarioPlanificador.setOnClickListener {
            crearDialogoLogin()
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
            if (password.text.toString() == "") {
                Toast.makeText(applicationContext, "Introduce la contraseña", Toast.LENGTH_LONG)
                    .show()
            } else {
                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                val username = prefs.getString("username", "")
                if(username != null){
                    val passCorrecta = usuario.comprobarPass(username, password.text.toString(), this@MainActivity)
                    if (passCorrecta) {
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

}