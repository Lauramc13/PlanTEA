package com.example.plantea.presentacion.viewModels

import android.content.Context
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import com.example.plantea.R

class TutorialViewModel: ViewModel() {

    fun updateButtonText(context: Context, currentItem: Int) : String{
        val string = if (currentItem == 3) {
            getString(context, R.string.str_finalizar)
        } else {
            getString(context, R.string.str_siguiente)
        }
        return string
    }

}