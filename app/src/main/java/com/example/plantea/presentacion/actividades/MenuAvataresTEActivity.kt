package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.example.plantea.presentacion.viewModels.MenuAvataresViewModel
import com.google.android.material.button.MaterialButton

class MenuAvataresTEActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var btnGaleria: Button
    private val viewModel by viewModels<MenuAvataresViewModel>()
    val usuario = Usuario()
    private var isConfiguration = false
    private var isFromMain = false

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

        val btnSaltar : MaterialButton = findViewById(R.id.btn_saltar)
        if(isConfiguration || isFromMain){
            btnSaltar.text = getString(R.string.str_cancelar)
        }else{
            btnSaltar.text = getString(R.string.str_saltar)
        }

        btnSaltar.setOnClickListener{
            finish()
        }

        observers()
    }

    fun observers(){
        viewModel._imageSelected.observe(this){
            val returnIntent = Intent()
            returnIntent.putExtra("selectedImageUsuario", CommonUtils.bitmapToByteArray(viewModel.bitmap))
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }

    private fun setAvatarOnClickListeners(avatarIds: List<String>) {
        avatarIds.forEach { avatarId ->
            val resources = applicationContext.resources
            val packageName = applicationContext.packageName
            val avatar = findViewById<CardView>(resources.getIdentifier(avatarId, "id", packageName))

            avatar.setOnClickListener {
                val drawableId = resources.getIdentifier(avatarId, "drawable", packageName)
                val image = ResourcesCompat.getDrawable(resources, drawableId, null) as BitmapDrawable
                val returnIntent = Intent()
                returnIntent.putExtra("selectedImageUsuario", CommonUtils.bitmapToByteArray(image.bitmap))
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                viewModel.bitmap = CommonUtils.uriToBitmap(this, uri)
                viewModel._imageSelected.value = true
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

}