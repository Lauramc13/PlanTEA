package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.viewModels.MenuAvataresViewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MenuAvataresPlanActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var btnGaleria: Button
    private val viewModel by viewModels<MenuAvataresViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_avatares)
        setAvatarOnClickListeners(listOf("avatar1chica", "avatar2chica", "avatar3chica","avatar4chica", "avatar5chica", "avatar1chico", "avatar2chico", "avatar3chico", "avatar4chico", "avatar5chico"))
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        createPickMedia()

        viewModel.idUsuario = prefs.getString("idUsuario", "").toString()

        btnGaleria = findViewById(R.id.btn_galeria)
        btnGaleria.setOnClickListener{
            viewModel.abrirGaleria()
        }

        val btnSaltar : Button = findViewById(R.id.btn_saltar)
        if(prefs.getBoolean("editPreferences", false)){
            btnSaltar.text = getString(R.string.str_cancelar)
        }else{
            btnSaltar.text = getString(R.string.str_saltar)
        }

        btnSaltar.setOnClickListener{
            if(!prefs.getBoolean("editPreferences", false)) {
                val drawableId = resources.getIdentifier("svg_user", "drawable", packageName)
                val uri = Uri.parse("android.resource://$packageName/$drawableId")
                val editor = prefs.edit()
                editor.putString("imagenPlanificador", uri.toString())
                editor.putString("imagenPlanificadorDraw", drawableId.toString())
                editor.apply()
            }
            next()
        }

        observers()
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                viewModel.imagenSeleccionada = true
                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModel.bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel._ruta.value = CommonUtils.getPathFromUri(this, uri)
            } else {
                Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun observers(){
        viewModel._ruta.observe(this){
            val editor = prefs.edit()
            editor.putString("imagenPlanificador", it)
            editor.apply()
            viewModel.bitmap?.let { it1 -> viewModel.guardarImagen(applicationContext, it, it1) }
            viewModel.imagenSeleccionada = true
            next()
        }
    }

    private fun setAvatarOnClickListeners(avatarIds: List<String>) {
        avatarIds.forEach { avatarId ->
            val resources = applicationContext.resources
            val packageName = applicationContext.packageName
            val cardViewId = resources.getIdentifier(avatarId, "id", packageName)
            val avatar = findViewById<CardView>(cardViewId)
            val drawableId = resources.getIdentifier(avatarId, "drawable", packageName)

            avatar.setOnClickListener {
                val uri = Uri.parse("android.resource://$packageName/$drawableId")
                val idUsuario = prefs.getString("idUsuario", "")
                if (idUsuario != null) {
                    val usuario = Usuario()
                    usuario.aniadirImagenPlanificador(uri.toString(), idUsuario, this@MenuAvataresPlanActivity)
                }
                val editor = prefs.edit()
                editor.putString("imagenPlanificador", uri.toString())
                editor.putString("imagenPlanificadorDraw", avatarId)
                editor.apply()
                next()
            }
        }
    }

    private fun next(){
        if(prefs.getBoolean("editPreferences", false)){
            finish()
        }else{
            val nextActivity = viewModel.determineNextScreenPlan(prefs)
            val intent = Intent(applicationContext, nextActivity)
            startActivity(intent)
            finish()
        }
    }


}