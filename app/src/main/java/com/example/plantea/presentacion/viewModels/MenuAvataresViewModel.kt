package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MenuAvataresViewModel : ViewModel() {
    var idUsuario : String = ""
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var bitmap : Bitmap? = null
    var mdImageSelected = MutableLiveData<Boolean>()

    @SuppressLint("IntentReset")
    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}