package com.example.plantea.presentacion.viewModels

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.Usuario

class ConfiguracionViewModel: ViewModel() {

    val _toast = SingleLiveEvent<String>()

    var email = ""
    var name = ""
    var username = ""
    var nameTEA = ""
    var nameObj = ""

    fun comprobarCampos(txtPlanificadorText: String, txtUsernameText: String, txtUsuarioTEAText: String, txtObjetoText: String, imgPlanificador: Drawable?, imgUserTEA: Drawable?, imageObjeto: Drawable?, infoUserTEA: Boolean, infoObjeto: Boolean): Boolean {
        if (txtPlanificadorText.isEmpty() || txtUsernameText.isEmpty() || txtUsuarioTEAText.isEmpty() && infoUserTEA) {
            _toast.value = "Se necesita un nombre para cada usuario"
            return false
        }

        if (imgPlanificador == null || (imgUserTEA == null && infoUserTEA)) {
            _toast.value = "Se necesita una imagen para cada usuario"
            return false
        }

        if ((txtObjetoText.isEmpty() || imageObjeto == null) && infoObjeto) {
            _toast.value = "Se necesita una imagen y nombre del objeto tranquilizador"
            return false
        }
        return true
    }
}