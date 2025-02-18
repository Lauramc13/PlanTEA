package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.dominio.CategoriaActividad
import com.example.plantea.presentacion.adaptadores.AdaptadorActividadesPantalla
import com.example.plantea.presentacion.adaptadores.AdaptadorListaCategoriasActividad
import com.example.plantea.presentacion.fragmentos.CountDownActividadFragment
import com.example.plantea.presentacion.viewModels.ActividadViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout

/***
 * Logica de la pantalla de actividades
 *
 * @property resultLauncher Cuando se añade una nueva actividad, lo que devuelve la clase MenuObjetosActivity
 */

class ActividadActivity : AppCompatActivity(), AdaptadorActividadesPantalla.OnItemSelectedListenerActividad, AdaptadorListaCategoriasActividad.OnItemSelectedListener {

    private val viewModel by viewModels<ActividadViewModel>()

    private lateinit var cardVideo: MaterialCardView
    private lateinit var frameVideo: FrameLayout
    private lateinit var recyclerActividades : RecyclerView
    private var atras : Button? = null
    private var toggleButtons: MaterialButtonToggleGroup? = null

    private var imageActividad : ShapeableImageView? = null
    private var imagenBitmapActividad: Bitmap? = null

    private var resultLauncher :  ActivityResultLauncher<Intent>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagenUriActividad = result.data?.extras?.getByteArray("selectedImageActividad") as ByteArray
            imagenBitmapActividad = CommonUtils.byteArrayToBitmap(imagenUriActividad)
            imageActividad?.setImageBitmap(imagenBitmapActividad)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val webView: WebView = findViewById(R.id.webView)
        viewModel.configureWebView(webView)
        cardVideo = findViewById(R.id.card_video)
        frameVideo = findViewById(R.id.webViewFrame)
        atras = findViewById(R.id.atras)
        toggleButtons = findViewById(R.id.toggleButtons)
        val closeButton : ImageView = findViewById(R.id.closeYoutube)
        val btnAddCategoria : MaterialButton = findViewById(R.id.addCategoria)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        viewModel.configureUser(prefs)
        val actividad = Actividad()
        viewModel.listaActividades = actividad.getAllActividades(viewModel.idUsuario, this)
        if(viewModel.isPlanificadorLogged){
            viewModel.listaActividades?.add(Actividad("-1", getString(R.string.aniadir_actividad), null, null, viewModel.idUsuario))
        }


        if(!viewModel.isPlanificadorLogged){
            btnAddCategoria.visibility = View.GONE
        }

        recyclerActividades = findViewById(R.id.recycler_actividades)
        val numberActivities = if (CommonUtils.isMobile(this)) 2 else 3
        recyclerActividades.layoutManager = GridLayoutManager(this, numberActivities, GridLayoutManager.VERTICAL, false)
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

        botonesCategorias()

        btnAddCategoria.setOnClickListener {
            addCategoria()
        }

