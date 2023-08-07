package com.example.plantea.presentacion.actividades.planificador

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PreLoginActivity
import com.google.android.material.textfield.TextInputLayout
import java.security.MessageDigest

class PasswordActivity : AppCompatActivity() {
    private lateinit var viejaPass: TextInputLayout
    private lateinit var nuevaPass: TextInputLayout
    private lateinit var confirmaPass: TextInputLayout
    private lateinit var btn_guardar: Button
    private var actualizado: Boolean = false
    var usuario = Usuario_Planificador()
    private lateinit var backButton: Button

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        viejaPass = findViewById(R.id.txt_PassActual)
        nuevaPass = findViewById(R.id.txt_NuevaPass)
        confirmaPass = findViewById(R.id.txt_RepPass)
        btn_guardar = findViewById(R.id.btn_Guardar)
        backButton = findViewById(R.id.goBackButton)


        backButton.setOnClickListener{
            finish()
        }

        //Este método se ejecutará al seleccionar el boton guardar
        btn_guardar.setOnClickListener {

            viejaPass.error = null
            nuevaPass.error = null
            confirmaPass.error = null

            val emptyTextViews = mutableListOf<TextView>()

            if (viejaPass.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(viejaPass.editText!!)
                viejaPass.error = "ESTO ES UN ERROR"
            }

            if (nuevaPass.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(nuevaPass.editText!!)
                nuevaPass.error = "ESTO ES UN ERROR"
            }

            if (confirmaPass.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(confirmaPass.editText!!)
                confirmaPass.error = "ESTO ES UN ERROR"
            }

            if (emptyTextViews.isNotEmpty()) {
                Toast.makeText(applicationContext, "Debes completar todos los campos",  Toast.LENGTH_LONG).show()
            } else {
                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                val username = prefs.getString("username", "")
                if (username != null){
                    val passCifrada = hashPassword(confirmaPass.editText?.text.toString())
                    val nuevaPassCifrada = hashPassword(nuevaPass.editText?.text.toString())
                    actualizado = usuario.confirmarPass(username, viejaPass.editText?.text.toString(), nuevaPassCifrada, passCifrada, this@PasswordActivity)
                }
                if (actualizado) {
                    Toast.makeText(applicationContext, "Contraseña actualizada", Toast.LENGTH_LONG)
                        .show()
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error al actualizar. Introduce de nuevo los datos. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
                val popupMenu = PopupMenu(this@PasswordActivity, findViewById(R.id.item_ayuda) )
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
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

}