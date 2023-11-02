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
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var backButton: Button
    private var isClicked = true
    private var creado: Boolean = false
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val emptyTextViews = mutableListOf<TextView>()

    var usuario = Usuario_Planificador()

    companion object {
        const val NAME_KEY = "NAME_KEY"
        const val EMAIL_KEY = "EMAIL_KEY"
        const val USERNAME_KEY = "USERNAME_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY"
        const val PASSWORD2_KEY = "PASSWORD2_KEY"
        const val OBJETO_KEY = "OBJETO_KEY"
        const val NAMEPLANIFICADO_KEY = "NAMEPLANIFICADO_KEY"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(NAME_KEY, txtName.editText?.text.toString())
        outState.putString(EMAIL_KEY, txtEmail.editText?.text.toString())
        outState.putString(USERNAME_KEY, txtUsername.editText?.text.toString())
        outState.putString(PASSWORD_KEY, txtPassword.editText?.text.toString())
        outState.putString(PASSWORD2_KEY, txtPassword2.editText?.text.toString())
        outState.putString(OBJETO_KEY, txtObjeto.editText?.text.toString())
        outState.putString(NAMEPLANIFICADO_KEY, txtNameplanificado.editText?.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        txtName.editText?.setText(savedInstanceState.getString(NAME_KEY).toString())
        txtEmail.editText?.setText(savedInstanceState.getString(EMAIL_KEY).toString())
        txtUsername.editText?.setText(savedInstanceState.getString(USERNAME_KEY).toString())
        txtPassword.editText?.setText(savedInstanceState.getString(PASSWORD_KEY).toString())
        txtPassword2.editText?.setText(savedInstanceState.getString(PASSWORD2_KEY).toString())
        txtObjeto.editText?.setText(savedInstanceState.getString(OBJETO_KEY).toString())
        txtNameplanificado.editText?.setText(savedInstanceState.getString(NAMEPLANIFICADO_KEY).toString())
    }

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

        txtEmail.editText?.setText(intent.getStringExtra("EMAIL"))
        txtName.editText?.setText(intent.getStringExtra("NAME"))


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
        if (txtPassword.editText?.text.toString().isEmpty()) {
            emptyTextViews.add(txtPassword.editText!!)
            txtPassword.error = "ESTO ES UN ERROR"
        }
        if (txtPassword2.editText?.text.toString().isEmpty()) {
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
        val email = txtEmail.editText?.text.toString().lowercase()
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

        //la contraseña tiene que tener minimo 6 caracteres
        if (password.length < 6) {
            error = "La contraseña tiene que tener mínimo 6 caracteres"
            showError(txtPassword)
            isAccountValid = false
        }

        if (emptyTextViews.isNotEmpty()) {
            error = "Tienes que rellenar todos los campos"
            isAccountValid = false
        }

        // TODO: ORGANIZAR ESTO
        if (isAccountValid) {
            //val passCifrada = hashPassword(password)
            creado = usuario.crearUsuario(name, email, username, objeto, namePlanificado, this@RegisterActivity)
            if (creado) {
                val id = usuario.consultarId(email, this@RegisterActivity)
                val editor = prefs.edit()
                editor.putString("idUsuario", id)
                editor.putString("nombreUsuarioPlanificador", username)
                editor.putBoolean("info_usuario", checkUserPlanificado.isChecked)
                editor.putBoolean("info_objeto", checkObjeto.isChecked)
                editor.putString("email", email)
                editor.putString("nombrePlanificador", name)
                editor.putString("nombreUsuarioTEA", namePlanificado)
                editor.putString("nombreObjeto", objeto)
                editor.putBoolean("editPreferences", false)
                editor.apply()

                val user = auth.currentUser
                //Guardamos los datos en firebase
                auth.createUserWithEmailAndPassword(email, "123456")
                    .addOnCompleteListener { task ->
                        Log.w("Registration", "ha petado")

                        if (!task.isSuccessful) {
                            // El registro ha fallado
                            Log.w("Registration", "createUserWithEmail:failure", task.exception)
                        }
                    }
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

    /*private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }*/

    private fun updateButtonIcon() {
        // Update the button's background based on the state
        val iconResource = if (isClicked) R.drawable.question_simple else R.drawable.svg_close
        val iconDrawable = ContextCompat.getDrawable(this, iconResource)
        botonAyuda.icon = iconDrawable
    }

}