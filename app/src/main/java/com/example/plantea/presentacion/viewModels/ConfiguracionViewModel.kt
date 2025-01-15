package com.example.plantea.presentacion.viewModels

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.CategoriaActividad
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.adaptadores.UserAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

class ConfiguracionViewModel: ViewModel() {

    val _toast = SingleLiveEvent<Int>()

    //var email = ""
    var name = ""
    var username = ""
    var selectedCategoriasNueva = ArrayList<String>()
    var arrayCategorias: ArrayList<CategoriaActividad>? = null
    var idUsuario = ""
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

    fun createTagChip(context: Context, chipName: String, idButton: Int): Chip {
        selectedCategoriasNueva.clear()
        val chip = Chip(context)
        val chipDrawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.materialButtonChipGroup)
        chip.setChipDrawable(chipDrawable)
        chip.text = chipName
        chip.isCheckable = true
        chip.id = idButton
        chip.setOnClickListener {
            if (selectedCategoriasNueva.contains(idButton.toString())) {
                selectedCategoriasNueva.remove(idButton.toString())
            } else {
                selectedCategoriasNueva.add(idButton.toString())
            }
        }
        return chip

    }
}