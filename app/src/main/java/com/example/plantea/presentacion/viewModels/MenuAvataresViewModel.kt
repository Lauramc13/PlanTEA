package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.presentacion.actividades.ConfiguracionPictogramasActivity
import com.example.plantea.presentacion.actividades.MenuAvataresTEActivity
import com.example.plantea.presentacion.actividades.MenuObjetosActivity
import com.example.plantea.presentacion.actividades.TutorialActivity

class MenuAvataresViewModel : ViewModel() {
    var idUsuario : String = ""
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    //val _image = MutableLiveData<Uri?>()
    var _ruta : MutableLiveData<String> = SingleLiveEvent()
    var bitmap : Bitmap? = null
    var _imageSelected = MutableLiveData<Boolean>()

    @SuppressLint("IntentReset")
    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}