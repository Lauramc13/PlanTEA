package com.example.plantea.presentacion.actividades

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MenuAvataresTEActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    private lateinit var btnGaleria: Button
    private var imagenSeleccionada : Boolean = false
    var usuario = Usuario_Planificador()
    private var firstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_avatarestea)
        setAvatarOnClickListeners(listOf("avatar1nina", "avatar2nina", "avatar3nina","avatar4nina", "avatar5nina", "avatar1nino", "avatar2nino", "avatar3nino", "avatar4nino", "avatar5nino"))
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        btnGaleria = findViewById(R.id.btn_galeria)
        btnGaleria.setOnClickListener{
            abrirGaleria()
            if(imagenSeleccionada){
                next()
            }else{
               Toast.makeText(this, "No se ha seleccionado ningun avatar", Toast.LENGTH_SHORT).show()
            }
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
                editor.putString("imagenUsuarioTEA", uri.toString())
                editor.apply()
            }
            next()
        }
    }

    private fun setAvatarOnClickListeners(avatarIds: List<String>) {
        avatarIds.forEach { avatarId ->
            val resources = applicationContext.resources
            val packageName = applicationContext.packageName
            val cardViewId = resources.getIdentifier(avatarId, "id", packageName)
            val avatar = findViewById<CardView>(cardViewId)
            avatar.setOnClickListener {
                val drawableId = resources.getIdentifier(avatarId, "drawable", packageName)
                val uri = Uri.parse("android.resource://$packageName/$drawableId")
                val username = prefs.getString("username", true.toString())
                if (username != null) {
                    usuario.aniadirImagenPlanificado(uri.toString(), username, this@MenuAvataresTEActivity)
                }
                val editor = prefs.edit()
                editor.putString("imagenUsuarioTEA", uri.toString())
                editor.apply()
                next()
            }
        }
    }

    private fun next(){
        if(prefs.getBoolean("editPreferences", false)){
            finish()
        }else{
            if(!prefs.getBoolean("info_objeto", false)){
                val intent = Intent(applicationContext, TutorialActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(applicationContext, MenuObjetosActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        // Manejar la URI devuelta aquí
        if (uri != null && firstTime) {
            firstTime = false
            // Load the selected image from the URI
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Guardar la imagen en rutaUsuarioTEA
            val rutaUsuarioTEA = getPathFromUri(this, uri)
            val editor = prefs.edit()
            editor.putString("imagenUsuarioTEA", rutaUsuarioTEA)
            editor.apply()
            guardarImagen(applicationContext, rutaUsuarioTEA, bitmap)
            imagenSeleccionada = true
            btnGaleria.performClick();

        } else {
            Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show()
            firstTime = true
        }
    }

    private fun getPathFromUri(context: Context, uri: Uri): String {
        val filePath: String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            filePath = uri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath ?: ""
    }

    private fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
        val cw = ContextWrapper(context)
        val dirImages = cw.getDir("Imagenes", MODE_PRIVATE)
        val myPath = File(dirImages, "$nombre.png")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            imagen.compress(Bitmap.CompressFormat.PNG, 10, fos) // calidad a 0 imagen mas pequeña
            fos.flush()
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return myPath.absolutePath
    }

}