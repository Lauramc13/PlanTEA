package com.example.plantea.presentacion.actividades

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

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
                CommonUtils.showSnackbar(findViewById(android.R.id.content), applicationContext, "No puedes dejar campos vacíos")
            } else {
                if(isValid()) {
                    val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                    val idUsuario = prefs.getString("idUsuario", "")
                    val email = prefs.getString("email", "")

                    if (idUsuario != null && email != null) {
                        if (viewModel.currentPasswordCorrect(this, applicationContext, email, viejaPass.editText?.text.toString())) {
                            viewModel.actualizarPassword(idUsuario, nuevaPass.editText?.text.toString(), this)
                            CommonUtils.showSnackbar(findViewById(android.R.id.content), applicationContext, "Contraseña actualizada")
                            finish()
                        } else {
                            viejaPass.error = "Contraseña incorrecta"
                            CommonUtils.showSnackbar(findViewById(android.R.id.content), applicationContext, "Error al actualizar. Introduce de nuevo los datos. ")
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
            CommonUtils.showSnackbar(findViewById(android.R.id.content), applicationContext, "Las contraseñas no coinciden")
            nuevaPass.error = "Las contraseñas no coinciden"
            confirmaPass.error = "Las contraseñas no coinciden"
            isValid = false
        }

        if (nuevaPass.editText?.text?.length!! < 6) {
            nuevaPass.error = "Contraseña no válida"
            confirmaPass.error = "Contraseña no válida"

            isValid = false
            CommonUtils.showSnackbar(findViewById(android.R.id.content), applicationContext, "La contraseña debe tener al menos 6 caracteres")
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