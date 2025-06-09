package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Actividad
import com.example.plantea.dominio.gestores.GestionActividades
import com.example.plantea.dominio.gestores.GestionCategoriasActividad
import com.example.plantea.dominio.gestores.GestionUsuarios
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.adaptadores.ActividadAdapter
import com.example.plantea.presentacion.adaptadores.UserAdapter
import com.example.plantea.presentacion.viewModels.ConfiguracionViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.ArrayList

class ConfiguracionUsersTEAActivity: AppCompatActivity(), UserAdapter.OnItemSelectedListener, ActividadAdapter.OnItemSelectedListenerActividad {
    lateinit var prefs: SharedPreferences
    private val viewModel by viewModels<ConfiguracionViewModel>()
    private var imageActividad : ShapeableImageView? = null
    var image : ShapeableImageView? = null
    private var recyclerViewUsers: RecyclerView? = null
    private var adapterUsers: RecyclerView.Adapter<*>? = null


    // Para cuando se crea un usuario TEA nuevo
    private var resultLauncher :  ActivityResultLauncher<Intent>? = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                if(result.data?.extras?.getByteArray("selectedImageUsuario") != null){
                    val imagenUriUsuario = result.data?.extras?.getByteArray("selectedImageUsuario") as ByteArray
                    image?.setImageBitmap(CommonUtils.byteArrayToBitmap(imagenUriUsuario))
                    image?.background = null
                    //TODO: GUARDAR EN LA BASE DE DATOS LA FOTO
                }else {
                    val imagenUriActividad = result.data?.extras?.getByteArray("selectedImageActividad") as ByteArray
                    imageActividad?.setImageBitmap(CommonUtils.byteArrayToBitmap(imagenUriActividad))
                    imageActividad?.background = null
                    //TODO: GUARDAR EN LA BASE DE DATOS LA FOTO
                }
            }
        }catch (e: Exception){
            Log.e("Error", e.message.toString())
        }
    }

    //Cambiar la configuracion de pictogramas de un usuario TEA
    private var resultLauncherConfigPicto : ActivityResultLauncher<Intent>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val config = result.data?.extras?.getString("configPicto") as String
            val gUsuario = GestionUsuarios()
            gUsuario.cambiarConfiguracionPictogramas(config, viewModel.usersTEA!![viewModel.userSelectPicto].id, this)
            viewModel.usersTEA!![viewModel.userSelectPicto].configPictograma = config
            adapterUsers?.notifyItemChanged(viewModel.userSelectPicto)
        }
    }

    //Editar la imagen de un usuario TEA
    private var resultLauncherImagen : ActivityResultLauncher<Intent>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagen = result.data?.extras?.getByteArray("selectedImageUsuario") as ByteArray
            viewModel.usersTEA!![viewModel.userSelectPicto].imagen = CommonUtils.byteArrayToBitmap(imagen)
            adapterUsers?.notifyItemChanged(viewModel.userSelectPicto)
        }
    }

    override fun finish() {
        super.finish()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_left, R.anim.slide_out_right)
        }else{
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_users_tea)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val btnGuardar = findViewById<MaterialButton>(R.id.buttonGuardar)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.idUsuario = prefs.getString("idUsuario", "").toString()

        if(savedInstanceState == null){
            val gUsuario = GestionUsuarios()
            viewModel.usersTEA = gUsuario.obtenerUsuariosTEA(viewModel.idUsuario, this)
            if(viewModel.usersTEA!!.size <3)
                viewModel.usersTEA!!.add(Usuario())
            adapterUsers = UserAdapter(viewModel.usersTEA, this, this)
        }

        btnGuardar?.setOnClickListener {
            guardarConfiguracion()
        }

        recyclerViewUsers = findViewById(R.id.recycler)
        recyclerViewUsers?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewUsers?.adapter = adapterUsers
    }


    private fun guardarConfiguracion(){
        recyclerViewUsers?.clearFocus()
        try {
            val gUsuario = GestionUsuarios()
            if(viewModel.usersTEA!!.isNotEmpty()){
                viewModel.usersTEA!!.removeLast()
                gUsuario.guardarConfiguracionUsersTEA(viewModel.usersTEA!!, viewModel.idUsuario, this)
            }
        }catch (e: Exception){
            Toast.makeText(this, getString(R.string.toast_error_guardar_configuracion), Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    override fun onEditName(position: Int, name: String) {
        viewModel.usersTEA!![position].name = name
    }

    override fun onEditImage(position: Int) {
        val intent = Intent(this, MenuAvataresTEActivity::class.java)
        intent.putExtra("editPreferences", true)
        viewModel.userSelectPicto = position
        resultLauncherImagen!!.launch(intent)
    }

    override fun onClick(isEdit: Boolean, positionUser: Int, position: Int){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_bienvenida)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo).setOnClickListener {
            dialog.dismiss()
        }

        val frame = dialog.findViewById<FrameLayout>(R.id.fragment_nuevoPicto)
        frame.addView(layoutInflater.inflate(R.layout.fragment_usuario_tea, frame, false) as View)

        frame.findViewById<TextView>(R.id.textUser).visibility = View.GONE
        frame.findViewById<MaterialCardView>(R.id.cardUsuario).visibility = View.GONE
        frame.findViewById<TextInputLayout>(R.id.txt_NameUsuario).visibility = View.GONE

        imageActividad = frame.findViewById(R.id.imagenActividad)
        val card = frame.findViewById<MaterialCardView>(R.id.cardActividad)
        val nombre = frame.findViewById<TextInputLayout>(R.id.txt_NameActividad)
        val guardar = frame.findViewById<MaterialButton>(R.id.btn_guardar)
        val borrar = frame.findViewById<MaterialButton>(R.id.btn_borrar)
        val gActividad = GestionActividades()
        val gCategoriaActividad = GestionCategoriasActividad()
        viewModel.selectedCategoriasNueva.clear()

        card.setOnClickListener {
            val intent = Intent(this, MenuObjetosActivity::class.java)
            intent.putExtra("editPreferences", true)
            resultLauncher!!.launch(intent)
        }

        viewModel.arrayCategorias = gCategoriaActividad.getCategorias(viewModel.usersTEA!![positionUser].id, this)
        val chipGroup = frame.findViewById<ChipGroup>(R.id.chipGroup)

        if (viewModel.arrayCategorias!!.size == 0) {
            chipGroup.visibility = View.GONE
            dialog.findViewById<TextView>(R.id.lbl_categoria).visibility = View.GONE
        }else{
            chipGroup.visibility = View.VISIBLE
            dialog.findViewById<TextView>(R.id.lbl_categoria).visibility = View.VISIBLE
            for (i in viewModel.arrayCategorias!!.indices) {
                chipGroup?.addView(viewModel.createTagChip(this, viewModel.arrayCategorias!![i].nombre!!, viewModel.arrayCategorias!![i].id!!.toInt()))
            }
        }

        if(isEdit){
            for(i in viewModel.arrayCategorias?.indices!!){
                if(viewModel.usersTEA!![positionUser].actividades!![position].idCategoria!!.contains(viewModel.arrayCategorias!![i].id)){
                    chipGroup?.findViewById<Chip>(viewModel.arrayCategorias!![i].id!!.toInt())?.isChecked = true
                    viewModel.selectedCategoriasNueva.add(viewModel.arrayCategorias!![i].id!!)
                }
            }

            imageActividad?.setImageBitmap(viewModel.usersTEA!![positionUser].actividades!![position].imagen)
            nombre.editText?.setText(viewModel.usersTEA!![positionUser].actividades!![position].nombre)

            guardar.text = getString(R.string.actualizar)
            borrar.visibility = View.VISIBLE

            guardar.setOnClickListener {
                if(nombre.editText?.text.toString() == ""){
                    Toast.makeText(this, getString(R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val imagenBlobActividad = CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
                gActividad.actualizarActividad(viewModel.usersTEA!![positionUser].actividades!![position].id, nombre.editText?.text.toString(), imagenBlobActividad , this)
                val nuevasCategorias = viewModel.selectedCategoriasNueva
                val antiguasCategorias = viewModel.usersTEA!![positionUser].actividades!![position].idCategoria!!

                for (categoria in nuevasCategorias) {
                    if (!antiguasCategorias.contains(categoria)) {
                        gActividad.addCategoriaActividad(viewModel.usersTEA!![positionUser].actividades!![position].id, categoria, this)
                        viewModel.usersTEA!![positionUser].actividades!![position].idCategoria!!.add(categoria)
                    }
                }

                for (categoria in antiguasCategorias) {
                    if (!nuevasCategorias.contains(categoria)) {
                        gActividad.removeCategoriaActividad(viewModel.usersTEA!![positionUser].actividades!![position].id, categoria, this)
                        viewModel.usersTEA!![positionUser].actividades!![position].idCategoria!!.remove(categoria)
                    }
                }

                viewModel.usersTEA!![positionUser].actividades!![position].nombre = nombre.editText?.text.toString()
                viewModel.usersTEA!![positionUser].actividades!![position].imagen = (imageActividad!!.drawable as BitmapDrawable).bitmap ?: viewModel.usersTEA!![positionUser].actividades!![position].imagen
                adapterUsers?.notifyItemChanged(positionUser)

                dialog.dismiss()
            }

            borrar.setOnClickListener {
                gActividad.borrarActividad(viewModel.usersTEA!![positionUser].actividades!![position].id, this)
                viewModel.usersTEA!![positionUser].actividades!!.removeAt(position)
                adapterUsers?.notifyItemChanged(positionUser)
                dialog.dismiss()
            }

        }else{
            guardar.setOnClickListener {
                if(nombre.editText?.text.toString() == ""){
                    Toast.makeText(this, getString(R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val imagenBlobActividad = CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
                val idActividad = gActividad.crearActividad(nombre.editText?.text.toString(), imagenBlobActividad, viewModel.usersTEA!![positionUser].id, this)
                for(i in viewModel.selectedCategoriasNueva.indices){
                    gActividad.addCategoriaActividad(idActividad, viewModel.selectedCategoriasNueva[i], this)
                }

                val listaActividades = viewModel.usersTEA!![positionUser].actividades
                val nuevaActividad = Actividad(idActividad, nombre.editText?.text.toString(), (imageActividad!!.drawable as BitmapDrawable).bitmap, viewModel.selectedCategoriasNueva, viewModel.usersTEA!![positionUser].id)
                listaActividades?.add(listaActividades.size -1, nuevaActividad)
                viewModel.usersTEA!![positionUser].actividades = listaActividades
                adapterUsers?.notifyItemChanged(positionUser)

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    override fun onBorrarUser(position: Int) {
        val gUsuario = GestionUsuarios()
        val idUsuarioTEA = viewModel.usersTEA!![position].id
        if(idUsuarioTEA != ""){
            gUsuario.borrarUsuarioTEA(viewModel.idUsuario,  idUsuarioTEA, this)
            viewModel.usersTEA!!.removeAt(position)
            adapterUsers?.notifyItemRemoved(position)
        }

        if(viewModel.usersTEA!!.size <3 && viewModel.usersTEA!![viewModel.usersTEA!!.size - 1].name != null){
            viewModel.usersTEA!!.add(Usuario())
            adapterUsers?.notifyItemInserted(viewModel.usersTEA!!.size - 1)
        }
    }

    override fun changeConfigPicto(position: Int) {
        val intent = Intent(this, ConfiguracionPictogramasActivity::class.java)
        intent.putExtra("editPreferences", true)
        viewModel.userSelectPicto = position
        resultLauncherConfigPicto!!.launch(intent)
    }

    override fun onNuevoUser() {
        dialogoNewUser()
    }

    private fun dialogoNewUser(){
        //create dialog to show the first time
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_bienvenida)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)

        val frame = dialog.findViewById<FrameLayout>(R.id.fragment_nuevoPicto)
        frame.addView(layoutInflater.inflate(R.layout.fragment_usuario_tea, frame, false) as View)

        val buttonGuardar = frame.findViewById<MaterialButton>(R.id.btn_guardar)
        val buttonImagenUsuario = frame.findViewById<MaterialCardView>(R.id.cardUsuario)
        val buttonImagenActividad = frame.findViewById<MaterialCardView>(R.id.cardActividad)
        frame.findViewById<TextView>(R.id.lbl_categoria).visibility = View.GONE
        frame.findViewById<ChipGroup>(R.id.chipGroup).visibility = View.GONE
        image = frame.findViewById(R.id.imagenUsuario)
        imageActividad = frame.findViewById(R.id.imagenActividad)


        buttonGuardar.setOnClickListener {
            val name = frame.findViewById<TextInputLayout>(R.id.txt_NameUsuario).editText?.text.toString().uppercase()
            val nameObjeto = frame.findViewById<TextInputLayout>(R.id.txt_NameActividad).editText?.text.toString().uppercase()
            val gUsuario = GestionUsuarios()
            //añadir boton para indicar que se quiere poner un objeto
            if(nameObjeto == "" ||imageActividad?.drawable == null || name == "" || image?.drawable == null){
                Toast.makeText(this, getString(R.string.toast_rellenar_campos), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val imagenBlob = CommonUtils.bitmapToByteArray((image!!.drawable as BitmapDrawable).bitmap)
            val idUsuarioTEA = gUsuario.crearUsuarioTEA(name, imagenBlob, "default", viewModel.idUsuario, this)

            if(idUsuarioTEA == ""){
                Toast.makeText(this, getString(R.string.error_usuario_tea), Toast.LENGTH_SHORT).show()
            }else{
                val gActividad = GestionActividades()
                val imagenBlobActividad = CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
                val idActividad = gActividad.crearActividad(nameObjeto, imagenBlobActividad, idUsuarioTEA, this)
                val newActividad = Actividad(idActividad, nameObjeto, (imageActividad!!.drawable as BitmapDrawable).bitmap, null, idUsuarioTEA)
                val listaActividades = ArrayList<Actividad>()
                listaActividades.add(newActividad)
                prefs.edit().putString("idUsuarioTEA", idUsuarioTEA).apply()
                if(viewModel.usersTEA!!.size >=3){
                    viewModel.usersTEA!!.removeLast()
                    viewModel.usersTEA!!.add(Usuario(null, name, null, (image!!.drawable as BitmapDrawable).bitmap, listaActividades,  "default"))
                    adapterUsers?.notifyItemChanged(viewModel.usersTEA!!.size-1)
                }else{
                    val penultimatePosition = viewModel.getPenultimatePosition(viewModel.usersTEA!!.size)
                    viewModel.usersTEA!!.add(penultimatePosition, Usuario(null, name, null, (image!!.drawable as BitmapDrawable).bitmap, listaActividades,  "default"))
                    adapterUsers?.notifyItemInserted(penultimatePosition)
                }

                dialog.dismiss()
            }
        }

        buttonImagenUsuario.setOnClickListener {
            val intent = Intent(this, MenuAvataresTEActivity::class.java)
            intent.putExtra("editPreferences", true)
            resultLauncher!!.launch(intent)
        }

        buttonImagenActividad.setOnClickListener {
            val intent = Intent(this, MenuObjetosActivity::class.java)
            intent.putExtra("editPreferences", true)
            resultLauncher!!.launch(intent)
        }

        cerrar.setOnClickListener {
            dialog.dismiss()
            prefs.edit().putBoolean("firstTime", false).apply()
        }

        dialog.show()
    }

}