package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.PlanificacionItem
import com.example.plantea.dominio.Usuario_Planificador
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PreLoginActivity : AppCompatActivity(){

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView
    private lateinit var btnOlvidarPass: TextView
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var background: ImageView
    private lateinit var signin : Button
    private lateinit var prefs : SharedPreferences
    private lateinit var auth: FirebaseAuth

    var usuario = Usuario_Planificador()
    var user = Usuario_Planificador()

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        const val EMAIL_KEY = "EMAIL_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY" 
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EMAIL_KEY, email.editText?.text.toString())
        outState.putString(PASSWORD_KEY, password.editText?.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        email.editText?.setText(savedInstanceState.getString(EMAIL_KEY).toString())
        password.editText?.setText(savedInstanceState.getString(PASSWORD_KEY).toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        setContentView(R.layout.activity_prelogin)
        background = findViewById(R.id.imageView6)
        signin = findViewById(R.id.Signin)
        auth = Firebase.auth

        FirebaseApp.initializeApp(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id_google))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        /*val database = Firebase.database
        val myref = database.getReference("/Cuaderno/kAJG6w1EDMD15TmKkKx0")
        myref.setValue("holi")*/

        //FIRESTORE CLOUD
/*
        val db = Firebase.firestore

        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )

// Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
*/
        email = findViewById(R.id.txt_Email)
        password = findViewById(R.id.txt_Password)

        signin.setOnClickListener {
            Toast.makeText(this, "Iniciando sesión", Toast.LENGTH_SHORT).show()
            val account = GoogleSignIn.getLastSignedInAccount(this)
            signInGoogle()
        }

        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            background.setImageResource(R.drawable.backgroundlogindark)
        } else {
            background.setImageResource(R.drawable.backgroundlogin)
        }

        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_registrar)
        btnOlvidarPass = findViewById(R.id.btn_olvidar)
        btnRegister.paintFlags = btnRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        btnOlvidarPass.paintFlags = btnRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        btnLogin.setOnClickListener {
            email.error = null
            password.error = null

            val emptyTextViews = mutableListOf<TextView>()

            if (email.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(email.editText!!)
                email.error = "ESTO ES UN ERROR"
            }

            if (password.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(password.editText!!)
                password.error = "ESTO ES UN ERROR"
            }

            if (emptyTextViews.isNotEmpty()) {
                Toast.makeText(applicationContext, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            } else {

                /*  val passCifrada = hashPassword(password.editText?.text.toString())
                if (usuario.comprobarUsuario(email.editText?.text.toString(), passCifrada, this@PreLoginActivity) == true) {
                     configurarDatos(email.editText?.text.toString())
                 } else {
                     email.error = "ESTO ES UN ERROR"
                     password.error = "ESTO ES UN ERROR"
                     Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_LONG).show()
                 }*/
                val emailText = email.editText?.text.toString().lowercase()
                val passwordText = password.editText?.text.toString()
                auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "signInWithEmail:success")
                            configurarDatos(emailText)
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
                            email.error = "ESTO ES UN ERROR"
                            password.error = "ESTO ES UN ERROR"
                            Toast.makeText(
                                baseContext,
                                "Las credenciales son incorrectas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnOlvidarPass.setOnClickListener{
            //dialog of dialogo_olvidar_password
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialogo_olvidar_password)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val correo : TextInputLayout = dialog.findViewById(R.id.txt_Email)
            val iconoCerrar : ImageView = dialog.findViewById(R.id.icono_CerrarDialogo)
            val btnEnviar : Button = dialog.findViewById(R.id.btn_enviar)

            //coger el correo y hacer cositas

            btnEnviar.setOnClickListener{
                val email = correo.editText?.text.toString()

                //if correo is empty -> error
                if(email.isEmpty()){
                    correo.error = "ESTO ES UN ERROR"
                    Toast.makeText(applicationContext, "No puedes dejar el campo vacío", Toast.LENGTH_LONG).show()
                }else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(this, "Si el usuario existe se enviará un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }else{
                            Toast.makeText(this, "El correo introducido no es válido", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            iconoCerrar.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

  /*
          private fun hashPassword(password: String): String {
              val bytes = password.toByteArray()
              val md = MessageDigest.getInstance("SHA-256")
              val digest = md.digest(bytes)
              return digest.fold("") { str, it -> str + "%02x".format(it) }
          }
      */

    private fun signInGoogle() {

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val id = usuario.consultarId(account?.email.toString(), this@PreLoginActivity)
                if (id != null){
                    configurarDatos(account?.email.toString())
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

    /*
    //ESTO ES PARA COMPROBAR SI YA SE HA INICIADO SESION CON GOOGLE AL ABRIR LA APLICACION
    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }*/

    private fun configurarDatos(email: String){
        user = usuario.obtenerUsuario(email, this@PreLoginActivity)
        val id = usuario.consultarId(email, this@PreLoginActivity)
        val editor = prefs.edit()
        editor.putString("idUsuario", id)
        editor.putBoolean("userAccount", true)
        editor.putString("nombrePlanificador", user.getName())
        editor.putString("nombreUsuarioPlanificador", user.getUsername())
        editor.putString("email", user.getEmail())
        editor.putString("nombreUsuarioTEA", user.getNameTEA())
        //  if(user.getPassword() == ""){
        //      editor.putBoolean("isGoogleUser", true)
        //  }
        if (user.getNameTEA() != "") {
            editor.putBoolean("info_usuario", true)
        }
        editor.putString("imagenPlanificador", user.getImagen())
        editor.putString("imagenUsuarioTEA", user.getImagenTEA())
        editor.putString("nombreObjeto", user.getObjeto())
        if(user.getObjeto() != "") {
            editor.putBoolean("info_objeto", true)
        }
        editor.putString("imagenObjeto", user.getImagenObjeto())
        editor.apply()

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}