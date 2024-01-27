package com.example.plantea.presentacion.viewModels

import android.content.Context
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import com.example.plantea.R

class TutorialViewModel: ViewModel() {

    fun updateButtonText(context: Context, currentItem: Int) : String{
        var string = ""
        if (currentItem == 2) {
            string = getString(context, R.string.str_finalizar)
        } else {
           string = getString(context, R.string.str_siguiente)
        }
        return string
    }

}