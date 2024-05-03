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


class MenuAvataresPlanActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var btnGaleria: Button
    val usuario = Usuario()
    private val viewModel by viewModels<MenuAvataresViewModel>()
    private var isConfiguration = false
    private var uri : Uri? = null

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
        val extras = intent.extras
        isConfiguration = extras?.getBoolean("editPreferences") ?: false

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
                editor.putString("imagenPlanificador", uri.toString())
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
                viewModel._ruta.value = CommonUtils.crearRuta(this, viewModel.bitmap!!, "Planificador")
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun observers(){
        viewModel._ruta.observe(this){
            val editor = prefs.edit()
            editor.putString("imagenPlanificador", it)
            editor.apply()
            val idUsuario = prefs.getString("idUsuario", "")
            if (idUsuario != null) {
                usuario.aniadirImagenPlanificador(it.toString(), idUsuario, this@MenuAvataresPlanActivity)
            }
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
                if(!isConfiguration){
                    val idUsuario = prefs.getString("idUsuario", "")
                    if (idUsuario != null) {
                        val usuario = Usuario()
                        usuario.aniadirImagenPlanificador(uri.toString(), idUsuario, this@MenuAvataresPlanActivity)
                    }

                    val editor = prefs.edit()
                    editor.putString("imagenPlanificador", uri.toString())
                    editor.apply()
                }
                next()
            }
        }
    }

    private fun next(){
        if(isConfiguration){
            val editor = prefs.edit()
            editor.putString("imagenPlanificadorConfig", uri.toString())
            editor.apply()
            finish()
        }else{
            val nextActivity = viewModel.determineNextScreenPlan(prefs)
            val intent = Intent(applicationContext, nextActivity)
            if(nextActivity == TutorialActivity::class.java){
                intent.putExtra("isFromManual", false)
            }
            startActivity(intent)
            finish()
        }
    }


}