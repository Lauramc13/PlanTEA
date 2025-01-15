package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.dominio.CategoriaActividad
import com.example.plantea.presentacion.actividades.MenuObjetosActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorActividadesPantalla
import com.example.plantea.presentacion.adaptadores.AdaptadorListaCategoriasActividad
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout

import java.util.Locale

class ActividadViewModel: ViewModel() {

    var listaActividades: ArrayList<Actividad>? = null
    lateinit var adapter : AdaptadorActividadesPantalla
    lateinit var adaptadorCategorias : AdaptadorListaCategoriasActividad
    var arrayCategorias: ArrayList<CategoriaActividad>? = null
    var idUsuario = ""
    var selectedCategoriasNueva = ArrayList<String>()
    val selectedCategories = mutableSetOf<Int>()

    var isPlanificadorLogged = false
    var _editActividad = MutableLiveData<Int>()

    val _timerEnded = MutableLiveData<Boolean>()
    @SuppressLint("SetJavaScriptEnabled")

    // Function to configure the WebView
    fun configureWebView(webView: WebView) {
        webView.settings.javaScriptEnabled = true

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }
    }

    // Function to show the dialog with the object information
    fun dialogoActividad(context: Context, position: Int){
        if(isPlanificadorLogged){
            _editActividad.value = position
        }else{
            dialogoActividadTEA(context, position)
        }
    }

    fun configureUser(prefs : android.content.SharedPreferences){
        isPlanificadorLogged = prefs.getBoolean("PlanificadorLogged", false)
        val userId = if(prefs.getString("idUsuarioTEA", "") == null || prefs.getString("idUsuarioTEA", "") == ""){
            prefs.getString("idUsuario", "")
        } else{
            prefs.getString("idUsuarioTEA", "")
        }
        idUsuario = userId.toString()
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

    private fun dialogoActividadTEA(context: Context, position: Int){
        val dialogo = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_actividad)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgObjeto : ImageView = dialogo.findViewById(R.id.imageObjeto)
        val txtObjeto : TextView = dialogo.findViewById(R.id.lbl_nombreObjeto)
        val chipGroup : ChipGroup = dialogo.findViewById(R.id.chipGroup)

        if(arrayCategorias!!.size == 1){
            dialogo.findViewById<TextView>(R.id.lbl_categoria).visibility = View.GONE
            chipGroup.visibility = View.GONE
        }else{
            for (i in arrayCategorias!!.indices) {
                chipGroup.addView(createTagChip(context, arrayCategorias!![i].name!!, arrayCategorias!![i].id!!.toInt()))
            }
        }

        imgObjeto.background = null
        imgObjeto.setImageBitmap(listaActividades?.get(position)?.imagen)
        txtObjeto.text = listaActividades?.get(position)?.name?.toUpperCase(Locale.ROOT)

        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)
        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

}