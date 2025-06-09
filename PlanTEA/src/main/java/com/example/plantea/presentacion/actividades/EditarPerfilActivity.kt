package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionUsuarios
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedByteArray
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.example.plantea.presentacion.viewModels.ConfiguracionViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class EditarPerfilActivity: AppCompatActivity() {

    lateinit var image : ShapeableImageView
    lateinit var cardImage : MaterialCardView
    lateinit var prefs: SharedPreferences
    private lateinit var txtPlanificador : TextInputLayout
    private lateinit var txtCorreoPlanificador : TextInputLayout

    private val viewModel by viewModels<ConfiguracionViewModel>()

    override fun onStop() {
        super.onStop()
        viewModel.name = txtPlanificador.editText?.text.toString()
    }

    override fun onPause() {
        super.onPause()
        prefs.edit().putString("imagenPlanificadorConfig", null).apply()
    }

    override fun onResume() {
        super.onResume()
        configurarDatos()
        getImages()
    }

    override fun finish() {
        super.finish()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_left, R.anim.slide_out_right)
        }else{
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        txtCorreoPlanificador = findViewById(R.id.txt_correoPlanificador)
        txtPlanificador = findViewById(R.id.txt_nombrePlanificador)
        txtCorreoPlanificador.editText?.setText(prefs.getString("email", "")!!.lowercase(Locale.getDefault()))
        image = findViewById(R.id.imagenUsuario)
        cardImage = findViewById(R.id.cardUsuario)

        viewModel.idUsuario = prefs.getString("idUsuario", "").toString()

        if(savedInstanceState != null){
            txtPlanificador.editText?.setText(viewModel.name)
        }else {
            txtPlanificador.editText?.setText(prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault()))
        }

        val btnPassword : Button? = findViewById(R.id.buttonContrasenia)
        btnPassword?.setOnClickListener {
            val intent = Intent(applicationContext, PasswordActivity::class.java)
            startActivity(intent)
        }

        val btnGuardar : MaterialButton? = findViewById(R.id.buttonGuardar)
        btnGuardar?.setOnClickListener {
            guardarConfiguracion()
        }

        cardImage.setOnClickListener {
            val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
            intent.putExtra("editPreferences", true)
            startActivity(intent)
        }

    }

    private fun guardarConfiguracion(){
        //Obtain values from the fields
        val nombreUsuarioPlanificador = txtPlanificador.editText?.text.toString()

        //if drawable doesnt exists, set it to null
        val isValid = viewModel.comprobarCampos(nombreUsuarioPlanificador)

        if(isValid){
            val imagenBlob = CommonUtils.bitmapToByteArray((image.drawable as BitmapDrawable).bitmap)

            //Cambiamos el valor en preferencias para no acceder a configuracion en el siguiente inicio y guardamos datos de los usuarios
            val editor = prefs.edit()
            editor.putString("nombrePlanificador", nombreUsuarioPlanificador)
            editor.putString("imagenPlanificador", imagenBlob.toPreservedString)
            editor.putString("imagenPlanificadorConfig", null)
            editor.apply()

            try {
                val gUsuario = GestionUsuarios()
                gUsuario.guardarConfiguracion(nombreUsuarioPlanificador, imagenBlob, viewModel.idUsuario, this)
            }catch (e: Exception){
                Toast.makeText(this, getString(R.string.toast_error_guardar_configuracion), Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun configurarDatos(){
        if (prefs.getString("imagenPlanificador", "") == "") {
            image.setBackgroundResource(R.drawable.svg_add_image)
        } else {
            image.background = null
            image.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenPlanificador", "")?.toPreservedByteArray))
        }
    }

    private fun getImages(){
        val value = prefs.getString("imagenPlanificadorConfig", "")
        if (value != "" && value != "null") {
            image.background = null
            image.setImageBitmap(CommonUtils.byteArrayToBitmap(value?.toPreservedByteArray))
        }
    }
}
