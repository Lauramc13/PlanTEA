package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador

class RegisterActivity : AppCompatActivity(){

    private lateinit var btnRegister: Button
    private lateinit var txt_name : EditText
    private lateinit var txt_username : EditText
    private lateinit var txt_password : EditText
    private lateinit var txt_password2 : EditText
    private lateinit var txt_nameplanificado : EditText
    private lateinit var checkUserPlanificado : Switch
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
        txt_nameplanificado = findViewById(R.id.txt_nombreplanificado)
        checkUserPlanificado = findViewById(R.id.check_Plaificado)
        txt_nameplanificado.isEnabled = false
        checkUserPlanificado.isChecked = false


        checkUserPlanificado.setOnCheckedChangeListener { _, isChecked ->
            txt_nameplanificado.isEnabled = isChecked
        }

        btnRegister.setOnClickListener {
          //  Handler().postDelayed({
            if(txt_name.text.toString() == "" ||  txt_username.text.toString() == "" || txt_password.text.toString() == "" || txt_password2.text.toString() == "" || (checkUserPlanificado.isChecked && txt_nameplanificado.text.toString() == "")){
                Toast.makeText(applicationContext, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            }else{
                if( txt_password.text.toString() != txt_password2.text.toString() ){
                    Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                }else{
                    creado = usuario.crearUsuario(txt_name.text.toString(), txt_username.text.toString(), txt_password.text.toString(), this@RegisterActivity)
                    if (creado) {
                        Toast.makeText(applicationContext,  "Cuenta creada con éxito", Toast.LENGTH_LONG).show()
                        val editor = prefs.edit()
                        editor.putBoolean("userAccount", true)
                        editor.putString("nombrePlanificador", txt_name.text.toString())
                        editor.putString("username", txt_username.text.toString())
                        editor.putString("password", txt_password.text.toString()) //TODO GUARDAR LA CONTRASEÑA ENCRIPTADA
                        editor.putString("nombreUsuarioTEA", txt_nameplanificado.text.toString())
                        editor.putBoolean("info_usuario", checkUserPlanificado.isChecked)
                        editor.commit()
                        val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        checkUserPlanificado.setOnCheckedChangeListener { _, isChecked ->
            txt_nameplanificado.isEnabled = isChecked
        }
    }


}