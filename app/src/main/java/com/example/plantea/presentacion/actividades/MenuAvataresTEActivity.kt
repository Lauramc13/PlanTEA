package com.example.plantea.presentacion.actividades


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

        val btnSaltar : Button = findViewById(R.id.btn_saltar)
        if(isConfiguration){
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
        val editor = prefs.edit()
        if(isConfiguration){
                editor.putString("imageUsuarioTEAConfig", it)
                editor.apply()
        }else{
            editor.putString("imagenUsuarioTEA", it)
            editor.apply()
            val idUsuario = prefs.getString("idUsuario", "")
            if (idUsuario != null) {
                usuario.aniadirImagenPlanificado(it.toString(), idUsuario, this@MenuAvataresTEActivity)
            }
            viewModel.bitmap?.let { it1 -> CommonUtils.guardarImagen(applicationContext, it, it1) }
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
                val editor = prefs.edit()
                if(isConfiguration) {
                    editor.putString("imageUsuarioTEAConfig", uri.toString())
                    editor.apply()
                }else{
                    val idUsuario = prefs.getString("idUsuario", "")
                    if (idUsuario != null) {
                        val usuario = Usuario()
                        usuario.aniadirImagenPlanificado(uri.toString(), idUsuario, this@MenuAvataresTEActivity)
                    }
                    editor.putString("imagenUsuarioTEA", uri.toString())
                    editor.apply()
                }
                next()
            }
        }
    }

    private fun next(){
        if(!isConfiguration){
            val nextActivity = viewModel.determineNextScreenTEA(prefs)
            val intent = Intent(applicationContext, nextActivity)
            if(nextActivity == TutorialActivity::class.java){
                intent.putExtra("isFromManual", false)
            }
            startActivity(intent)
        }

        finish()
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModel.bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel._ruta.value = CommonUtils.guardarImagen(this, "UsuarioGaleria", viewModel.bitmap!!)
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

}