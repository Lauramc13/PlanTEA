package com.example.plantea.presentacion.actividades

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MenuAvataresPlanActivity : AppCompatActivity() {
    private lateinit var btn_galeria: Button
    private var imagenSeleccionada : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_avatares)
        setAvatarOnClickListeners(listOf("avatar1chica", "avatar2chica", "avatar3chica","avatar4chica", "avatar5chica", "avatar1chico", "avatar2chico", "avatar3chico", "avatar4chico", "avatar5chico"))

        btn_galeria = findViewById(R.id.btn_galeria)
        btn_galeria.setOnClickListener{
            abrirGaleria() //TODO ARREGLAR EL CODIGO UN POQUITO MEJOR
            if(imagenSeleccionada){
                next()
            }else{
                Toast.makeText(this, "No se ha seleccionado ningun avatar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAvatarOnClickListeners(avatarIds: List<String>) {
        avatarIds.forEach { avatarId ->
            val resources = applicationContext.resources
            val packageName = applicationContext.packageName
            val cardViewId = resources.getIdentifier(avatarId, "id", packageName)
            val avatar = findViewById<CardView>(cardViewId)
            avatar.setOnClickListener {
                val resources = applicationContext.resources
                val drawableId = resources.getIdentifier(avatarId, "drawable", packageName)
                val uri = Uri.parse("android.resource://" + packageName + "/" + drawableId)
                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("imagenPlanificador", uri.toString())
                editor.commit()
                next()

            }
        }
    }

    private fun next(){
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        if(prefs.getBoolean("editPreferences", false) === true){
            val intent = Intent(applicationContext, ConfiguracionActivity::class.java)
            startActivity(intent)
        }else{
            if (prefs.getBoolean("info_usuario", false) === false){
                val intent = Intent(applicationContext, MenuObjetosActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(applicationContext, MenuAvataresTEActivity::class.java)
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
        // Handle the returned URI here
        if (uri != null) {
            // Load the selected image from the URI
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Save the image to rutaUsuarioTEA
            val rutaUsuarioTEA = getPathFromUri(this, uri)
            val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("imagenPlanificador", rutaUsuarioTEA)
            editor.commit()
            guardarImagen(applicationContext, rutaUsuarioTEA, bitmap)
            imagenSeleccionada = true

        } else {
            Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show()
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
        val fos: FileOutputStream?
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