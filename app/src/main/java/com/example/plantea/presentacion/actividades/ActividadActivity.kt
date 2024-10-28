package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.presentacion.adaptadores.ActividadAdapter
import com.example.plantea.presentacion.adaptadores.AdaptadorActividadesPantalla
import com.example.plantea.presentacion.fragmentos.CountDownActividadFragment
import com.example.plantea.presentacion.fragmentos.CountDownFragment
import com.example.plantea.presentacion.viewModels.ActividadViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout

class ActividadActivity : AppCompatActivity(), AdaptadorActividadesPantalla.OnItemSelectedListenerActividad {

    private val viewModel by viewModels<ActividadViewModel>()

    private lateinit var cardVideo: MaterialCardView
    private lateinit var frameVideo: FrameLayout
    private lateinit var recyclerActividades : RecyclerView
    private var atras : Button? = null

    private var userID = ""

    private var imageActividad : ShapeableImageView? = null
    private var imagenUriActividad: String? = null

    private var resultLauncher :  ActivityResultLauncher<Intent>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imagenUriActividad = result.data?.extras?.get("selectedImageActividad") as String
            imageActividad?.setImageURI(Uri.parse(imagenUriActividad))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        val webView: WebView = findViewById(R.id.webView)
        viewModel.configureWebView(webView)
        cardVideo = findViewById(R.id.card_video)
        frameVideo = findViewById(R.id.webViewFrame)
        atras = findViewById(R.id.atras)
        val closeButton : ImageView = findViewById(R.id.closeYoutube)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        userID = prefs.getString("idUsuarioTEA", "")!!
        val actividad = Actividad()
        viewModel.listaActividades = actividad.getActividades(userID, this)

        if(prefs.getBoolean("PlanificadorLogged", false))
            viewModel.listaActividades?.add(Actividad("-1", "", "", userID))

        recyclerActividades = findViewById(R.id.recycler_actividades)
        recyclerActividades.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        viewModel.adapter = AdaptadorActividadesPantalla(viewModel.listaActividades, this)
        recyclerActividades.adapter = viewModel.adapter

        //frame
        val countDownTimer = CountDownActividadFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, countDownTimer).commit()

        closeButton.setOnClickListener {
            //stop the webview from playing
            webView.loadUrl("about:blank")

            cardVideo.visibility = View.VISIBLE
            frameVideo.visibility = View.INVISIBLE
        }

        cardVideo.setOnClickListener{
            //load the video
            webView.loadUrl("https://www.youtube.com")

            cardVideo.visibility = View.INVISIBLE
            frameVideo.visibility = View.VISIBLE
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

    override fun onClick(context: Context, position: Int) {
        viewModel.dialogoActividad(context, position)
    }

    override fun onClickNuevaActividad(context: Context, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialogo_bienvenida)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo).setOnClickListener {
            dialog.dismiss()
        }

        val frame = dialog.findViewById<FrameLayout>(R.id.fragment_nuevoPicto)
        frame.addView(layoutInflater.inflate(R.layout.fragment_usuario_tea, frame, false) as View)

        frame.findViewById<LinearLayout>(R.id.linearLayoutUser).visibility = View.GONE
        imageActividad = frame.findViewById(R.id.imagenActividad)
        val card = frame.findViewById<MaterialCardView>(R.id.cardActividad)
        val nombre = frame.findViewById<TextInputLayout>(R.id.txt_NameActividad)
        val guardar = frame.findViewById<MaterialButton>(R.id.btn_guardar)
        val actividad = Actividad()

        card.setOnClickListener {
            val intent = Intent(context, MenuObjetosActivity::class.java)
            intent.putExtra("editPreferences", true)
            resultLauncher!!.launch(intent)
        }


        guardar.setOnClickListener {
            if(nombre.editText?.text.toString() == ""){
                Toast.makeText(context,
                    ContextCompat.getString(context, R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val idActividad = actividad.crearActividad(nombre.editText?.text.toString(), imagenUriActividad, userID, this)

            viewModel.listaActividades?.add(viewModel.listaActividades!!.size-1, Actividad(idActividad, nombre.editText?.text.toString(), imagenUriActividad, userID))
            viewModel.adapter.notifyItemInserted(viewModel.listaActividades!!.size-2)

            dialog.dismiss()
        }

        dialog.show()
    }


    fun stopVideo(){
        cardVideo.visibility = View.VISIBLE
        frameVideo.visibility = View.INVISIBLE
        cardVideo.isEnabled = false
    }
}