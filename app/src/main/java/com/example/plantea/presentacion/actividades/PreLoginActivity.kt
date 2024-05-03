package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.plantea.R
import com.example.plantea.presentacion.fragmentos.LoginFragment
import com.example.plantea.presentacion.viewModels.PreLoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout

class PreLoginActivity : AppCompatActivity(){
    private  var btnLogin: Button? = null
    private  var btnRegister: TextView? = null
   // private lateinit var btnOlvidarPass: TextView
    private  var email: TextInputLayout? = null
    private  var password: TextInputLayout? = null
    private  var signin : Button? = null
    private var btnComenzar : Button? = null
    private lateinit var prefs : SharedPreferences
    private val bottomSheetDialogFragment = LoginFragment()

    private val viewModel by viewModels<PreLoginViewModel>()

    override fun onStop() {
        super.onStop()
        viewModel.email = email?.editText?.text.toString()
        viewModel.password = password?.editText?.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prelogin)
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        signin = findViewById(R.id.Signin)
        email = findViewById(R.id.txt_Email)
        password = findViewById(R.id.txt_Password)
        btnComenzar = findViewById(R.id.comenzar)

        viewModel.initGoogleSignInClient(this)

        if(savedInstanceState != null){
            if(viewModel.email != "null" && viewModel.password != "null") {
                email?.editText?.setText(viewModel.email)
                password?.editText?.setText(viewModel.password)
            }
        }

        signin?.setOnClickListener {
            Toast.makeText(this, R.string.toast_iniciando_sesion, Toast.LENGTH_SHORT).show()
            signInGoogle()
        }

        btnComenzar?.setOnClickListener {
            bottomSheetDialog(this)
        }

        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_registrar)
        btnRegister?.paintFlags = btnRegister?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!

        btnLogin?.setOnClickListener {
            email?.error = null
            password?.error = null

            val noTextViewVacios = viewModel.comprobarTextViewsVacios(email?.editText?.text.toString(), password?.editText?.text.toString())

            if (!noTextViewVacios) {
                Toast.makeText(this, R.string.toast_rellenar_campos, Toast.LENGTH_SHORT).show()
            } else {
                val emailText = email?.editText?.text.toString().lowercase()
                val passwordText = password?.editText?.text.toString()
                if(viewModel.iniciarSesion(this, this, emailText, passwordText)) {
                    viewModel.configurarDatos(emailText, prefs, this)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }else{
                    viewModel._errorEmail.value = "El usuario o la contraseña son incorrectos"
                    viewModel._errorPassword.value = "El usuario o la contraseña son incorrectos"
                }
            }
        }

        btnRegister?.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        observers()
    }

    fun observers(){
        viewModel._errorEmail.observe(this) {
            email?.error = it
        }

        viewModel._errorPassword.observe(this) {
            password?.error = it
        }
    }


    private fun signInGoogle() {
        val signInIntent: Intent = viewModel.mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val id = viewModel.usuario.consultarId(account?.email.toString(), this@PreLoginActivity)
                if (id != null){
                    viewModel.configurarDatos(account?.email.toString(), prefs, this@PreLoginActivity)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }else{
                    // If the user is not registered, we will redirect him to the registration activity with the email and name fields filled in
                    val intent = Intent(applicationContext, RegisterActivity::class.java)
                    intent.putExtra("EMAIL", account?.email.toString())
                    intent.putExtra("NAME", account?.givenName.toString())
                    startActivity(intent)
                    finish()
                }
            } catch (e: ApiException) {
                Log.w("FAILURE", "Google Sign-In Failed: ${e.statusCode}")
            }
        }else{
            Log.d("pruebas", "El resultado es: " + result.resultCode.toString() )
        }
    }

    private fun bottomSheetDialog(context: Context){
        bottomSheetDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        bottomSheetDialogFragment.show((context as AppCompatActivity).supportFragmentManager, bottomSheetDialogFragment.tag)
    }

}