package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.MenuAvataresTEActivity
import com.example.plantea.presentacion.actividades.MenuObjetosActivity
import com.example.plantea.presentacion.actividades.TutorialActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MenuAvataresViewModel : ViewModel() {
    var imagenSeleccionada : Boolean = false
    var idUsuario : String = ""
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    //val _image = MutableLiveData<Uri?>()
    var _ruta : MutableLiveData<String> = SingleLiveEvent<String>()
    var bitmap : Bitmap? = null

    fun determineNextScreenPlan(prefs: SharedPreferences): Class<out AppCompatActivity> {
        return when {
            prefs.getBoolean("info_usuario", false) -> MenuAvataresTEActivity::class.java
            prefs.getBoolean("info_objeto", false) -> MenuObjetosActivity::class.java
            else -> TutorialActivity::class.java
        }
    }

    fun determineNextScreenTEA(prefs: SharedPreferences): Class<out AppCompatActivity> {
        return when {
            prefs.getBoolean("info_usuario", false) -> TutorialActivity::class.java
            else -> MenuObjetosActivity::class.java
        }
    }

    @SuppressLint("IntentReset")
    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /*fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
        val cw = ContextWrapper(context)
        val dirImages = cw.getDir("Imagenes", AppCompatActivity.MODE_PRIVATE)
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
    }*/


}