        observers()
    }

    fun observers(){
        viewModel.mdEditActividad.observe(this) {
            editActividad(this, it, true)
        }
    }

    private fun addCategoria(){
        val categoriaActividad = CategoriaActividad()

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_add_categoria_actividad)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val text = dialog.findViewById<TextInputLayout>(R.id.txt_nombre)
        val btnGuardar = dialog.findViewById<MaterialButton>(R.id.btn_guardar)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.categorias_recyclerview)
        viewModel.adaptadorCategorias = AdaptadorListaCategoriasActividad(viewModel.arrayCategorias, this)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = viewModel.adaptadorCategorias
        recyclerView.requestLayout()

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        btnGuardar.setOnClickListener {
            if(text.editText?.text.toString() == ""){
                Toast.makeText(this, ContextCompat.getString(this, R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idCategoria = categoriaActividad.crearCategoria(text.editText?.text.toString(), viewModel.idUsuario, this)
            val newCategoria = CategoriaActividad(idCategoria, text.editText?.text.toString(), viewModel.idUsuario)
            viewModel.arrayCategorias!!.add(newCategoria)
            addtoggleButton(newCategoria)
            viewModel.adaptadorCategorias.notifyItemInserted(viewModel.arrayCategorias!!.size-1)

            text.editText?.setText("")
            CommonUtils.hideKeyboard(this, text.editText!!)
        }

        dialog.show()
    }

    override fun onClick(context: Context, position: Int) {
        viewModel.dialogoActividad(context, position)
    }

    override fun onClickNuevaActividad(context: Context, position: Int) {
        editActividad(context, position, false)
    }

    private fun editActividad(context: Context, position: Int, isEdit: Boolean){
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
        val borrar = frame.findViewById<MaterialButton>(R.id.btn_borrar)
        val actividad = Actividad()
        val categoriaActividad = CategoriaActividad()

        card.setOnClickListener {
            val intent = Intent(context, MenuObjetosActivity::class.java)
            intent.putExtra("editPreferences", true)
            resultLauncher!!.launch(intent)
        }

        val chipGroup = frame.findViewById<ChipGroup>(R.id.chipGroup)

        viewModel.arrayCategorias = categoriaActividad.getCategorias(viewModel.idUsuario, this)
        if(viewModel.arrayCategorias!!.isEmpty()){
            chipGroup.visibility = View.GONE
            dialog.findViewById<TextView>(R.id.lbl_categoria).visibility = View.GONE
        }else{
            for (i in viewModel.arrayCategorias!!.indices) {
                chipGroup?.addView(viewModel.createTagChip(context, viewModel.arrayCategorias!![i].name!!, viewModel.arrayCategorias!![i].id!!.toInt()))
            }
        }

        if(isEdit){
            nombre.editText?.setText(viewModel.listaActividades?.get(position)?.name)
            imagenBitmapActividad = viewModel.listaActividades?.get(position)?.imagen
            imageActividad?.setImageBitmap(imagenBitmapActividad)
            guardar.text = getString(R.string.actualizar)
            borrar.visibility = View.VISIBLE

            //check chips for the selected categories
            for(i in viewModel.arrayCategorias!!.indices){
                if(viewModel.listaActividades?.get(position)?.idCategoria?.contains(viewModel.arrayCategorias!![i].id) == true){
                    chipGroup?.findViewById<Chip>(viewModel.arrayCategorias!![i].id!!.toInt())?.isChecked = true
                    viewModel.selectedCategoriasNueva.add(viewModel.arrayCategorias!![i].id!!)
                }
            }

            borrar.setOnClickListener {
                actividad.borrarActividad(viewModel.listaActividades?.get(position)?.id, this)
                viewModel.listaActividades?.removeAt(position)
                viewModel.adapter.notifyItemRemoved(position)
                dialog.dismiss()
            }

            guardar.setOnClickListener {
                if(nombre.editText?.text.toString() == ""){
                    Toast.makeText(context,
                        ContextCompat.getString(context, R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                actividad.actualizarActividad(viewModel.listaActividades?.get(position)?.id, nombre.editText?.text.toString(), CommonUtils.bitmapToByteArray(imagenBitmapActividad), this)
                val nuevasCategorias = viewModel.selectedCategoriasNueva
                val antiguasCategorias = viewModel.listaActividades?.get(position)?.idCategoria ?: emptyList()

                for (categoria in nuevasCategorias) {
                    if (!antiguasCategorias.contains(categoria)) {
                        actividad.addCategoria(viewModel.listaActividades?.get(position)?.id, categoria, this)
                        viewModel.listaActividades?.get(position)?.idCategoria?.add(categoria)
                    }
                }

                for (categoria in antiguasCategorias) {
                    if (!nuevasCategorias.contains(categoria)) {
                        actividad.removeCategoria(viewModel.listaActividades?.get(position)?.id, categoria, this)
                        viewModel.listaActividades?.get(position)?.idCategoria?.remove(categoria)
                    }
                }

                viewModel.listaActividades?.get(position)?.name = nombre.editText?.text.toString()
                viewModel.listaActividades?.get(position)?.imagen = imagenBitmapActividad
                viewModel.adapter.notifyItemChanged(position)
                dialog.dismiss()
            }

        }else{
            guardar.setOnClickListener {
                if(nombre.editText?.text.toString() == ""){
                    Toast.makeText(context,
                        ContextCompat.getString(context, R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val idActividad = actividad.crearActividad(nombre.editText?.text.toString(), CommonUtils.bitmapToByteArray(imagenBitmapActividad), viewModel.idUsuario, this)
                //add categorias
                for(i in viewModel.selectedCategoriasNueva.indices){
                    actividad.addCategoria(idActividad, viewModel.selectedCategoriasNueva[i], this)
                }
                viewModel.listaActividades?.add(viewModel.listaActividades!!.size-1, Actividad(idActividad, nombre.editText?.text.toString(), imagenBitmapActividad,  viewModel.selectedCategoriasNueva, viewModel.idUsuario))
                viewModel.adapter.notifyItemInserted(viewModel.listaActividades!!.size-2)

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun botonesCategorias(){
        val categoriaActividad = CategoriaActividad()
        viewModel.arrayCategorias = categoriaActividad.getCategorias(viewModel.idUsuario, this)
        viewModel.arrayCategorias!!.add(0, CategoriaActividad("0", getString(R.string.todas), viewModel.idUsuario))

        //Genera dinamicamente los botones de las categorias
        for (i in viewModel.arrayCategorias!!.indices) {
            addtoggleButton(viewModel.arrayCategorias!![i])
        }
        toggleButtons?.check(0)
    }

    fun stopVideo(){
        cardVideo.visibility = View.VISIBLE
        frameVideo.visibility = View.INVISIBLE
        cardVideo.isEnabled = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateActividadesList(actividades: List<Actividad>) {
        viewModel.listaActividades?.clear()
        //if its the same id
        for (element in actividades) {
            if (element.id !in viewModel.listaActividades!!.map { it.id }) {
                viewModel.listaActividades?.add(element)
            }
        }

        if(viewModel.isPlanificadorLogged){
            viewModel.listaActividades?.add(Actividad("-1", getString(R.string.aniadir_actividad), null, null, viewModel.idUsuario))
        }
        recyclerActividades.adapter?.notifyDataSetChanged()
    }

    private fun addtoggleButton(categoria: CategoriaActividad){
        val actividad = Actividad()

        val button = MaterialButton(this, null, R.attr.materialButtonToggleGroup).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            id = categoria.id!!.toInt()
            text = categoria.name!!.uppercase()
            textSize = if (CommonUtils.isMobile(this@ActividadActivity)) 12f else 14f
            isAllCaps = true
            typeface = resources.getFont(R.font.poppins_medium)
            setOnClickListener {

                if (id == 0) {
                    if (isChecked) {
                        // uncheck the ones that are in selectedCategories
                        for (id in viewModel.selectedCategories){
                            toggleButtons?.uncheck(id)
                        }

                        viewModel.selectedCategories.clear()
                        viewModel.selectedCategories.add(0)

                        val actividades = actividad.getAllActividades(viewModel.idUsuario, this@ActividadActivity)
                        updateActividadesList(actividades!!)
                    } else {
                        viewModel.selectedCategories.remove(0)
                        updateActividadesList(emptyList())
                    }
                } else {
                    if (isChecked) {
                        viewModel.selectedCategories.add(id)
                        toggleButtons?.uncheck(0)
                        viewModel.selectedCategories.remove(0)
                    } else {
                        viewModel.selectedCategories.remove(id)
                    }

                    val actividades = if (viewModel.selectedCategories.isNotEmpty()) {
                        viewModel.selectedCategories.flatMap { categoryId ->
                            actividad.getActividades(viewModel.idUsuario, categoryId.toString(), this@ActividadActivity) ?: emptyList() }
                    } else {
                        emptyList()
                    }

                    updateActividadesList(actividades)
                }
            }
        }

        toggleButtons?.addView(button)
    }

    override fun borrarCategoria(idCategoria: String?, position: Int) {
        val categoria = CategoriaActividad()
        categoria.borrarCategoria(idCategoria, this)
        viewModel.arrayCategorias?.removeAt(position)
        viewModel.adaptadorCategorias.notifyItemRemoved(position)
        toggleButtons?.removeView(toggleButtons?.findViewById(idCategoria!!.toInt()))
    }

    override fun editarCategoria(idCategoria: String?, nombre: String?, position: Int) {
        val categoria = CategoriaActividad()
        categoria.editarCategoria(idCategoria, nombre, this)
        viewModel.arrayCategorias?.get(position)?.name = nombre
        toggleButtons?.findViewById<MaterialButton>(idCategoria!!.toInt())?.text = nombre
    }

}