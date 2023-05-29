package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PreLoginActivity
import org.w3c.dom.Text
import java.util.*

class ActividadActivity : AppCompatActivity() {
    lateinit var listaPictogramas: ArrayList<Pictograma>
    var plan = Planificacion()
    lateinit var titulo: TextView
    lateinit var cardVideo: CardView
    lateinit var cardObjeto: CardView

    lateinit var img_objeto: ImageView
    lateinit var img_animacion: ImageView
    lateinit var txt_objeto: TextView

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
        setContentView(R.layout.activity_actividades)

        cardVideo = findViewById(R.id.card_video)
        cardObjeto = findViewById(R.id.card_objeto)

        cardVideo.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"));
            startActivity(intent);
        }

        cardObjeto.setOnClickListener{
            val dialogLogout = Dialog(this)
            dialogLogout.setContentView(R.layout.dialogo_actividad)
            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            img_objeto = dialogLogout.findViewById(R.id.imageObjeto)
            img_animacion = dialogLogout.findViewById(R.id.img_animacion)
            txt_objeto = dialogLogout.findViewById(R.id.lbl_nombreObjeto)


            rotateImageWithAnimation(img_animacion, 260f, 6000)

            val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

            txt_objeto.text = prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())


            if (prefs.getString("imagenObjeto", "") === "") {
                img_objeto.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            } else {
                img_objeto.background = null
                img_objeto.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
            }

            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
            dialogLogout.show()
        }


    }

    fun rotateImageWithAnimation(imageView: ImageView, degrees: Float, duration: Long) {
        val rotateAnimation = RotateAnimation(0f, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.duration = duration
        rotateAnimation.fillAfter = true

        imageView.startAnimation(rotateAnimation)
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
                val popupMenu = PopupMenu(this@ActividadActivity, findViewById(R.id.item_ayuda))
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
            android.R.id.home -> {
                finish() // Go back to the previous screen
                return true
            }
        }
        return true
    }

}