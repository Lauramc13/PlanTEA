package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.SharedPreferences
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
    lateinit var prefs: SharedPreferences
    private lateinit var btnRegister: Button
    private lateinit var txtName : TextInputLayout
    private lateinit var txtEmail: TextInputLayout
    private lateinit var txtUsername : TextInputLayout
    private lateinit var txtPassword : TextInputLayout
    private lateinit var txtPassword2 : TextInputLayout
    private lateinit var txtNameplanificado : TextInputLayout
    private lateinit var txtObjeto : TextInputLayout
    private lateinit var checkUserPlanificado : SwitchCompat
    private lateinit var checkObjeto: SwitchCompat
    private lateinit var botonAyuda: MaterialButton
    private lateinit var tooltipText: TextView
    private var isGoogleUser: Boolean = false
    private lateinit var backButton: Button
    private var isClicked = true
    private var creado: Boolean = false

    val emptyTextViews = mutableListOf<TextView>()

    var usuario = Usuario_Planificador()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        btnRegister = findViewById(R.id.btn_register)
        txtName = findViewById(R.id.txt_Name)
        txtEmail = findViewById(R.id.txt_Email)
        txtUsername = findViewById(R.id.txt_UserName)
        txtPassword = findViewById(R.id.txt_password)
        txtPassword2 = findViewById(R.id.txt_password2)
        txtObjeto = findViewById(R.id.txt_objeto)
        txtNameplanificado = findViewById(R.id.txt_nombreplanificado)
        checkUserPlanificado = findViewById(R.id.check_Plaificado)
        checkObjeto = findViewById(R.id.check_Objeto)
        botonAyuda = findViewById(R.id.buttonAyudaActividad)
        tooltipText = findViewById(R.id.tooltipText)
        backButton = findViewById(R.id.goBackButton)

        txtNameplanificado.isEnabled = false
        txtObjeto.isEnabled = false
        checkUserPlanificado.isChecked = false
        checkObjeto.isChecked = false

        val intent = intent
        isGoogleUser = intent.getBooleanExtra("IS_GOOGLE_USER", false)
        if (isGoogleUser) {
            txtEmail.editText?.setText(intent.getStringExtra("EMAIL"))
            txtName.editText?.setText(intent.getStringExtra("NAME"))
            txtPassword.visibility = View.GONE
            txtPassword2.visibility = View.GONE
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
            txtNameplanificado.isEnabled = isChecked
        }

        checkObjeto.setOnCheckedChangeListener { _, isChecked ->
            txtObjeto.isEnabled = isChecked
        }

        btnRegister.setOnClickListener {
            // Clear previous errors
            txtName.error = null
            txtEmail.error = null
            txtUsername.error = null
            txtPassword.error = null
            txtObjeto.error = null
            txtPassword2.error = null
            txtNameplanificado.error = null

            comprobarTextViews()
            val errorMessage = createAccount()
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun comprobarTextViews() {
        if (txtName.editText?.text.toString().isEmpty()) {
            emptyTextViews.add(txtName.editText!!)
            txtName.error = "ESTO ES UN ERROR"
        }
        if (txtEmail.editText?.text.toString().isEmpty()) {
            emptyTextViews.add(txtEmail.editText!!)
            txtEmail.error = "ESTO ES UN ERROR"
        }
        if (txtUsername.editText?.text.toString().isEmpty()) {
            emptyTextViews.add(txtUsername.editText!!)
            txtUsername.error = "ESTO ES UN ERROR"
        }
        if (txtPassword.editText?.text.toString().isEmpty() && !isGoogleUser) {
            emptyTextViews.add(txtPassword.editText!!)
            txtPassword.error = "ESTO ES UN ERROR"
        }
        if (txtPassword2.editText?.text.toString().isEmpty() && !isGoogleUser) {
            emptyTextViews.add(txtPassword2.editText!!)
            txtPassword2.error = "ESTO ES UN ERROR"
        }
        if (txtObjeto.editText?.text.toString().isEmpty() && checkObjeto.isChecked) {
            emptyTextViews.add(txtObjeto.editText!!)
            txtObjeto.error = "ESTO ES UN ERROR"
        }
        if (txtNameplanificado.editText?.text.toString().isEmpty() && checkUserPlanificado.isChecked) {
            emptyTextViews.add(txtObjeto.editText!!)
            txtNameplanificado.error = "ESTO ES UN ERROR"
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showError(editText: TextInputLayout?) {
        editText?.error = "ESTO ES UN ERROR"
    }

    private fun createAccount(): String {
        val email = txtEmail.editText?.text.toString()
        val password = txtPassword.editText?.text.toString()
        val password2 = txtPassword2.editText?.text.toString()
        val name = txtName.editText?.text.toString()
        val username = txtUsername.editText?.text.toString()
        val objeto = txtObjeto.editText?.text.toString()
        val namePlanificado = txtNameplanificado.editText?.text.toString()

        var error = ""
        var isAccountValid = true

        if (!isValidEmail(email)) {
            error = "La dirección de correo electrónico no es válida"
            showError(txtEmail)
            isAccountValid = false
        }

        if (password != password2) {
            error = "Las contraseñas no coinciden"
            showError(txtPassword)
            showError(txtPassword2)
            isAccountValid = false
        }

        if (emptyTextViews.isNotEmpty()) {
            error = "Tienes que rellenar todos los campos"
            isAccountValid = false
        }

        if (isAccountValid) {
            val passCifrada = if (!isGoogleUser) hashPassword(password) else ""
            creado = usuario.crearUsuario(name, email, username, passCifrada, objeto, namePlanificado, this@RegisterActivity)
            if (creado) {
                val id = usuario.consultarId(email, this@RegisterActivity)
                val editor = prefs.edit()
                editor.putString("idUsuario", id)
                editor.putString("username", username)
                editor.putBoolean("info_usuario", checkUserPlanificado.isChecked)
                editor.putBoolean("info_objeto", checkObjeto.isChecked)
                editor.putString("email", email)
                editor.putString("nombrePlanificador", name)
                editor.putString("nombreUsuarioTEA", namePlanificado)
                editor.putString("nombreObjeto", objeto)
                editor.putBoolean("isGoogleUser", isGoogleUser)
                editor.putBoolean("editPreferences", false)
                editor.apply()
                val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                error = "El nombre de usuario o correo introducido ya existe"
                showError(txtUsername)
                showError(txtEmail)
            }
        }
        return error
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