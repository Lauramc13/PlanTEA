package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.example.plantea.presentacion.viewModels.EventosViewModel
import com.example.plantea.presentacion.viewModels.SemanaViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class AniadirPictoUtils  {
    companion object {
        lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
        private lateinit var imgPicto: ImageView
        private lateinit var buttonSiguiente: MaterialButton

        fun initializeDialog(viewModel: ViewModel, activity: Activity, isCrearCategoria: Boolean) {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.dialogo_nuevo_pictograma)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val titleDialog = dialog.findViewById<TextView>(R.id.lbl_NuevoPicto)

            val imgCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
            val buttons = dialog.findViewById<LinearLayout>(R.id.buttons)
            val buttonArasaac = dialog.findViewById<TextView>(R.id.btn_arasaac)
            val buttonGaleria = dialog.findViewById<TextView>(R.id.btn_galeria)
            buttonSiguiente = dialog.findViewById(R.id.btn_siguiente)
            //val buttonSaltar = dialog.findViewById<MaterialButton>(R.id.btn_saltar)

            val view = dialog.findViewById<ViewGroup>(R.id.fragment_nuevoPicto)
            val inflater = LayoutInflater.from(activity)
            val arasaacLayout = inflater.inflate(R.layout.fragment_nuevo_picto_busqueda, null, false)
            val chooseImageLayout = inflater.inflate(R.layout.fragment_nuevo_picto_galeria, null, false)
            val searchBar = arasaacLayout.findViewById<SearchView>(R.id.searchViewPicto)

            val datosLayoutPlan = inflater.inflate(R.layout.fragment_nuevo_picto_nombre, null, false)
            val datosLayoutCrearCategoria = inflater.inflate(R.layout.dialogo_crear_categoria_plan, null, false)
            val datosLayoutSemana = inflater.inflate(R.layout.fragment_nuevo_picto_semana, null, false)

          /*  if(viewModel is SemanaViewModel){
                titleDialog.text = activity.getString(R.string.lbl_NuevoPictoSemana).uppercase()
            }else {
                titleDialog.text = activity.getString(R.string.lbl_NuevoPicto).uppercase()
            }*/

            when(viewModel){
                is CrearPlanViewModel -> {
                    if(isCrearCategoria){
                        titleDialog.text = activity.getString(R.string.lbl_NuevoPicto)
                    }
                }
                is SemanaViewModel -> {
                    titleDialog.text = activity.getString(R.string.lbl_NuevoPictoSemana).uppercase()
                }
                is EventosViewModel -> {
                    titleDialog.text = activity.getString(R.string.lbl_imprevistoPicto).uppercase()
                }
            }

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

            /*if (viewModel is CrearPlanViewModel) {
                initializeAniadirPictoPlan(viewModel, activity, recyclerViewBusqueda)
            } else if (viewModel is SemanaViewModel) {
                initializeAniadirPictoPlan(viewModel, activity, recyclerViewBusqueda)
            }*/

            when (viewModel) {
                is CrearPlanViewModel -> initializeAniadirPictoPlan(viewModel, activity, recyclerViewBusqueda)
                is SemanaViewModel -> initializeAniadirPictoPlan(viewModel, activity, recyclerViewBusqueda)
                is EventosViewModel -> initializeAniadirPictoPlan(viewModel, activity, recyclerViewBusqueda)
                else -> throw IllegalArgumentException("Unsupported ViewModel type")
            }

            buttonSiguiente.setOnClickListener {
                if(viewModel is CrearPlanViewModel){
                    if (isCrearCategoria) {
                        dialogoAniadirCategoriaPlan(dialog, view, datosLayoutCrearCategoria, buttons, viewModel, activity)
                        titleDialog.text = activity.getString(R.string.crear_categoria)
                    } else {
                        dialogoAniadirPictoPlan(dialog, view, datosLayoutPlan, buttons, viewModel, activity)
                    }
                }else if (viewModel is SemanaViewModel){
                    dialogoAniadirPictoSemana(dialog, view, datosLayoutSemana, buttons, viewModel, activity)
                }else if (viewModel is EventosViewModel){
                    dialogoAniadirPictoEvento(dialog, view, datosLayoutSemana, buttons, viewModel, activity)
                }
            }

            inializeSearch(searchBar, true, viewModel, activity)

            imgPicto = chooseImageLayout.findViewById(R.id.img_galeria)

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
                abrirGaleria()
            }

            imgCerrar.setOnClickListener {
                dialog.dismiss()
            }

            view.addView(arasaacLayout)
            dialog.show()
        }

        fun abrirGaleria() {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        fun createPickMedia(viewModel: ViewModel, activity: AppCompatActivity) {
            pickMedia = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                    // Handle the returned URI here
                    if (uri != null) {
                        imgPicto.setImageURI(uri)
                        //uri to bitmap
                        if(viewModel is CrearPlanViewModel){
                            val picto = Pictograma()
                            picto.imagen = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
                            viewModel._nuevoPicto.value = picto
                            //viewModel._nuevoPicto.value?.imagen = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
                        }else if (viewModel is SemanaViewModel){
                            val picto = Pictograma()
                            picto.imagen = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
                            viewModel._nuevoPicto.value = picto
                           // viewModel._nuevoPicto.value?.imagen = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
                        }

                        //if is dark mode
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

        private fun dialogoAniadirPictoPlan(dialogNuevoPictograma: Dialog, view: ViewGroup, datosLayout: View, buttons: LinearLayout, viewModel: CrearPlanViewModel, activity: Activity) {
            view.removeAllViews()
            view.addView(datosLayout)
            val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
            imagenPicto.setImageBitmap(viewModel._nuevoPicto.value?.imagen)

            val categorias = viewModel.categoria.obtenerCategoriasPrincipales(activity, viewModel.idUsuario, Locale.getDefault().language)
            val spinnerDialogo = view.findViewById<Spinner>(R.id.spinner_Categorias)
            val nombrePictograma = view.findViewById<TextInputLayout>(R.id.txt_Titulo)

            spinnerDialogo.adapter = ArrayAdapter(activity.applicationContext, android.R.layout.simple_spinner_dropdown_item, categorias.map { it.getTitulo() })

            //find position of the category by the identificador
            spinnerDialogo.setSelection(findId(viewModel.identificadorCategoria, categorias))

            buttonSiguiente.visibility = View.GONE
            buttons.visibility = View.GONE

            val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)

            buttonGuardar.setOnClickListener {
                if (nombrePictograma.editText?.text.toString().isEmpty()) {
                    Toast.makeText(activity, R.string.toast_necesita_titulo, Toast.LENGTH_SHORT).show()
                } else if (imagenPicto.drawable == null) {
                    Toast.makeText(activity, R.string.toast_necesita_imagen, Toast.LENGTH_SHORT).show()
                } else {
                    val imageBlob = CommonUtils.bitmapToByteArray((imagenPicto.drawable as BitmapDrawable).bitmap)
                    Toast.makeText(activity, R.string.toast_pictograma_creado, Toast.LENGTH_SHORT).show()

                    dialogNuevoPictograma.dismiss()

                    //Añadir pictograma
                    var idCategoria = viewModel.categoria.obtenerCategoria(activity, spinnerDialogo.selectedItem.toString(), Locale.getDefault().language)
                    //if categoria is between 0 and 3
                    if(idCategoria in 1..4){
                        idCategoria = viewModel.categoria.duplicateCategoria(activity.applicationContext, viewModel.idUsuario, idCategoria)
                    }
                    val id = if(viewModel._nuevoPicto.value?.idAPI != 0){
                        viewModel.pictograma.nuevoPictogramaAPI(activity, nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault()), viewModel._nuevoPicto.value?.idAPI.toString(), idCategoria.toString())
                    }else{
                        viewModel.pictograma.nuevoPictogramaLocal(activity, nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault()), imageBlob, idCategoria.toString(), viewModel.idUsuario)
                    }

                    val pictograma = Pictograma()
                    pictograma.titulo = nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault())
                    pictograma.categoria = idCategoria
                    pictograma.id = id
                    pictograma.imagen = (imagenPicto.drawable as BitmapDrawable).bitmap
                    viewModel._listaPictogramas.value?.add(pictograma)

                    //find current fragment and update the recycler
                    val categoriasPictoFragment =
                        (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.contenedor_fragments) as CategoriasPictogramasFragment
                    categoriasPictoFragment.recyclerPictogramas.adapter?.notifyItemInserted(
                        viewModel._listaPictogramas.value!!.size - 1
                    )
                    categoriasPictoFragment.textoVacio.visibility = View.GONE
                }
            }
        }

        private fun dialogoAniadirPictoEvento(dialogNuevoPictograma: Dialog, view: ViewGroup, datosLayout: View, buttons: LinearLayout, viewModel: EventosViewModel, activity: Activity) {
            view.removeAllViews()
            view.addView(datosLayout)
            if(CommonUtils.isMobile(activity)){
                view.layoutParams.height = 800
                view.requestLayout()
            }

            val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
            imagenPicto.setImageBitmap(viewModel._nuevoPicto.value?.imagen)

            buttonSiguiente.visibility = View.GONE
            buttons.visibility = View.GONE

            val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)
            buttonGuardar.text = activity.getString(R.string.cambiar).uppercase()

            buttonGuardar.setOnClickListener {
                if (imagenPicto.drawable == null) {
                    Toast.makeText(activity, R.string.toast_necesita_imagen, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel._pictoChanged.value = true
                    dialogNuevoPictograma.dismiss() //Cerrar dialogo
                }
            }
        }


            private fun dialogoAniadirPictoSemana(dialogNuevoPictograma: Dialog, view: ViewGroup, datosLayout: View, buttons: LinearLayout, viewModel: SemanaViewModel, activity: Activity) {
            view.removeAllViews()
            view.addView(datosLayout)
            val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
            imagenPicto.setImageBitmap(viewModel._nuevoPicto.value?.imagen)

            buttonSiguiente.visibility = View.GONE
            buttons.visibility = View.GONE

            val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)

            buttonGuardar.setOnClickListener {
                if (imagenPicto.drawable == null) {
                    Toast.makeText(activity, R.string.toast_necesita_imagen, Toast.LENGTH_SHORT).show()
                } else {

                    viewModel._imageSelected.value = (imagenPicto.drawable as BitmapDrawable).bitmap
                    dialogNuevoPictograma.dismiss() //Cerrar dialogo

                    //Añadir pictograma

                }
            }
        }

        private fun dialogoAniadirCategoriaPlan(dialogNuevaCategoria: Dialog, view: ViewGroup, datosLayout: View, buttons: LinearLayout, viewModel: CrearPlanViewModel, activity: Activity) {
            view.removeAllViews()
            view.addView(datosLayout)
            val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
            imagenPicto.setImageBitmap(viewModel._nuevoPicto.value?.imagen)

            val nombrePictograma = view.findViewById<TextInputLayout>(R.id.txt_Titulo)
            var colorSelected = "default"

            buttonSiguiente.visibility = View.GONE
            buttons.visibility = View.GONE

            val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)

            //Colores buttons
            val buttonMorado = dialogNuevaCategoria.findViewById<FloatingActionButton>(R.id.fab1)
            val buttonRosa = dialogNuevaCategoria.findViewById<FloatingActionButton>(R.id.fab2)
            val buttonVerde = dialogNuevaCategoria.findViewById<FloatingActionButton>(R.id.fab3)
            val buttonAmarillo = dialogNuevaCategoria.findViewById<FloatingActionButton>(R.id.fab4)
            val buttonAzul = dialogNuevaCategoria.findViewById<FloatingActionButton>(R.id.fab5)
            val buttonDefault = dialogNuevaCategoria.findViewById<FloatingActionButton>(R.id.fab6)

            buttonAmarillo.setOnClickListener {
                clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
                buttonAmarillo.setImageResource(R.drawable.svg_check)
                colorSelected = "yellow"
            }

            buttonAzul.setOnClickListener {
                clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
                buttonAzul.setImageResource(R.drawable.svg_check)
                colorSelected = "blue"
            }

            buttonMorado.setOnClickListener {
                clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
                buttonMorado.setImageResource(R.drawable.svg_check)
                colorSelected = "purple"
            }

            buttonRosa.setOnClickListener {
                clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
                buttonRosa.setImageResource(R.drawable.svg_check)
                colorSelected = "pink"
            }

            buttonVerde.setOnClickListener {
                clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
                buttonVerde.setImageResource(R.drawable.svg_check)
                colorSelected = "green"
            }

            buttonDefault.setOnClickListener {
                clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
                buttonDefault.setImageResource(R.drawable.svg_check)
                colorSelected = "default"
            }

            val categoria = Categoria()
            buttonGuardar.setOnClickListener {
                if (nombrePictograma.editText?.text.toString().isEmpty()) {
                    nombrePictograma.error = activity.getString(R.string.toast_obligatorio)
                } else if (categoria.checkCategoriaExiste(view.context, nombrePictograma.editText?.text.toString(), viewModel.idUsuario, Locale.getDefault().language)) {
                    Toast.makeText(view.context, R.string.toast_categoria_existente, Toast.LENGTH_SHORT).show()
                } else {
                    val imagenBlob = CommonUtils.bitmapToByteArray((imagenPicto.drawable as BitmapDrawable).bitmap)
                    val idCategoria = categoria.crearCategoria(activity, nombrePictograma.editText?.text.toString().uppercase(), imagenBlob, colorSelected, viewModel.idUsuario)

                    val newCategoria = Categoria(idCategoria, nombrePictograma.editText?.text.toString().uppercase(), (imagenPicto.drawable as BitmapDrawable).bitmap, colorSelected)
                    viewModel.listaCategorias.add(viewModel.listaCategorias.size - 1, newCategoria)
                    viewModel._createdCategoria.value = true

                    dialogNuevaCategoria.dismiss() //Cerrar dialogo
                }
            }
        }

        fun inializeSearch(searchBar: SearchView, isNuevoPictoBusqueda: Boolean, viewModel: ViewModel, activity: Activity) {
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (!CommonUtils.isNetworkAvailable(activity)) {
                        Toast.makeText(activity, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
                        searchBar.setQuery("", false)
                        searchBar.clearFocus()
                    } else {
                        if (viewModel is CrearPlanViewModel) {
                            viewModel.getPictogramas(query.trim(), isNuevoPictoBusqueda, activity)

                        } else if (viewModel is SemanaViewModel) {
                            viewModel.getPictogramas(query.trim(), isNuevoPictoBusqueda, activity)
                        } else if (viewModel is EventosViewModel) {
                            viewModel.getPictogramas(query.trim(), activity)
                        }
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

        @SuppressLint("NotifyDataSetChanged")
        private fun initializeAniadirPictoPlan(viewModel: CrearPlanViewModel, activity: Activity, recyclerViewBusqueda: RecyclerView) {
            viewModel._nuevoPicto.observe(activity as AppCompatActivity) {
                buttonSiguiente.isEnabled = true
            }

            val randomPictos = viewModel.pictograma.getRandomPictograms(activity, viewModel.idUsuario, Locale.getDefault().language)

            CoroutineScope(Dispatchers.Main).launch {
                randomPictos.forEach { pictogram ->
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = withContext(Dispatchers.IO) {
                            CommonUtils.getImagenAPI(pictogram.idAPI)
                        }
                    }
                }
                viewModel.adaptador = AdaptadorNuevoPicto(randomPictos, viewModel)
                recyclerViewBusqueda.adapter = viewModel.adaptador
            }

            viewModel._listaPictoRandom.observe(activity) {
                viewModel.adaptador.listaPictogramas = it
                viewModel.adaptador.notifyDataSetChanged()
            }
        }

        private fun initializeAniadirPictoPlan(viewModel: SemanaViewModel, activity: Activity, recyclerViewBusqueda: RecyclerView) {
            viewModel._nuevoPicto.observe(activity as AppCompatActivity) {
                buttonSiguiente.isEnabled = true
            }

            val randomPictos = viewModel.pictograma.getRandomPictograms(activity, viewModel.idUsuario, Locale.getDefault().language)

            CoroutineScope(Dispatchers.Main).launch {
                randomPictos.forEach { pictogram ->
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = withContext(Dispatchers.IO) {
                            CommonUtils.getImagenAPI(pictogram.idAPI)
                        }
                    }
                }
                viewModel.adaptador = AdaptadorNuevoPicto(randomPictos, viewModel)
                recyclerViewBusqueda.adapter = viewModel.adaptador
            }

            viewModel._listaPictoRandom.observe(activity) {
                viewModel.adaptador.listaPictogramas = it
                viewModel.adaptador.notifyDataSetChanged()
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun initializeAniadirPictoPlan(viewModel: EventosViewModel, activity: Activity, recyclerViewBusqueda: RecyclerView) {
            viewModel._nuevoPicto.observe(activity as AppCompatActivity) {
                buttonSiguiente.isEnabled = true
            }

            val randomPictos = viewModel.pictograma.getRandomPictograms(activity, viewModel.idUsuario, Locale.getDefault().language)

            CoroutineScope(Dispatchers.Main).launch {
                randomPictos.forEach { pictogram ->
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = withContext(Dispatchers.IO) {
                            CommonUtils.getImagenAPI(pictogram.idAPI)
                        }
                    }
                }
                viewModel.adaptadorNuevoPicto = AdaptadorNuevoPicto(randomPictos, viewModel)
                recyclerViewBusqueda.adapter = viewModel.adaptadorNuevoPicto
            }

            viewModel._listaPictoRandom.observe(activity) {
                viewModel.adaptadorNuevoPicto.listaPictogramas = it
                viewModel.adaptadorNuevoPicto.notifyDataSetChanged()
            }
        }


        private fun clearButtonsSelected(buttonMorado: FloatingActionButton, buttonRosa: FloatingActionButton, buttonVerde: FloatingActionButton, buttonAmarillo: FloatingActionButton, buttonDefault: FloatingActionButton, buttonAzul: FloatingActionButton) {
            buttonMorado.setImageResource(0)
            buttonRosa.setImageResource(0)
            buttonVerde.setImageResource(0)
            buttonAzul.setImageResource(0)
            buttonDefault.setImageResource(0)
            buttonAmarillo.setImageResource(0)
        }

        private fun findId(id: Int, categorias: ArrayList<Categoria>): Int {
            var position = 0
            for (i in categorias.indices) {
                if (categorias[i].getCategoria() == id) {
                    position = i
                    break
                }
            }
            return position

        }

    }
}