package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity(){

    private lateinit var btnRegister: Button
    private lateinit var txt_name : TextInputLayout
    private lateinit var txt_username : TextInputLayout
    private lateinit var txt_password : TextInputLayout
    private lateinit var txt_password2 : TextInputLayout
    private lateinit var txt_nameplanificado : TextInputLayout
    private lateinit var txt_objeto : TextInputLayout
    private lateinit var checkUserPlanificado : SwitchCompat
    private lateinit var checkObjeto: SwitchCompat
    private lateinit var botonAyuda: MaterialButton
    private lateinit var tooltipText: TextView
    private lateinit var backButton: Button
    private var isClicked = true

    private var creado: Boolean = false

    var usuario = Usuario_Planificador()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        //val userAccount = prefs.getBoolean("userAccount", false)

       // if(userAccount){
          //  val intent = Intent(applicationContext, MainActivity::class.java)
          //  startActivity(intent)
      //  }
        setContentView(R.layout.activity_register)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnRegister = findViewById(R.id.btn_register)
        txt_name = findViewById(R.id.txt_Name)
        txt_username = findViewById(R.id.txt_UserName)
        txt_password = findViewById(R.id.txt_password)
        txt_password2 = findViewById(R.id.txt_password2)
        txt_objeto = findViewById(R.id.txt_objeto)
        txt_nameplanificado = findViewById(R.id.txt_nombreplanificado)
        checkUserPlanificado = findViewById(R.id.check_Plaificado)
        checkObjeto = findViewById(R.id.check_Objeto)
        botonAyuda = findViewById(R.id.buttonAyudaActividad)
        tooltipText = findViewById(R.id.tooltipText)
        backButton = findViewById(R.id.goBackButton)

        txt_nameplanificado.isEnabled = false
        txt_objeto.isEnabled = false
        checkUserPlanificado.isChecked = false
        checkObjeto.isChecked = false

        botonAyuda.setOnClickListener{
            val slideTransition = Slide(Gravity.END)
            slideTransition.duration = 800
            val parentView = findViewById<RelativeLayout>(R.id.relativeLayoutTooltip)
            val pathInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)
            slideTransition.interpolator = pathInterpolator
            TransitionManager.beginDelayedTransition(parentView, slideTransition)

            isClicked = !isClicked
            updateButtonIcon()
            if (isClicked) tooltipText.visibility = View.GONE else tooltipText.visibility = View.VISIBLE
        }

        backButton.setOnClickListener{
            finish()
        }

        checkUserPlanificado.setOnCheckedChangeListener { _, isChecked ->
            txt_nameplanificado.isEnabled = isChecked
        }

        checkObjeto.setOnCheckedChangeListener{_, isChecked ->
            txt_objeto.isEnabled = isChecked
        }

        btnRegister.setOnClickListener {

            // Clear previous errors
            txt_name.error = null
            txt_username.error = null
            txt_password.error = null
            txt_objeto.error = null
            txt_password2.error = null
            txt_nameplanificado.error = null


            val emptyTextViews = mutableListOf<TextView>()

            if (txt_name.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(txt_name.editText!!)
                txt_name.error = "ESTO ES UN ERROR"
            }
            if (txt_username.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(txt_username.editText!!)
                txt_username.error = "ESTO ES UN ERROR"
            }
            if (txt_password.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(txt_password.editText!!)
                txt_password.error = "ESTO ES UN ERROR"
            }
            if (txt_password2.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(txt_password2.editText!!)
                txt_password2.error = "ESTO ES UN ERROR"
            }
            if (txt_objeto.editText?.text.toString().isEmpty() && checkObjeto.isChecked) {
                emptyTextViews.add(txt_objeto.editText!!)
                txt_objeto.error = "ESTO ES UN ERROR"
            }
            if (txt_nameplanificado.editText?.text.toString().isEmpty() && checkUserPlanificado.isChecked) {
                emptyTextViews.add(txt_objeto.editText!!)
                txt_nameplanificado.error = "ESTO ES UN ERROR"
            }

          //  Handler().postDelayed({
            if (emptyTextViews.isNotEmpty()) {
                Toast.makeText(applicationContext, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            } else {
                if( txt_password.editText?.text.toString() != txt_password2.editText?.text.toString() ){
                    Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                    txt_password.error = "ESTO ES UN ERROR"
                    txt_password2.error = "ESTO ES UN ERROR"
                }else{
                    val passCifrada = hashPassword(txt_password.editText?.text.toString())
                    creado = usuario.crearUsuario(txt_name.editText?.text.toString(), txt_username.editText?.text.toString(), passCifrada, txt_objeto.editText?.text.toString(), txt_nameplanificado.editText?.text.toString(), this@RegisterActivity)
                    if (creado) {
                        val id = usuario.consultarId(txt_username.editText?.text.toString(), this@RegisterActivity)
                        val editor = prefs.edit()
                        editor.putString("idUsuario", id)
                        Log.d("USUARIO", "$id")
                        editor.putString("username", txt_username.editText?.text.toString())
                        editor.putBoolean("info_usuario", checkUserPlanificado.isChecked)
                        editor.putBoolean("info_objeto", checkObjeto.isChecked)
                        Log.d("asf", checkObjeto.isChecked.toString())
                        editor.putString("nombrePlanificador", txt_name.editText?.text.toString())
                        editor.putString("nombreUsuarioTEA", txt_nameplanificado.editText?.text.toString())
                        editor.putString("nombreObjeto", txt_objeto.editText?.text.toString())
                        editor.putBoolean("editPreferences", false)
                        editor.apply()
                        val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        txt_username.error = "ESTO ES UN ERROR"
                        Toast.makeText(applicationContext,  "El nombre de usuario introducido ya existe", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun updateButtonIcon() {
        // Update the button's background based on the state
        val iconResource = if (isClicked) R.drawable.question_simple else R.drawable.svg_close
        val iconDrawable = ContextCompat.getDrawable(this, iconResource)
        botonAyuda.icon = iconDrawable


    }

}