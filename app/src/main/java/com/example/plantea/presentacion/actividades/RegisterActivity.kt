package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var txt_email: TextInputLayout
    private lateinit var txt_username : TextInputLayout
    private lateinit var txt_password : TextInputLayout
    private lateinit var txt_password2 : TextInputLayout
    private lateinit var txt_nameplanificado : TextInputLayout
    private lateinit var txt_objeto : TextInputLayout
    private lateinit var checkUserPlanificado : SwitchCompat
    private lateinit var checkObjeto: SwitchCompat
    private lateinit var botonAyuda: MaterialButton
    private lateinit var tooltipText: TextView
    private var isGoogleUser: Boolean = false
    private lateinit var backButton: Button
    private var isClicked = true

    private var creado: Boolean = false

    var usuario = Usuario_Planificador()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        btnRegister = findViewById(R.id.btn_register)
        txt_name = findViewById(R.id.txt_Name)
        txt_email = findViewById(R.id.txt_Email)
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

        val intent = intent
        isGoogleUser = intent.getBooleanExtra("IS_GOOGLE_USER", false)
        if (isGoogleUser) {
            txt_email.editText?.setText(intent.getStringExtra("EMAIL"))
            txt_name.editText?.setText(intent.getStringExtra("NAME"))
            txt_password.visibility = View.GONE
            txt_password2.visibility = View.GONE
        }

        botonAyuda.setOnClickListener {
            val slideTransition = Slide(Gravity.END)
            slideTransition.duration = 800
            val parentView = findViewById<RelativeLayout>(R.id.relativeLayoutTooltip)
            val pathInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)
            slideTransition.interpolator = pathInterpolator
            TransitionManager.beginDelayedTransition(parentView, slideTransition)

            isClicked = !isClicked
            updateButtonIcon()
            if (isClicked) tooltipText.visibility = View.GONE else tooltipText.visibility =
                View.VISIBLE
        }

        backButton.setOnClickListener {
            finish()
        }

        checkUserPlanificado.setOnCheckedChangeListener { _, isChecked ->
            txt_nameplanificado.isEnabled = isChecked
        }

        checkObjeto.setOnCheckedChangeListener { _, isChecked ->
            txt_objeto.isEnabled = isChecked
        }

        btnRegister.setOnClickListener {

            // Clear previous errors
            txt_name.error = null
            txt_email.error = null
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
            if (txt_email.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(txt_email.editText!!)
                txt_email.error = "ESTO ES UN ERROR"
            }
            if (txt_username.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(txt_username.editText!!)
                txt_username.error = "ESTO ES UN ERROR"
            }
            if (txt_password.editText?.text.toString().isEmpty() && !isGoogleUser) {
                emptyTextViews.add(txt_password.editText!!)
                txt_password.error = "ESTO ES UN ERROR"
            }
            if (txt_password2.editText?.text.toString().isEmpty() && !isGoogleUser) {
                emptyTextViews.add(txt_password2.editText!!)
                txt_password2.error = "ESTO ES UN ERROR"
            }
            if (txt_objeto.editText?.text.toString().isEmpty() && checkObjeto.isChecked) {
                emptyTextViews.add(txt_objeto.editText!!)
                txt_objeto.error = "ESTO ES UN ERROR"
            }
            if (txt_nameplanificado.editText?.text.toString()
                    .isEmpty() && checkUserPlanificado.isChecked
            ) {
                emptyTextViews.add(txt_objeto.editText!!)
                txt_nameplanificado.error = "ESTO ES UN ERROR"
            }

            var isAccountValid = true
            var error = ""

            //  Handler().postDelayed({

            if (!Patterns.EMAIL_ADDRESS.matcher(txt_email.editText?.text.toString()).matches()) {
                error = "La dirección de correo electrónico no es válida"
                txt_email.error = "ESTO ES UN ERROR"
                isAccountValid = false
            }

            if (txt_password.editText?.text.toString() != txt_password2.editText?.text.toString()) {
                error = "Las contraseñas no coinciden"
                txt_password.error = "ESTO ES UN ERROR"
                txt_password2.error = "ESTO ES UN ERROR"
                isAccountValid = false
            }

            if (emptyTextViews.isNotEmpty()) {
                error = "Tienes que rellenar todos los campos"
                isAccountValid = false
            }

            if (isAccountValid) {
                var passCifrada = ""
                if (!isGoogleUser) {
                    passCifrada = hashPassword(txt_password.editText?.text.toString())
                }
                creado = usuario.crearUsuario(
                    txt_name.editText?.text.toString(),
                    txt_email.editText?.text.toString(),
                    txt_username.editText?.text.toString(),
                    passCifrada,
                    txt_objeto.editText?.text.toString(),
                    txt_nameplanificado.editText?.text.toString(),
                    this@RegisterActivity
                )
                if (creado) {
                    val id = usuario.consultarId(
                        txt_email.editText?.text.toString(),
                        this@RegisterActivity
                    )
                    val editor = prefs.edit()
                    editor.putString("idUsuario", id)
                    Log.d("USUARIO", "$id")
                    editor.putString("username", txt_username.editText?.text.toString())
                    editor.putBoolean("info_usuario", checkUserPlanificado.isChecked)
                    editor.putBoolean("info_objeto", checkObjeto.isChecked)
                    editor.putString("email", txt_email.editText?.text.toString())
                    editor.putString("nombrePlanificador", txt_name.editText?.text.toString())
                    editor.putString(
                        "nombreUsuarioTEA",
                        txt_nameplanificado.editText?.text.toString()
                    )
                    editor.putString("nombreObjeto", txt_objeto.editText?.text.toString())
                    editor.putBoolean("isGoogleUser", isGoogleUser)
                    editor.putBoolean("editPreferences", false)
                    editor.apply()
                    val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    txt_username.error = "ESTO ES UN ERROR"
                    txt_email.error = "ESTO ES UN ERROR"
                    Toast.makeText(
                        applicationContext,
                        "El nombre de usuario o correo introducido ya existe",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }else{
                Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
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