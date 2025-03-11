package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionPictogramas
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.viewModels.SingleLiveEvent
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class AniadirPictoUtils  {
    companion object {
        private lateinit var buttonSiguiente: MaterialButton
        private lateinit var buttonSaltarPDF: MaterialButton
        private lateinit var imgPicto: ImageView
        lateinit var inflater: LayoutInflater

        interface CustomViewModel {
            val pictograma: Pictograma
            val gPicto: GestionPictogramas
            val idUsuario: String
            var adaptadorRandomPictos: AdaptadorNuevoPicto
            var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
            var saltar: Boolean
            var isEditImage: Boolean
            var isCalendarioMensual: Boolean

            var seNuevoPicto: SingleLiveEvent<Pictograma?>
            var selistaPictoRandom: SingleLiveEvent<ArrayList<Pictograma>>
            var selistaPictogramas: SingleLiveEvent<ArrayList<Pictograma>>
            var seimageSelected: SingleLiveEvent<Bitmap>

            val onItemSelectedListener: AdaptadorNuevoPicto.OnItemSelectedListener

            fun setupTitle(textView: TextView, activity: Activity)
            fun dialogoAniadirPicto(dialog: Dialog, view: ViewGroup, activity: Activity, buttons: LinearLayout, buttonSiguiente: MaterialButton, buttonSaltarPDF: MaterialButton)
            fun abrirGaleria()

            @SuppressLint("NotifyDataSetChanged")
             fun initializeAniadirPicto(viewModel: CustomViewModel, activity: Activity, recyclerViewBusqueda: RecyclerView) {
                viewModel.seNuevoPicto.observe(activity as AppCompatActivity) {
                    buttonSiguiente.isEnabled = true
                }

                val randomPictos = viewModel.gPicto.getRandomPictograms(activity, viewModel.idUsuario, Locale.getDefault().language)

                CoroutineScope(Dispatchers.Main).launch {
                    randomPictos.forEach { pictogram ->
                        if (pictogram.idAPI != 0) {
                            pictogram.imagen = withContext(Dispatchers.IO) {
                                CommonUtils.getImagenAPI(pictogram.idAPI)
                            }
                        }
                    }
                    viewModel.adaptadorRandomPictos = AdaptadorNuevoPicto(randomPictos, onItemSelectedListener)
                    recyclerViewBusqueda.adapter = viewModel.adaptadorRandomPictos
                }

                viewModel.selistaPictoRandom.observe(activity) {
                    viewModel.adaptadorRandomPictos.listaPictogramas = it
                    viewModel.adaptadorRandomPictos.notifyDataSetChanged()
                }
            }

            fun getPictogramas(query: String, isNuevoPictoBusqueda: Boolean, activity: Activity) {
                val pictogramasBusqueda = ArrayList<Pictograma>()
                CoroutineScope(Dispatchers.IO).launch {
                    val dict = CommonUtils.getDataApi(query)

                    withContext(Dispatchers.Main) {
                        dict.keys.mapNotNull { key ->
                            dict[key]?.let { (value, id) ->
                                pictogramasBusqueda.add(crearPictoBusqueda(key, value, id, activity))
                            }
                        }
                    }

                    if (pictogramasBusqueda.isNotEmpty()) {
                        if(isNuevoPictoBusqueda){
                            selistaPictoRandom.postValue(pictogramasBusqueda)
                        } else{
                            selistaPictogramas.postValue(pictogramasBusqueda)
                        }

                    }
                }
            }

            private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int, activity: Activity): Pictograma {
                val tituloMayus = titulo?.uppercase()
                val favorito = gPicto.getFavorito(activity, id.toString(), idUsuario)
                return Pictograma("0", tituloMayus, bitmap, id, 0, favorito)
            }

            fun createPickMedia(viewModel: Companion.CustomViewModel, activity: AppCompatActivity) {
                pickMedia = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                    // Handle the returned URI here
                    if (uri != null) {
                        imgPicto.setImageURI(uri)
                        val picto = Pictograma()
                        picto.imagen = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
                        viewModel.seNuevoPicto.value = picto

                        if (CommonUtils.isDarkMode(activity)) {
                            imgPicto.background = ColorDrawable(Color.parseColor("#323F4B"))
                        } else {
                            imgPicto.background = ColorDrawable(Color.WHITE)
                        }

                    } else {
                        Toast.makeText(activity, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fun initializeDialog(viewModel: CustomViewModel, activity: Activity) {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.dialogo_nuevo_pictograma)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val titleDialog = dialog.findViewById<TextView>(R.id.lbl_NuevoPicto)
            val imgCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
            val buttons = dialog.findViewById<LinearLayout>(R.id.buttons)
            val buttonArasaac = dialog.findViewById<TextView>(R.id.btn_arasaac)
            val buttonGaleria = dialog.findViewById<TextView>(R.id.btn_galeria)
            buttonSiguiente = dialog.findViewById(R.id.btn_siguiente)
            buttonSaltarPDF = dialog.findViewById(R.id.btn_saltar_pdf)

            if(viewModel.isCalendarioMensual){
                buttonSaltarPDF.visibility = View.VISIBLE
            }

            val view = dialog.findViewById<ViewGroup>(R.id.fragment_nuevoPicto)
            inflater = LayoutInflater.from(activity)
            val arasaacLayout = inflater.inflate(R.layout.fragment_nuevo_picto_busqueda, null, false)
            val chooseImageLayout = inflater.inflate(R.layout.fragment_nuevo_picto_galeria, null, false)
            val searchBar = arasaacLayout.findViewById<SearchView>(R.id.searchViewPicto)
            imgPicto = chooseImageLayout.findViewById(R.id.img_galeria)

            if (searchBar.query.isEmpty()) {
                searchBar.setQuery("", false)
                searchBar.clearFocus()
            }

            val recyclerViewBusqueda = arasaacLayout.findViewById<RecyclerView>(R.id.pictogramas_busqueda)
            if (CommonUtils.isMobile(activity)) {
                recyclerViewBusqueda.layoutManager = GridLayoutManager(activity, 2)
            } else {
                recyclerViewBusqueda.layoutManager = GridLayoutManager(activity, 3)
            }

            viewModel.setupTitle(titleDialog, activity)
            viewModel.initializeAniadirPicto(viewModel, activity, recyclerViewBusqueda)

            if(viewModel.isEditImage){
                buttonSiguiente.text = activity.getString(R.string.aceptar).uppercase()
            }

            buttonSiguiente.setOnClickListener {
                if(viewModel.isEditImage){
                    viewModel.dialogoAniadirPicto(dialog, view, activity, buttons, buttonSiguiente, buttonSaltarPDF)
                    dialog.dismiss()
                }else{
                    buttons.visibility = View.GONE
                    buttonSiguiente.visibility = View.GONE
                    buttonSaltarPDF.visibility = View.GONE
                    viewModel.dialogoAniadirPicto(dialog, view, activity, buttons, buttonSiguiente, buttonSaltarPDF)
                }
            }

            buttonSaltarPDF.setOnClickListener {
                viewModel.saltar = true
                buttonSaltarPDF.visibility = View.GONE
                buttonSiguiente.visibility = View.GONE
                buttons.visibility = View.GONE
                viewModel.dialogoAniadirPicto(dialog, view, activity, buttons, buttonSiguiente, buttonSaltarPDF)
            }

            inializeSearch(searchBar, true, viewModel, activity)

            // Define animations
            val arasaacIN = TranslateAnimation(Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            val galeriaIN = TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            val galeriaOUT = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            val arasaacOUT = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)

            arasaacIN.duration = 300
            galeriaIN.duration = 300
            galeriaOUT.duration = 300
            arasaacOUT.duration = 300
            arasaacIN.interpolator = AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator)
            galeriaIN.interpolator = AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator)
            galeriaOUT.interpolator = AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator)
            arasaacOUT.interpolator = AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator)

            //Funciones de los botones
            buttonArasaac.setOnClickListener {
                buttonArasaac.isEnabled = false
                buttonGaleria.isEnabled = true
                arasaacLayout.startAnimation(arasaacIN)
                chooseImageLayout.startAnimation(galeriaOUT)
                view.addView(arasaacLayout)
                view.removeView(chooseImageLayout)
            }

            buttonGaleria.setOnClickListener {
                buttonArasaac.isEnabled = true
                buttonGaleria.isEnabled = false
                chooseImageLayout.startAnimation(galeriaIN)
                arasaacLayout.startAnimation(arasaacOUT)
                view.addView(chooseImageLayout)
                view.removeView(arasaacLayout)
            }

            imgPicto.setOnClickListener {
                viewModel.abrirGaleria()
            }

            imgCerrar.setOnClickListener {
                dialog.dismiss()
            }

            view.addView(arasaacLayout)
            dialog.show()
        }

        private fun inializeSearch(searchBar: SearchView, isNuevoPictoBusqueda: Boolean, viewModel: CustomViewModel, activity: Activity) {
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (!CommonUtils.isNetworkAvailable(activity)) {
                        Toast.makeText(activity, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
                        searchBar.setQuery("", false)
                        searchBar.clearFocus()
                    } else {
                        viewModel.getPictogramas(query.trim(), isNuevoPictoBusqueda, activity)
                    }
                    CommonUtils.hideKeyboard(activity, searchBar)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    newText.trim()
                    return true
                }
            })
        }

        fun clearButtonsSelected(buttonMorado: MaterialButton, buttonRosa: MaterialButton, buttonVerde: MaterialButton, buttonAmarillo: MaterialButton, buttonDefault: MaterialButton, buttonAzul: MaterialButton) {
            buttonMorado.icon = null
            buttonRosa.icon = null
            buttonVerde.icon = null
            buttonAzul.icon = null
            buttonDefault.icon = null
            buttonAmarillo.icon = null
        }
    }
}