package com.example.plantea.presentacion.actividades

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.example.plantea.presentacion.viewModels.TraductorViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class TraductorActivity : AppCompatActivity() {
   // private lateinit var escucharButtonPalabra : Button
   // private lateinit var escucharButtonFrase : Button
    private var guardarButton : Button? = null
    private var guardarPDFButton : Button? = null
    private var guardarButtonPlan : Button? = null
    private lateinit var textoATraducir : TextInputLayout
    private lateinit var recyclerView: RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traductor)

        val traducirButton: Button = findViewById(R.id.traducirButton)
        guardarButton = findViewById(R.id.guardarButton)
        guardarPDFButton = findViewById(R.id.guardarButtonPDF)
        guardarButtonPlan = findViewById(R.id.guardarButtonPlan)

        textoATraducir = findViewById(R.id.textoTraducir)
        textoATraducir.editText?.setText(viewModel.textInputContent)
        atras = findViewById(R.id.atras)

        if(textoATraducir.editText?.text.toString().isNotEmpty()){
            textoATraducir.requestFocus()
        }

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
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
                    viewModel._visibilityButtons.value = true
                    CommonUtils.hideKeyboard(this@TraductorActivity, textoATraducir)
                }else{
                    Toast.makeText(this, R.string.toast_campo_vacio, Toast.LENGTH_SHORT).show()

                }
            }
        }

        atras?.setOnClickListener {
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
            val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.width = guardarButton!!.width

            customView.findViewById<TextView>(R.id.item_planificacion).setOnClickListener {
                if(viewModel.listaPictogramas.isNotEmpty()){
                    viewModel.dialogGuardar(this)
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

        guardarButtonPlan?.setOnClickListener {
            if(viewModel.listaPictogramas.isNotEmpty()){
                viewModel.dialogGuardar(this)
            }else{
                Toast.makeText(this, R.string.toast_error_guardar_traduccion_vacia, Toast.LENGTH_SHORT).show()
            }
        }

        guardarPDFButton?.setOnClickListener {
            if(viewModel.listaPictogramas.isNotEmpty()){
                viewModel.dialogoTraduccion(this)
            }else{
                Toast.makeText(this, R.string.toast_error_guardar_traduccion_vacia, Toast.LENGTH_SHORT).show()
            }
        }

        /////////////  Observers  //////////////

        //Cuando se actualiza la lista de pictogramas, actualizamos el adaptador
        viewModel._listaPictogramasTraduccion.observe(this) { listaPictogramas ->
            viewModel.adaptador = AdaptadorPictogramasTraductor(listaPictogramas, viewModel)
            recyclerView.adapter = viewModel.adaptador
        }

        //Si hay un mensaje de error o de éxito, lo mostramos a traves de un Snackbar
        viewModel._dialogMessage.observe(this){ message ->
            Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
        }

        //Si hay pictogramas, mostramos los botones
        viewModel._visibilityButtons.observe(this) { visibility ->
            if(visibility){
                if(isPlanificador){
                    guardarButton?.visibility = View.VISIBLE
                    guardarPDFButton?.visibility = View.VISIBLE
                    guardarButtonPlan?.visibility = View.VISIBLE
                }
            }else{
                guardarButton?.visibility = View.GONE
                guardarPDFButton?.visibility = View.GONE
                guardarButtonPlan?.visibility = View.GONE
            }
        }

        viewModel._traduccionEnded.observe(this) {
            traducirButton.isEnabled = true
        }
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModel.bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.imageSelected()
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

}