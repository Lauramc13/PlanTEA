package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ninio.CuadernoActivity
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var cardCalendario: CardView
    private lateinit var cardEmociones: CardView
    private lateinit var cardPlanificacion: CardView
    lateinit var btn_logout: Button
    lateinit var icono_cerrar_login : AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        cardCalendario = findViewById(R.id.card_Calendario)
        cardEmociones = findViewById(R.id.card_Emociones)
        cardPlanificacion = findViewById(R.id.card_Planificacion)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        cardCalendario.setOnClickListener {
            val intent = Intent(applicationContext, CalendarioActivity::class.java)
            startActivity(intent)
        }
        cardEmociones.setOnClickListener {
            val intent = Intent(applicationContext, CuadernoActivity::class.java)
            startActivity(intent)
        }
        cardPlanificacion.setOnClickListener {
            val intent = Intent(applicationContext, PlanActivity::class.java)
            startActivity(intent)
        }
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
                val popupMenu = PopupMenu(this@MenuActivity, findViewById(R.id.item_ayuda) )
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
                                val editor = prefs.edit()
                                editor.putBoolean("userAccount", false)
                                editor.apply()
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

}