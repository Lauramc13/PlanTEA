package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.SharedPreferences
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
import com.example.plantea.presentacion.viewModels.MenuAvataresViewModel
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.google.android.material.button.MaterialButton


class MenuAvataresPlanActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var btnGaleria: Button
    val usuario = Usuario()
    private val viewModel by viewModels<MenuAvataresViewModel>()
    private var isConfiguration = false

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

        val btnSaltar : MaterialButton = findViewById(R.id.btn_saltar)
        val extras = intent.extras
        isConfiguration = extras?.getBoolean("editPreferences") ?: false

        if(isConfiguration){
            btnSaltar.text = getString(R.string.str_cancelar)
        }else{
            btnSaltar.text = getString(R.string.str_saltar)
        }

        btnSaltar.setOnClickListener{
            next()
        }
        observers()
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

    fun observers(){
        viewModel._imageSelected.observe(this){
            val editor = prefs.edit()
            if(isConfiguration){
                editor.putString("imagenPlanificadorConfig",CommonUtils.bitmapToByteArray(viewModel.bitmap).toPreservedString)
                editor.apply()
              //  next()
            }else{
                val rutaPlanificador = CommonUtils.guardarImagen(this, "Planificador", viewModel.bitmap!!)
                editor.putString("imagenPlanificador", rutaPlanificador)
                editor.apply()
                val idUsuario = prefs.getString("idUsuario", "")
                if (idUsuario != null) {
                    val imagenBlob = CommonUtils.bitmapToByteArray(viewModel.bitmap!!)
                    usuario.aniadirImagenPlanificador(imagenBlob, idUsuario, this@MenuAvataresPlanActivity)
                }
            }
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
                val editor = prefs.edit()
                val image = ResourcesCompat.getDrawable(resources, drawableId, null) as BitmapDrawable

                if(isConfiguration){
                    editor.putString("imagenPlanificadorConfig", CommonUtils.bitmapToByteArray(image.bitmap).toPreservedString)
                    editor.apply()
                }else{
                    val idUsuario = prefs.getString("idUsuario", "")
                    if (idUsuario != null) {
                        val usuario = Usuario()
                        usuario.aniadirImagenPlanificador(CommonUtils.bitmapToByteArray(image.bitmap), idUsuario, this@MenuAvataresPlanActivity)
                    }
                    editor.putString("imagenPlanificador", CommonUtils.bitmapToByteArray(image.bitmap).toPreservedString)
                    editor.apply()
                }
                next()
            }
        }
    }

    private fun next(){
        if(!isConfiguration){
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
        }else{
            finish()
        }
    }


}