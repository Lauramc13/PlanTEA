package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.actividades.RegisterActivity
import com.example.plantea.presentacion.viewModels.PreLoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class LoginFragment: BottomSheetDialogFragment() {

    lateinit var actividad: Activity
    private  var btnLogin: MaterialButton? = null
    private  var btnRegister: TextView? = null
    private  var email: TextInputLayout? = null
    private  var password: TextInputLayout? = null
    private  var signin : MaterialButton? = null
    private lateinit var prefs : SharedPreferences

    private val viewModel by viewModels<PreLoginViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_login, container, false)
        prefs = actividad.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)

        signin = vista.findViewById(R.id.Signin)
        email = vista.findViewById(R.id.txt_Email)
        password = vista.findViewById(R.id.txt_Password)
        btnLogin = vista.findViewById(R.id.btn_login)
        btnRegister = vista.findViewById(R.id.btn_registrar)
        btnRegister?.paintFlags = btnRegister?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)!!

        viewModel.initGoogleSignInClient(requireContext())

        signin?.setOnClickListener {
            signInGoogle()
        }

        btnRegister?.setOnClickListener {
            val intent = Intent(actividad.applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin?.setOnClickListener {
            email?.error = null
            password?.error = null

            val noTextViewVacios = viewModel.comprobarTextViewsVacios(email?.editText?.text.toString(), password?.editText?.text.toString())

            if (!noTextViewVacios) {
                Toast.makeText(requireContext(), R.string.toast_campos_vacios, Toast.LENGTH_SHORT).show()
            } else {
                val emailText = email?.editText?.text.toString().lowercase()
                val passwordText = password?.editText?.text.toString()
                if(viewModel.iniciarSesion(actividad, requireContext(), emailText, passwordText)) {
                    viewModel.configurarDatos(emailText, prefs, actividad)
                    startActivity(Intent(actividad.applicationContext, MainActivity::class.java))
                    actividad.finish()
                }else{
                    viewModel._errorEmail.value = "El usuario o la contraseña son incorrectos"
                    viewModel._errorPassword.value = "El usuario o la contraseña son incorrectos"
                }
            }
        }

        observers()
        return vista
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
        try{
            val signInIntent: Intent = viewModel.mGoogleSignInClient.signInIntent
            launcher.launch(signInIntent)
        } catch (e: Exception) {
            Toast.makeText(actividad, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            Log.d("pruebas", "Error al iniciar sesión con Google: ${e.message}")
        }

    }

    private var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val id = viewModel.usuario.consultarId(account?.email.toString(), actividad)
                if (id != null){
                    viewModel.configurarDatos(account?.email.toString(), prefs, actividad)
                    startActivity(Intent(actividad.applicationContext, MainActivity::class.java))
                    actividad.finish()
                }else{
                    //IR A REGISTER PERO CON ALGUNOS DATOS YA COMPLETOS
                    val intent = Intent(actividad.applicationContext, RegisterActivity::class.java)
                    intent.putExtra("EMAIL", account?.email.toString())
                    intent.putExtra("NAME", account?.givenName.toString())
                    startActivity(intent)
                    actividad.finish()
                }
            } catch (e: ApiException) {
                Log.w("FAILURE", "Google Sign-In Failed: ${e.statusCode}")
            }
        }else{
            Log.d("pruebas", "El resultado es: " + result.resultCode.toString() )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

}