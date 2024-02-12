package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import java.util.Locale

class ActividadViewModel: ViewModel() {

    val _timerEnded = MutableLiveData<Boolean>()
    @SuppressLint("SetJavaScriptEnabled")

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

    fun dialogObjeto(context: Context){
        val dialogo = Dialog(context)
        dialogo.setContentView(R.layout.dialogo_actividad)
        dialogo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgObjeto : ImageView = dialogo.findViewById(R.id.imageObjeto)
        val txtObjeto : TextView = dialogo.findViewById(R.id.lbl_nombreObjeto)
        // imgAnimacion = dialogLogout.findViewById(R.id.img_animacion)
        // rotateImageWithAnimation(imgAnimacion, 260f, 6000)

        val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        txtObjeto.text = prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())

        if (prefs.getString("imagenObjeto", "") === "") {
            imgObjeto.setBackgroundResource(R.drawable.question2)
            txtObjeto.text = context.getString(R.string.noConfigurationActivity)
        } else {
            imgObjeto.background = null
            imgObjeto.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
        }

        val iconoCerrarLogin : ImageView = dialogo.findViewById(R.id.icono_CerrarDialogo)
        iconoCerrarLogin.setOnClickListener { dialogo.dismiss() }
        dialogo.show()
    }

    fun timerEnded(){
        _timerEnded.value = true
    }


}