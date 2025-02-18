package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.presentacion.viewModels.PasswordViewModel
import com.google.android.material.textfield.TextInputLayout

class PasswordActivity : AppCompatActivity() {
    private lateinit var viejaPass: TextInputLayout
    private lateinit var nuevaPass: TextInputLayout
    private lateinit var confirmaPass: TextInputLayout
    private lateinit var btnGuardar: Button
    private val emptyTextViews = mutableListOf<TextView>()
    private val viewModel by viewModels<PasswordViewModel>()

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        viejaPass = findViewById(R.id.txt_PassActual)
        nuevaPass = findViewById(R.id.txt_NuevaPass)
        confirmaPass = findViewById(R.id.txt_RepPass)
        btnGuardar = findViewById(R.id.btn_Guardar)

        if(savedInstanceState != null){
            viejaPass.editText?.setText(viewModel.viejaPass)
            nuevaPass.editText?.setText(viewModel.nuevaPass)
            confirmaPass.editText?.setText(viewModel.confirmaPass)
        }


        //Este método se ejecutará al seleccionar el boton guardar
        btnGuardar.setOnClickListener {
            checkTextViews()
            if (emptyTextViews.isNotEmpty()) {
                Toast.makeText(this, R.string.toast_campos_vacios, Toast.LENGTH_SHORT).show()
            } else {
                if(isValid()) {
                    val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                    val idUsuario = prefs.getString("idUsuario", "")
                    val email = prefs.getString("email", "")

                    if (idUsuario != null && email != null) {
                        if (viewModel.currentPasswordCorrect(this, applicationContext, email, viejaPass.editText?.text.toString(), idUsuario)){
                            viewModel.actualizarPassword(idUsuario, nuevaPass.editText?.text.toString(), this)
                            Toast.makeText(this, R.string.toast_contrasenia_actualizada, Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            viejaPass.error = "Contraseña incorrecta"
                            Toast.makeText(this, R.string.toast_error_actualizar_contrasenia, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun checkTextViews(){
        viejaPass.error = null
        nuevaPass.error = null
        confirmaPass.error = null
        emptyTextViews.clear()

        if (viejaPass.editText?.text.toString().isEmpty()) {
            emptyTextViews.add(viejaPass.editText!!)
            viejaPass.error = "Campo vacío"
        }

        if (nuevaPass.editText?.text.toString().isEmpty()) {
            emptyTextViews.add(nuevaPass.editText!!)
            nuevaPass.error = "Campo vacío"
        }

        if (confirmaPass.editText?.text.toString().isEmpty()) {
            emptyTextViews.add(confirmaPass.editText!!)
            confirmaPass.error = "Campo vacío"
        }
    }

    private fun isValid(): Boolean{
        var isValid = true
        if(nuevaPass.editText?.text.toString() != confirmaPass.editText?.text.toString()){
            Toast.makeText(this, R.string.toast_cotrasenias_diferentes, Toast.LENGTH_SHORT).show()

            nuevaPass.error = "Las contraseñas no coinciden"
            confirmaPass.error = "Las contraseñas no coinciden"
            isValid = false
        }

        if (nuevaPass.editText?.text?.length!! < 6) {
            nuevaPass.error = "Contraseña no válida"
            confirmaPass.error = "Contraseña no válida"

            isValid = false
            Toast.makeText(this, R.string.toast_cotrasenias_6, Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    override fun onStop() {
        super.onStop()
        viewModel.viejaPass = viejaPass.editText?.text.toString()
        viewModel.nuevaPass = nuevaPass.editText?.text.toString()
        viewModel.confirmaPass = confirmaPass.editText?.text.toString()
    }
}