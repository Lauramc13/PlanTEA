package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.viewModels.MenuAvataresViewModel

class MenuObjetosActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var btnGaleria: Button
    private val viewModel by viewModels<MenuAvataresViewModel>()
    private var isConfiguration = false
    private var uri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_objetos)
        setAvatarOnClickListeners(listOf("bici", "comida", "futbol","juegos", "libros", "movil", "musica", "recompensa"))
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
                editor.putString("imagenObjeto", uri.toString())
                editor.apply()
            }
            next()
        }

        observers()
    }

    fun observers() {
        viewModel._ruta.observe(this) {
            val editor = prefs.edit()
            editor.putString("imagenObjeto", it)
            editor.apply()
            viewModel.bitmap?.let { it1 -> CommonUtils.guardarImagen(applicationContext, it, it1) }
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
                uri = Uri.parse("android.resource://$packageName/$drawableId")
                if(!isConfiguration) {
                    val idUsuario = prefs.getString("idUsuario", "")
                    if (idUsuario != null) {
                        val usuario = Usuario()
                        usuario.aniadirImagenObjeto(
                            uri.toString(),
                            idUsuario,
                            this@MenuObjetosActivity
                        )
                    }
                    val editor = prefs.edit()
                    editor.putString("imagenObjeto", uri.toString())
                    editor.apply()
                }
                next()
            }
        }
    }

    private fun next(){
        if(isConfiguration){
            val editor = prefs.edit()
            editor.putString("imageObjetoConfig", uri.toString())
            editor.apply()
            finish()
        }else{
            val intent = Intent(applicationContext, TutorialActivity::class.java)
            intent.putExtra("isFromManual", false)
            startActivity(intent)
            finish()
        }
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModel.bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel._ruta.value =  CommonUtils.crearRuta(this, viewModel.bitmap!!, "Objeto")
            } else {
                CommonUtils.showSnackbar(findViewById(android.R.id.content),this, "No se ha seleccionado ninguna imagen")
            }
        }
    }
}