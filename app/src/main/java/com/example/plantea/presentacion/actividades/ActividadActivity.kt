package com.example.plantea.presentacion.actividades

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.presentacion.viewModels.ActividadViewModel

class ActividadActivity : AppCompatActivity() {

    private val viewModel by viewModels<ActividadViewModel>()

    private lateinit var cardVideo: CardView
    private lateinit var cardObjeto: CardView
    private lateinit var frameVideo: FrameLayout
    private var atras : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        val webView: WebView = findViewById(R.id.webView)
        viewModel.configureWebView(webView)

        cardVideo = findViewById(R.id.card_video)
        cardObjeto = findViewById(R.id.card_objeto)
        frameVideo = findViewById(R.id.webViewFrame)
        atras = findViewById(R.id.atras)
        val closeButton : ImageView = findViewById(R.id.closeYoutube)

        closeButton.setOnClickListener {
            //stop the webview from playing
            webView.loadUrl("about:blank")

            cardVideo.visibility = View.VISIBLE
            cardObjeto.visibility = View.VISIBLE
            frameVideo.visibility = View.INVISIBLE
        }

        cardVideo.setOnClickListener{
            //load the video
            webView.loadUrl("https://www.youtube.com")

            cardVideo.visibility = View.INVISIBLE
            cardObjeto.visibility = View.INVISIBLE
            frameVideo.visibility = View.VISIBLE
        }

        cardObjeto.setOnClickListener{
            viewModel.dialogObjeto(this)
        }

        atras?.setOnClickListener {
            finish()
        }
    }

    fun observers(){
        viewModel._timerEnded.observe(this) {
            if (it) {
                stopVideo()
            }
        }
    }

    fun stopVideo(){
        cardVideo.visibility = View.VISIBLE
        cardObjeto.visibility = View.VISIBLE
        frameVideo.visibility = View.INVISIBLE

        cardObjeto.isEnabled = false
        cardVideo.isEnabled = false
    }
}