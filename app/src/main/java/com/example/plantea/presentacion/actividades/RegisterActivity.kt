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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.plantea.R
import com.example.plantea.presentacion.viewModels.RegisterViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

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

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onStop() {
        super.onStop()
        textInput()
    }

    private fun textInput(){
        viewModel.name = txtName.editText?.text.toString()
        viewModel.email = txtEmail.editText?.text.toString()
        viewModel.username = txtUsername.editText?.text.toString()
        viewModel.password = txtPassword.editText?.text.toString()
        viewModel.password2 = txtPassword2.editText?.text.toString()
        viewModel.objeto = txtObjeto.editText?.text.toString()
        viewModel.namePlanificado = txtNameplanificado.editText?.text.toString()
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

        if(savedInstanceState != null){
            txtName.editText?.setText(viewModel.name)
            txtEmail.editText?.setText(viewModel.email)
            txtUsername.editText?.setText(viewModel.username)
            txtPassword.editText?.setText(viewModel.password)
            txtPassword2.editText?.setText(viewModel.password2)
            txtObjeto.editText?.setText(viewModel.objeto)
            txtNameplanificado.editText?.setText(viewModel.namePlanificado)
        }

        botonAyuda.setOnClickListener {
            val slideTransition = Slide(Gravity.END)
            slideTransition.duration = 800
            val parentView = findViewById<RelativeLayout>(R.id.relativeLayoutTooltip)
            val pathInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)
            slideTransition.interpolator = pathInterpolator
            TransitionManager.beginDelayedTransition(parentView, slideTransition)

            viewModel.isClicked = !viewModel.isClicked
            botonAyuda.icon = viewModel.updateButtonIcon(this)

            if (viewModel.isClicked) tooltipText.visibility = View.GONE else tooltipText.visibility =
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
                CommonUtils.showSnackbar(findViewById(android.R.id.content), applicationContext, errorMessage)
            }
        }

        observers()
    }


    fun observers(){
        viewModel._accountCreated.observe(this) {
            if(it){
                val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Log.w("Registration", "createUserWithEmail:failure")
                txtUsername.error = "El nombre de usuario o correo introducido ya existe"
                txtEmail.error = "El nombre de usuario o correo introducido ya existe"
            }
        }
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
        textInput()

        val notextViewsVacios =  comprobarTextViewsVacios(viewModel.username, viewModel.password, viewModel.password2, viewModel.name, viewModel.email, viewModel.objeto, viewModel.namePlanificado, checkObjeto.isChecked, checkUserPlanificado.isChecked)
        val isAccountValid = isAccountValid(viewModel.email, viewModel.password, viewModel.password2, notextViewsVacios)

        if (isAccountValid.isValid) {
            val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
            //viewModel.registerUser(checkObjeto.isChecked, checkUserPlanificado.isChecked, prefs, this)
            viewModel.accountCreated(this, prefs, checkUserPlanificado.isChecked, checkObjeto.isChecked)
        }

        return isAccountValid.errorMessage ?: ""
    }

    fun comprobarTextViewsVacios(username: String, password: String, password2: String, name: String, email: String, objeto: String, namePlanificado: String, checkedObjeto: Boolean, checkedUserTea: Boolean): Boolean {
        if (name.isEmpty()) {
            txtName.error = "ESTO ES UN ERROR"
            return false
        }

        if (email.isEmpty()) {
            txtEmail.error = "ESTO ES UN ERROR"
            return false
        }

        if (username.isEmpty()) {
            txtUsername.error = "ESTO ES UN ERROR"
            return false
        }

        if (password.isEmpty()) {
            txtPassword.error = "ESTO ES UN ERROR"
            return false
        }

        if (password2.isEmpty()) {
            txtPassword2.error = "ESTO ES UN ERROR"
            return false
        }

        if (objeto.isEmpty() && checkedObjeto) {
            txtObjeto.error = "ESTO ES UN ERROR"
            return false
        }

        if (namePlanificado.isEmpty() && checkedUserTea) {
            txtNameplanificado.error = "ESTO ES UN ERROR"
            return false
        }
        return true
    }

}