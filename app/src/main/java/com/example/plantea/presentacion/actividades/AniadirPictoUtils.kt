package com.example.plantea.presentacion.actividades

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class AniadirPictoUtils  {
    companion object {

        private lateinit var buttonSiguiente: Button
        private lateinit var imgPicto: ImageView
        lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

        fun dialogoNuevoPicto(viewModel: CrearPlanViewModel, activity: Activity) {
            val dialogNuevoPictograma = Dialog(activity)
            dialogNuevoPictograma.setContentView(R.layout.dialogo_nuevo_pictograma)
            dialogNuevoPictograma.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogNuevoPictograma.findViewById<TextView>(R.id.lbl_NuevoPicto).text = activity.getString(R.string.lbl_NuevoPicto).uppercase()

            val imgCerrar = dialogNuevoPictograma.findViewById<ImageView>(R.id.icono_CerrarDialogo)
            val buttons = dialogNuevoPictograma.findViewById<LinearLayout>(R.id.buttons)
            val buttonArasaac = dialogNuevoPictograma.findViewById<TextView>(R.id.btn_arasaac)
            val buttonGaleria = dialogNuevoPictograma.findViewById<TextView>(R.id.btn_galeria)
            buttonSiguiente = dialogNuevoPictograma.findViewById<MaterialButton>(R.id.btn_siguiente)

            viewModel._imagenNuevoPicto.observe(activity as AppCompatActivity) {
                buttonSiguiente.isEnabled = true
            }

            val view = dialogNuevoPictograma.findViewById<ViewGroup>(R.id.fragment_nuevoPicto)
            val inflater = LayoutInflater.from(activity)
            val arasaacLayout = inflater.inflate(R.layout.fragment_nuevo_picto_busqueda, null, false)
            val chooseImageLayout = inflater.inflate(R.layout.fragment_nuevo_picto_galeria, null, false)
            val datosLayout = inflater.inflate(R.layout.fragment_nuevo_picto_nombre, null, false)
            val searchBar = arasaacLayout.findViewById<SearchView>(R.id.searchViewPicto)

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

            viewModel.adaptador = AdaptadorNuevoPicto(viewModel.pictograma.getRandomPictograms(activity, viewModel.idUsuario, Locale.getDefault().language), viewModel)
            recyclerViewBusqueda.adapter = viewModel.adaptador

            viewModel._listaPictoRandom.observe(activity) {
                viewModel.adaptador.listaPictogramas = it
                viewModel.adaptador.notifyDataSetChanged()
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

            buttonSiguiente.setOnClickListener {
                dialogoAniadirPicto(dialogNuevoPictograma, view, datosLayout, buttons, viewModel, activity)
            }

            imgPicto.setOnClickListener {
                abrirGaleria()
            }

            view.addView(arasaacLayout)

            imgCerrar.setOnClickListener {
                dialogNuevoPictograma.dismiss()
            }

            dialogNuevoPictograma.show()
        }

        fun abrirGaleria() {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        fun createPickMedia(viewModel: CrearPlanViewModel, activity: AppCompatActivity) {
            pickMedia = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                // Handle the returned URI here
                if (uri != null) {
                    imgPicto.setImageURI(uri)
                    viewModel._imagenNuevoPicto.value = uri.toString()
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

        private fun dialogoAniadirPicto(dialogNuevoPictograma: Dialog, view: ViewGroup, datosLayout: View, buttons: LinearLayout, viewModel: CrearPlanViewModel, activity: Activity) {
            view.removeAllViews()
            view.addView(datosLayout)
            val identifier = activity.resources.getIdentifier(viewModel._imagenNuevoPicto.value, "drawable", activity.packageName)
            val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
            if (identifier == 0) {
                imagenPicto.setImageURI(viewModel._imagenNuevoPicto.value?.let { Uri.parse(it) })
            } else {
                imagenPicto.setImageResource(identifier)
            }
            val categorias = viewModel.categoria.consultarCategorias(activity, Locale.getDefault().language)
            val spinnerDialogo = view.findViewById<Spinner>(R.id.spinner_Categorias)
            val nombrePictograma = view.findViewById<TextInputLayout>(R.id.txt_Titulo)

            spinnerDialogo.adapter = ArrayAdapter(activity.applicationContext, android.R.layout.simple_spinner_dropdown_item, categorias as ArrayList<String>)

            if (viewModel.subcategoriaOpen) {
                spinnerDialogo.setSelection(viewModel.identificadorSubCategoria - 1)
            } else {
                spinnerDialogo.setSelection(viewModel.identificadorCategoria - 1)
            }

            buttonSiguiente.visibility = View.GONE
            buttons.visibility = View.GONE

            val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)

            buttonGuardar.setOnClickListener {
                if (nombrePictograma.editText?.text.toString().isEmpty()) {
                    Toast.makeText(activity, R.string.toast_necesita_titulo, Toast.LENGTH_SHORT).show()
                } else if (imagenPicto.drawable == null) {
                    Toast.makeText(activity, R.string.toast_necesita_imagen, Toast.LENGTH_SHORT).show()
                } else {
                    val imagen = nombrePictograma.editText?.text.toString() //Nombre de la imagen
                    val image = (imagenPicto.drawable as BitmapDrawable).bitmap

                    val ruta = viewModel.guardarImagen(activity.applicationContext, imagen, image)
                    Toast.makeText(activity, R.string.toast_pictograma_creado, Toast.LENGTH_SHORT).show()

                    dialogNuevoPictograma.dismiss() //Cerrar dialogo

                    //Añadir pictograma
                    val id = viewModel.pictograma.nuevoPictograma(activity, nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault()), ruta, spinnerDialogo.selectedItem.toString(), viewModel.idUsuario)

                    val pictograma = Pictograma()
                    pictograma.id = id
                    pictograma.titulo = nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault())
                    pictograma.imagen = ruta
                    pictograma.categoria = spinnerDialogo.selectedItemPosition + 1
                    viewModel._listaPictogramas.value?.add(pictograma)

                    //find current fragment and update the recycler
                    val categoriasPictoFragment = (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.contenedor_fragments) as CategoriasPictogramasFragment
                    categoriasPictoFragment.recyclerPictogramas.adapter?.notifyItemInserted(viewModel._listaPictogramas.value!!.size - 1)
                    viewModel.categoria.crearCategoria(activity, nombrePictograma.editText?.text.toString().uppercase(Locale.getDefault()), ruta, 0, "default", viewModel.idUsuario)
                }
            }
        }

        fun inializeSearch(searchBar: SearchView, isNuevoPictoBusqueda: Boolean, viewModel: CrearPlanViewModel, activity: Activity) {
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
    }
}