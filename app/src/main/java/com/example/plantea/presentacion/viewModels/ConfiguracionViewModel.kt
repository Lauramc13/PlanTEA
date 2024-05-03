package com.example.plantea.presentacion.viewModels

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.example.plantea.R

class ConfiguracionViewModel: ViewModel() {

    val _toast = SingleLiveEvent<Int>()

    //var email = ""
    var name = ""
    var username = ""
    var nameTEA = ""
    var nameObj = ""

    fun comprobarCampos(txtPlanificadorText: String, txtUsernameText: String, txtUsuarioTEAText: String, txtObjetoText: String, imgPlanificador: Drawable?, imgUserTEA: Drawable?, imageObjeto: Drawable?, infoUserTEA: Boolean, infoObjeto: Boolean): Boolean {
        if (txtPlanificadorText.isEmpty() || txtUsernameText.isEmpty() || txtUsuarioTEAText.isEmpty() && infoUserTEA) {
            _toast.value = R.string.toast_necesita_nombre
            return false
        }

        if (imgPlanificador == null || (imgUserTEA == null && infoUserTEA)) {
            _toast.value = R.string.toast_necesita_imagen_usuario
            return false
        }

        if ((txtObjetoText.isEmpty() || imageObjeto == null) && infoObjeto) {
            _toast.value = R.string.toast_necesita_objeto
            return false
        }
        return true
    }
}