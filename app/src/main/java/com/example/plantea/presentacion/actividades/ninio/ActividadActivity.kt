package com.example.plantea.presentacion.actividades.ninio

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import java.util.*

class ActividadActivity : AppCompatActivity() {
    lateinit var listaPictogramas: ArrayList<Pictograma>
    var plan = Planificacion()
    private var navigationHandler = NavegacionUtils()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Comprobamos la orientacion de la pantalla
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        navigationHandler.configurarDatos(this, R.id.actividades)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationHandler.destroyPopup()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        navigationHandler.inicializarVariables(this, R.id.actividades, ActividadActivity::class.java)

        val webView: WebView = findViewById(R.id.webView)
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

       // webView.loadUrl("https://www.youtube.com")

        val cardVideo : CardView = findViewById(R.id.card_video)
        val cardObjeto : CardView = findViewById(R.id.card_objeto)
        val backButton : Button = findViewById(R.id.goBackButton)
        val frameVideo : FrameLayout =  findViewById(R.id.webViewFrame)
        val closeButton : Button = findViewById(R.id.closeYoutube)

        backButton.setOnClickListener{
            finish()
        }

        closeButton.setOnClickListener {
            cardVideo.visibility = View.VISIBLE
            cardObjeto.visibility = View.VISIBLE
            frameVideo.visibility = View.INVISIBLE
            //stop the webview from playing
            webView.loadUrl("about:blank")
        }

        cardVideo.setOnClickListener{
            /*val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"))
            startActivity(intent)*/
            webView.loadUrl("https://www.youtube.com")
            cardVideo.visibility = View.INVISIBLE
            cardObjeto.visibility = View.INVISIBLE
            frameVideo.visibility = View.VISIBLE
        }

        cardObjeto.setOnClickListener{
            val dialogLogout = Dialog(this)
            dialogLogout.setContentView(R.layout.dialogo_actividad)
            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val imgObjeto : ImageView = dialogLogout.findViewById(R.id.imageObjeto)
            val txtObjeto : TextView = dialogLogout.findViewById(R.id.lbl_nombreObjeto)
            // imgAnimacion = dialogLogout.findViewById(R.id.img_animacion)
            // rotateImageWithAnimation(imgAnimacion, 260f, 6000)

            val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
            txtObjeto.text = prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())


            if (prefs.getString("imagenObjeto", "") === "") {
                imgObjeto.setBackgroundResource(R.drawable.question2)
                txtObjeto.text = getString(R.string.noConfigurationActivity)
            } else {
                imgObjeto.background = null
                imgObjeto.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
            }

            val iconoCerrarLogin :ImageView = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
            iconoCerrarLogin.setOnClickListener { dialogLogout.dismiss() }
            dialogLogout.show()
        }
    }
}