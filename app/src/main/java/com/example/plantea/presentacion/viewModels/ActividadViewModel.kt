package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.presentacion.actividades.MenuObjetosActivity
import com.example.plantea.presentacion.adaptadores.ActividadAdapter
import com.example.plantea.presentacion.adaptadores.AdaptadorActividadesPantalla
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class ActividadViewModel: ViewModel(){

    var listaActividades: ArrayList<Actividad>? = null
    lateinit var adapter : AdaptadorActividadesPantalla


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
        val dialogo = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_actividad)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgObjeto : ImageView = dialogo.findViewById(R.id.imageObjeto)
        val txtObjeto : TextView = dialogo.findViewById(R.id.lbl_nombreObjeto)

        imgObjeto.background = null
        imgObjeto.setImageURI(Uri.parse(listaActividades?.get(position)?.imagen))
        txtObjeto.text = listaActividades?.get(position)?.name?.toUpperCase(Locale.ROOT)

        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)
        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

}