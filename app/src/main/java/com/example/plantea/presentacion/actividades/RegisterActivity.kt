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
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity(){
    lateinit var prefs: SharedPreferences
    private lateinit var btnRegister: Button
    lateinit var txtName : TextInputLayout
    private lateinit var txtEmail: TextInputLayout
    private lateinit var txtUsername : TextInputLayout
    private lateinit var txtPassword : TextInputLayout
    private lateinit var txtPassword2 : TextInputLayout
    private lateinit var txtNameplanificado : TextInputLayout
    private lateinit var txtObjeto : TextInputLayout
    private lateinit var checkUserPlanificado : SwitchCompat
    lateinit var checkObjeto: SwitchCompat
    private lateinit var botonAyuda: MaterialButton
    private lateinit var tooltipText: TextView
    private lateinit var backButton: Button
    private var isClicked = true
    //private var creado: Boolean = false
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //val emptyTextViews = mutableListOf<TextView>()

    var usuario = Usuario()

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

            val errorMessage = createAccount()
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun comprobarTextViewsVacios(username: String, password: String, password2: String, name: String, email: String, objeto: String, namePlanificado: String, checkedObjeto: Boolean, checkedUserTea: Boolean): Boolean {
        if (name.isEmpty()) {
            runOnUiThread{txtName.error = "ESTO ES UN ERROR"}
            return false
        }

        if (email.isEmpty()) {
            runOnUiThread{txtEmail.error = "ESTO ES UN ERROR"}
            return false
        }

        if (username.isEmpty()) {
            runOnUiThread{txtUsername.error = "ESTO ES UN ERROR"}
            return false
        }

        if (password.isEmpty()) {
            runOnUiThread { txtPassword.error = "ESTO ES UN ERROR" }
            return false
        }

        if (password2.isEmpty()) {
            runOnUiThread { txtPassword2.error = "ESTO ES UN ERROR" }
            return false
        }

        if (objeto.isEmpty() && checkedObjeto) {
            runOnUiThread { txtObjeto.error = "ESTO ES UN ERROR" }
            return false
        }

        if (namePlanificado.isEmpty() && checkedUserTea) {
            runOnUiThread { txtNameplanificado.error = "ESTO ES UN ERROR" }
            return false
        }
        return true
    }

    data class ValidationResult(val isValid: Boolean, var errorMessage: String? = null)

    fun isAccountValid(email: String, password: String, password2: String, notextViewsVacios: Boolean): ValidationResult{
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            runOnUiThread { txtEmail.error = "ESTO ES UN ERROR" }
            return  ValidationResult(false, "La dirección de correo electrónico no es válida")
        }

        if (password != password2) {
            runOnUiThread {
                txtPassword.error = "ESTO ES UN ERROR"
                txtPassword2.error = "ESTO ES UN ERROR"
            }
            return ValidationResult(false, "Las contraseñas no coinciden")
        }

        //la contraseña tiene que tener minimo 6 caracteres
        if (password.length < 6) {
            runOnUiThread {txtPassword.error = "ESTO ES UN ERROR"}
            return ValidationResult(false, "La contraseña debe tener al menos 6 caracteres")
        }

        if (!notextViewsVacios) {
            return ValidationResult(false, "Tienes que rellenar todos los campos")
        }
        return ValidationResult(true)
    }


    private fun createAccount(): String {
        val email = txtEmail.editText?.text.toString().lowercase()
        val password = txtPassword.editText?.text.toString()
        val password2 = txtPassword2.editText?.text.toString()
        val name = txtName.editText?.text.toString()
        val username = txtUsername.editText?.text.toString()
        val objeto = txtObjeto.editText?.text.toString()
        val namePlanificado = txtNameplanificado.editText?.text.toString()

        val notextViewsVacios =  comprobarTextViewsVacios(email, password, password2, name, username, objeto, namePlanificado, checkObjeto.isChecked, checkUserPlanificado.isChecked)

        val isAccountValid = isAccountValid(email, password, password2, notextViewsVacios)


        // TODO: ORGANIZAR ESTO
        if (isAccountValid.isValid) {
            auth.currentUser
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // El registro ha fallado
                        Log.w("Registration", "createUserWithEmail:failure", task.exception)
                        txtUsername.error = "El nombre de usuario o correo introducido ya existe"
                        txtEmail.error = "El nombre de usuario o correo introducido ya existe"
                    }else{
                        usuario.crearUsuario(name, email, username, objeto, namePlanificado, this@RegisterActivity)
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

                        val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }
        return isAccountValid.errorMessage ?: ""
    }

    private fun updateButtonIcon() {
        // Actualizar el icono del botón segun el estado
        val iconResource = if (isClicked) R.drawable.question_simple else R.drawable.svg_close
        val iconDrawable = ContextCompat.getDrawable(this, iconResource)
        botonAyuda.icon = iconDrawable
    }

}