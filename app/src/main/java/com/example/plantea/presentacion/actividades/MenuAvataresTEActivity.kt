package com.example.plantea.presentacion.actividades


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
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


class MenuAvataresTEActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var btnGaleria: Button
    private val viewModel by viewModels<MenuAvataresViewModel>()
    val usuario = Usuario()
    private var isConfiguration = false
    private var isFromMain = false
    private var uri : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_avatarestea)
        setAvatarOnClickListeners(listOf("avatar1nina", "avatar2nina", "avatar3nina","avatar4nina", "avatar5nina", "avatar1nino", "avatar2nino", "avatar3nino", "avatar4nino", "avatar5nino"))
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        createPickMedia()

        btnGaleria = findViewById(R.id.btn_galeria)
        btnGaleria.setOnClickListener{
            viewModel.abrirGaleria()
        }

        val extras = intent.extras
        isConfiguration = extras?.getBoolean("editPreferences") ?: false
        isFromMain = extras?.getBoolean("isFromMain") ?: false

        val btnSaltar : Button = findViewById(R.id.btn_saltar)
        if(isConfiguration || isFromMain){
            btnSaltar.text = getString(R.string.str_cancelar)
        }else{
            btnSaltar.text = getString(R.string.str_saltar)
        }

        btnSaltar.setOnClickListener{
            if(isConfiguration) {
                uri = null
            }else{
                val drawableId = resources.getIdentifier("svg_user", "drawable", packageName)
                val uri = Uri.parse("android.resource://$packageName/$drawableId")
                val editor = prefs.edit()
                editor.putString("imagenUsuarioTEA", uri.toString())
                editor.apply()
            }
            next()
        }

        observers()

    }

    fun observers(){
        viewModel._ruta.observe(this){
        if(isConfiguration || isFromMain){
            val returnIntent = Intent()
            returnIntent.putExtra("selectedImageUsuario", uri.toString())
            setResult(RESULT_OK, returnIntent)
            finish()
        }else{
            val editor = prefs.edit()

            /*editor.putString("imagenUsuarioTEA", it)
            editor.apply()
            val idUsuario = prefs.getString("idUsuario", "")
            if (idUsuario != null) {
                usuario.aniadirImagenPlanificado(it.toString(), idUsuario, this@MenuAvataresTEActivity)
            }
            viewModel.bitmap?.let { it1 -> CommonUtils.guardarImagen(applicationContext, it, it1) }*/
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
        }
        viewModel.imagenSeleccionada = true
        next()
        }
    }

    private fun setAvatarOnClickListeners(avatarIds: List<String>) {
        avatarIds.forEach { avatarId ->
            val resources = applicationContext.resources
            val packageName = applicationContext.packageName
            val avatar = findViewById<CardView>(resources.getIdentifier(avatarId, "id", packageName))

            avatar.setOnClickListener {
                val drawableId = resources.getIdentifier(avatarId, "drawable", packageName)
                uri = Uri.parse("android.resource://$packageName/$drawableId")
                if(isConfiguration || isFromMain){
                    val returnIntent = Intent()
                    returnIntent.putExtra("selectedImageUsuario", uri.toString())
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }else{
                    val editor = prefs.edit()

                    /* val idUsuario = prefs.getString("idUsuario", "")
                     if (idUsuario != null) {
                         val usuario = Usuario()
                         usuario.aniadirImagenPlanificado(uri.toString(), idUsuario, this@MenuAvataresTEActivity)
                     }
                     editor.putString("imagenUsuarioTEA", uri.toString())
                     editor.apply()*/
                    Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
                }
                next()
            }
        }
    }

    private fun next(){
        if(!isConfiguration && !isFromMain){
            val nextActivity = viewModel.determineNextScreenTEA(prefs)
            val intent = Intent(applicationContext, nextActivity)
            startActivity(intent)
        }
        finish()
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModel.bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel._ruta.value = CommonUtils.guardarImagen(this, "UsuarioGaleria", viewModel.bitmap!!)  // TODO: CAMBIAR
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

}