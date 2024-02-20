package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.presentacion.viewModels.PreLoginViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout

class PreLoginActivity : AppCompatActivity(){
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView
   // private lateinit var btnOlvidarPass: TextView
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var background: ImageView
    private lateinit var signin : Button
    private lateinit var prefs : SharedPreferences

    private val viewModel by viewModels<PreLoginViewModel>()

    override fun onStop() {
        super.onStop()
        viewModel.email = email.editText?.text.toString()
        viewModel.password = password.editText?.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prelogin)
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        background = findViewById(R.id.imageView6)
        signin = findViewById(R.id.Signin)
        email = findViewById(R.id.txt_Email)
        password = findViewById(R.id.txt_Password)

        viewModel.initGoogleSignInClient(this)

        if(savedInstanceState != null){
            email.editText?.setText(viewModel.email)
            password.editText?.setText(viewModel.password)
        }

        signin.setOnClickListener {
            CommonUtils.showSnackbar(findViewById(android.R.id.content),this, "Iniciando sesión")
           // GoogleSignIn.getLastSignedInAccount(this)
            signInGoogle()
        }

        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_registrar)
        //btnOlvidarPass = findViewById(R.id.btn_olvidar)
        btnRegister.paintFlags = btnRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG
       //btnOlvidarPass.paintFlags = btnRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        btnLogin.setOnClickListener {
            email.error = null
            password.error = null

            val noTextViewVacios = viewModel.comprobarTextViewsVacios(email.editText?.text.toString(), password.editText?.text.toString())

            if (!noTextViewVacios) {
                CommonUtils.showSnackbar(findViewById(android.R.id.content),applicationContext, "Tienes que rellenar todos los campos")
            } else {

                val emailText = email.editText?.text.toString().lowercase()
                val passwordText = password.editText?.text.toString()
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

        btnRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

       /* btnOlvidarPass.setOnClickListener{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialogo_olvidar_password)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val correo : TextInputLayout = dialog.findViewById(R.id.txt_Email)
            val iconoCerrar : ImageView = dialog.findViewById(R.id.icono_CerrarDialogo)
            val btnEnviar : Button = dialog.findViewById(R.id.btn_enviar)

            btnEnviar.setOnClickListener{
                val email = correo.editText?.text.toString()
                if(email.isEmpty()){
                    correo.error = "No puedes dejar el campo vacío"
                }else{
                   if(viewModel.restablecerContrasesnia()){
                       CommonUtils.showSnackbar(findViewById(android.R.id.content),applicationContext, "Si el usuario existe se enviará un correo para restablecer la contraseña")
                       dialog.dismiss()
                   }else{
                       CommonUtils.showSnackbar(findViewById(android.R.id.content),applicationContext, "El correo introducido no está registrado en la aplicación")
                   }
                }
            }

            iconoCerrar.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }*/

        observers()
    }

    fun observers(){
        viewModel._errorEmail.observe(this) {
            email.error = it
        }

        viewModel._errorPassword.observe(this) {
            password.error = it
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
                    //IR A REGISTER PERO CON ALGUNOS DATOS YA COMPLETOS
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



}