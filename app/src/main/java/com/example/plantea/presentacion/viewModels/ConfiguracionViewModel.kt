package com.example.plantea.presentacion.viewModels

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.adaptadores.UserAdapter

class ConfiguracionViewModel: ViewModel() {

    val _toast = SingleLiveEvent<Int>()

    //var email = ""
    var name = ""
    var username = ""
    var nameTEA = ""
    var nameObj = ""

    var userSelectPicto = -1

    var adapterUsers: UserAdapter? = null
    var usersTEA : ArrayList<Usuario>? = null

    fun comprobarCampos(txtPlanificadorText: String, txtUsernameText: String, imgPlanificador: Drawable?): Boolean {
        if (txtPlanificadorText.isEmpty() || txtUsernameText.isEmpty()) {
            _toast.value = R.string.toast_necesita_nombre
            return false
        }

        return true
    }

    fun getPenultimatePosition(listaSize: Int): Int {
        return if (listaSize < 3) {
            listaSize - 1
        } else {
            listaSize - 2
        }

    }
}