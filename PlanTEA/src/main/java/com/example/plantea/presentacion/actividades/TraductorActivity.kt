package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.example.plantea.presentacion.viewModels.TraductorViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class TraductorActivity : AppCompatActivity() {
    private var guardarButton : Button? = null
    private lateinit var textoATraducir : TextInputLayout
    private lateinit var recyclerView: RecyclerView
    private var textInputContent: String = ""
    private var atras : Button? = null

    private val viewModel by viewModels<TraductorViewModel>()

    override fun onStop() {
        super.onStop()
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.textInputContent = textoATraducir.editText?.text.toString()
    }

    override fun onStart() {
        super.onStart()
        CommonUtils.loadLemmatizer(Locale.getDefault().language.lowercase(), this)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traductor)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !prefs.getBoolean("darkMode", false)

        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.data
                data?.let {
                    val inputStream = contentResolver.openInputStream(it)
                    val text = inputStream?.bufferedReader().use { it?.readText() }
                    textInputContent = text ?: ""
                    textoATraducir.editText?.setText(textInputContent)
                    textoATraducir.editText?.setSelection(textInputContent.length)
                }
            }
        }

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val traducirButton: MaterialButton = findViewById(R.id.traducirButton)
        val subir : MaterialButton = findViewById(R.id.subir)
        guardarButton = findViewById(R.id.guardarButton)

        textoATraducir = findViewById(R.id.textoTraducir)
        textoATraducir.editText?.setText(viewModel.textInputContent)
        atras = findViewById(R.id.atras)

        if(textoATraducir.editText?.text.toString().isNotEmpty()){
            textoATraducir.requestFocus()
        }

        val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)

        createPickMedia()

        recyclerView = findViewById(R.id.recycler_plan)

        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        textoATraducir.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        recyclerView.layoutManager = layoutManagerLinear

       // CommonUtils.initializeTextToSpeech(this)
       // CommonUtils.listener = this

        traducirButton.setOnClickListener {
            // if there is no internet connection, show a snackbar
            if (!CommonUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
            }else{
                if(viewModel.traducirFrase(textoATraducir.editText?.text?.trim())) {
                    traducirButton.isEnabled = false
                    viewModel.mdVisibilityButtons.value = true
                    CommonUtils.hideKeyboard(this@TraductorActivity, textoATraducir)
                }else{
                    Toast.makeText(this, R.string.toast_campo_vacio, Toast.LENGTH_SHORT).show()

                }
            }
        }

        subir.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "text/plain"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            resultLauncher.launch(intent)
        }

        atras?.setOnClickListener{
            finish()
        }

        textoATraducir.editText?.setOnKeyListener{_, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Simulate button click when "Enter" is pressed
                traducirButton.performClick()
                return@setOnKeyListener true
            }
            false
        }

        guardarButton?.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val customView = inflater.inflate(R.layout.popup_guardar_traduccion, null)
            customView.elevation = 10f
            val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.width = guardarButton!!.width

            customView.findViewById<TextView>(R.id.item_planificacion).setOnClickListener {
                if(viewModel.listaPictogramas.isNotEmpty()){
                    viewModel.dialogGuardarPlan(this)
                }else{
                    Toast.makeText(this, R.string.toast_error_guardar_traduccion_vacia, Toast.LENGTH_SHORT).show()
                }
                popupWindow.dismiss()
            }

            customView.findViewById<TextView>(R.id.item_pdf).setOnClickListener {
                if(viewModel.listaPictogramas.isNotEmpty()){
                    viewModel.dialogoTraduccion(this)
                }else{
                    Toast.makeText(this, R.string.toast_error_guardar_traduccion_vacia, Toast.LENGTH_SHORT).show()
                }
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(guardarButton, 0, -10)
        }

        /////////////  Observers  //////////////

        //Cuando se actualiza la lista de pictogramas, actualizamos el adaptador
        viewModel.mdListaPictogramasTraduccion.observe(this) { listaPictogramas ->
            viewModel.adaptador = AdaptadorPictogramasTraductor(listaPictogramas, viewModel)
            recyclerView.adapter = viewModel.adaptador
        }

        //Si hay un mensaje de error o de éxito, lo mostramos a traves de un Snackbar
        viewModel.seDialogMessage.observe(this){ message ->
            Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
        }

        //Si hay pictogramas, mostramos los botones
        viewModel.mdVisibilityButtons.observe(this) { visibility ->
            if(visibility){
                if(isPlanificador){
                    findViewById<MaterialDivider>(R.id.divider).visibility = View.VISIBLE
                    guardarButton?.visibility = View.VISIBLE
                }
            }else{
                findViewById<MaterialDivider>(R.id.divider).visibility = View.GONE
                guardarButton?.visibility = View.GONE
            }
        }

        viewModel.seTraduccionEnded.observe(this) {
            traducirButton.isEnabled = true
        }
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModel.listaPictogramas[viewModel.posicionSelected].imagen = BitmapFactory.decodeStream(inputStream)!!
                viewModel.listaPictogramas[viewModel.posicionSelected].idAPI = 0
                viewModel.adaptador.notifyItemChanged(viewModel.posicionSelected)
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }



}