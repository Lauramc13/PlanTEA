package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.google.android.material.textfield.TextInputEditText
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
        btnRegister = findViewById(R.id.btn_register)
        txt_name = findViewById(R.id.txt_Name)
        txt_username = findViewById(R.id.txt_UserName)
        txt_password = findViewById(R.id.txt_password)
        txt_password2 = findViewById(R.id.txt_password2)
        txt_objeto = findViewById(R.id.txt_objeto)
        txt_nameplanificado = findViewById(R.id.txt_nombreplanificado)
        checkUserPlanificado = findViewById(R.id.check_Plaificado)
        txt_nameplanificado.isEnabled = false
        checkUserPlanificado.isChecked = false


        checkUserPlanificado.setOnCheckedChangeListener { _, isChecked ->
            txt_nameplanificado.isEnabled = isChecked
        }

        btnRegister.setOnClickListener {
          //  Handler().postDelayed({
            if(txt_name.editText?.text.toString() == "" ||  txt_username.editText?.text.toString() == "" || txt_password.editText?.text.toString() == "" || txt_objeto.editText?.text.toString() == "" || txt_password2.editText?.text.toString() == "" || (checkUserPlanificado.isChecked && txt_nameplanificado.editText?.text.toString() == "")){
                Toast.makeText(applicationContext, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            }else{
                if( txt_password.editText?.text.toString() != txt_password2.editText?.text.toString() ){
                    Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
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
                        editor.putString("nombrePlanificador", txt_name.editText?.text.toString())
                        editor.putString("nombreUsuarioTEA", txt_nameplanificado.editText?.text.toString())
                        editor.putString("nombreObjeto", txt_objeto.editText?.text.toString())
                        editor.putBoolean("editPreferences", false)
                        editor.apply()
                        val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(applicationContext,  "El nombre de usuario introducido ya existe", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }

        checkUserPlanificado.setOnCheckedChangeListener { _, isChecked ->
            txt_nameplanificado.isEnabled = isChecked
        }
    }
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

}