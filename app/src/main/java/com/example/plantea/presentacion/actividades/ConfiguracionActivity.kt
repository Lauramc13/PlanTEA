package com.example.plantea.presentacion.actividades


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.dominio.CategoriaActividad
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedByteArray
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.example.plantea.presentacion.adaptadores.ActividadAdapter
import com.example.plantea.presentacion.adaptadores.UserAdapter
import com.example.plantea.presentacion.viewModels.ConfiguracionViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ConfiguracionActivity : AppCompatActivity(), UserAdapter.OnItemSelectedListener, ActividadAdapter.OnItemSelectedListenerActividad {
    lateinit var prefs: SharedPreferences
    private lateinit var imgUsuarioPlanificador: ImageView
    private lateinit var txtPlanificador : TextInputLayout
    private lateinit var txtUsernamePlanificador : TextInputLayout
    private lateinit var txtCorreoPlanificador : TextInputLayout

    private var restart = false
    var image : ShapeableImageView? = null
    private var imageActividad : ShapeableImageView? = null



    private lateinit var recyclerViewUsers: RecyclerView

    private var resultLauncher :  ActivityResultLauncher<Intent>? = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                if(result.data?.extras?.getByteArray("selectedImageUsuario") != null){
                    val imagenUriUsuario = result.data?.extras?.getByteArray("selectedImageUsuario") as ByteArray
                    image?.setImageBitmap(CommonUtils.byteArrayToBitmap(imagenUriUsuario))
                    image?.background = null
                }else {
                    val imagenUriActividad = result.data?.extras?.getByteArray("selectedImageActividad") as ByteArray
                    imageActividad?.setImageBitmap(CommonUtils.byteArrayToBitmap(imagenUriActividad))
                    imageActividad?.background = null
                }
            }
        }catch (e: Exception){
            Log.e("Error", e.message.toString())
        }
    }

    //Cambiar la configuracion de pictogramas de un usuario TEA
    private var resultLauncherConfigPicto : ActivityResultLauncher<Intent>? = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val config = result.data?.extras?.getString("configPicto") as String
            val usuario = Usuario()
            usuario.cambiarConfiguracionPictogramas(config, viewModel.usersTEA!![viewModel.userSelectPicto].id, this) // TODO: NO GUARDAR AQUI, GUARDAR CUANDO SE GUARDE LA CONFIGURACION
            viewModel.usersTEA!![viewModel.userSelectPicto].configPictograma = config
            viewModel.adapterUsers?.notifyItemChanged(viewModel.userSelectPicto)
        }
    }

    //Editar la imagen de un usuario TEA
    private var resultLauncherImagen : ActivityResultLauncher<Intent>? = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagen = result.data?.extras?.getByteArray("selectedImageUsuario") as ByteArray
            viewModel.usersTEA!![viewModel.userSelectPicto].imagen = CommonUtils.byteArrayToBitmap(imagen)
            viewModel.adapterUsers?.notifyItemChanged(viewModel.userSelectPicto)
        }
    }

    private val viewModel by viewModels<ConfiguracionViewModel>()

    override fun onResume() {
        super.onResume()
        configurarDatos()
        getImages()
        restart = false
    }

    override fun onRestart() {
        super.onRestart()
        restart = true
    }

    override fun onDestroy() {
        super.onDestroy()
        resultLauncher = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.name = txtPlanificador.editText?.text.toString()
        viewModel.username = txtUsernamePlanificador.editText?.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        imgUsuarioPlanificador = findViewById(R.id.img_FotoPlanificador)
        viewModel.idUsuario = prefs.getString("idUsuario", "").toString()

        txtPlanificador = findViewById(R.id.txt_nombrePlanificador)
        txtUsernamePlanificador = findViewById(R.id.txt_nombreUsuarioPlanificador)
        txtCorreoPlanificador = findViewById(R.id.txt_correoPlanificador)

        val btnGuardar : Button = findViewById(R.id.btn_guardarConfiguracion)
        val btnPassword : Button= findViewById(R.id.buttonContrasenia)
        val credits : TextView = findViewById(R.id.btn_credits)

        if(savedInstanceState != null){
            txtPlanificador.editText?.setText(viewModel.name)
            txtUsernamePlanificador.editText?.setText(viewModel.username)
        }else {
            txtPlanificador.editText?.setText(prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault()))
            txtUsernamePlanificador.editText?.setText(prefs.getString("nombreUsuarioPlanificador", "")!!.uppercase(Locale.getDefault()))

            val usuario = Usuario()
            viewModel.usersTEA = usuario.obtenerUsuariosTEA(viewModel.idUsuario, this)
            if(viewModel.usersTEA!!.size <3)
                viewModel.usersTEA!!.add(Usuario())
            viewModel.adapterUsers = UserAdapter(viewModel.usersTEA, this, this)
        }

        recyclerViewUsers = findViewById(R.id.recycler_viewUsers)
        if (resources.configuration.orientation == 1)
            recyclerViewUsers.layoutManager = GridLayoutManager(this, 2)
        else{
            recyclerViewUsers.layoutManager = GridLayoutManager(this, 3)
        }

        recyclerViewUsers.adapter = viewModel.adapterUsers

        txtCorreoPlanificador.editText?.setText(prefs.getString("email", "")!!.lowercase(Locale.getDefault()))

        credits.paintFlags = credits.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        credits.setOnClickListener{
            val intent = Intent(applicationContext, CreditsActivity::class.java)
            startActivity(intent)
        }

        btnPassword.setOnClickListener {
            val intent = Intent(applicationContext, PasswordActivity::class.java)
            startActivity(intent)
        }

        imgUsuarioPlanificador.setOnClickListener {
            val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
            intent.putExtra("editPreferences", true)
            startActivity(intent)
        }

        btnGuardar.setOnClickListener {
           guardarConfiguracion()
        }

        observers()
    }

    fun observers(){
        viewModel._toast.observe(this) {
            Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarDatos(){
        if (prefs.getString("imagenPlanificador", "") == "") {
            imgUsuarioPlanificador.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
        } else {
            imgUsuarioPlanificador.background = null
            imgUsuarioPlanificador.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenPlanificador", "")?.toPreservedByteArray))
        }

    }

    private fun guardarConfiguracion(){
        //Obtain values from the fields
        val nombreUsuarioPlanificador = txtPlanificador.editText?.text.toString()
        val username = txtUsernamePlanificador.editText?.text.toString()

        //remove focus from textview in recyclerview
        recyclerViewUsers.clearFocus()

        //if drawable doesnt exists, set it to null
        val isValid = viewModel.comprobarCampos(nombreUsuarioPlanificador, username, imgUsuarioPlanificador.drawable)

        if(isValid){
            val imagenBlob = CommonUtils.bitmapToByteArray((imgUsuarioPlanificador.drawable as BitmapDrawable).bitmap)


            //Cambiamos el valor en preferencias para no acceder a configuracion en el siguiente inicio y guardamos datos de los usuarios
            val editor = prefs.edit()
            editor.putString("nombrePlanificador", nombreUsuarioPlanificador)
            editor.putString("imagenPlanificador", imagenBlob.toPreservedString)
           /* editor.putBoolean("info_objeto", lblObjeto.isChecked)
            editor.putBoolean("info_usuario", lblInfoUsuario.isChecked)
            editor.putString("nombreUsuarioTEA", nombreUsuarioTEA)*/

            editor.putString("imagenPlanificadorConfig", null)

            editor.apply()

            try {
                val usuario = Usuario()
                usuario.guardarConfiguracion(nombreUsuarioPlanificador, username, imagenBlob, viewModel.idUsuario, this)
                if(viewModel.usersTEA!!.isNotEmpty()){
                    viewModel.usersTEA!!.removeLast()
                    usuario.guardarConfiguracionUsersTEA(viewModel.usersTEA!!, viewModel.idUsuario, this)
                }
            }catch (e: Exception){
                Toast.makeText(this, getString(R.string.toast_error_guardar_configuracion), Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun getImages(){
        val value = prefs.getString("imagenPlanificadorConfig", "")
        if (value != "" && value != "null") {
            imgUsuarioPlanificador.background = null
            imgUsuarioPlanificador.setImageBitmap(CommonUtils.byteArrayToBitmap(value?.toPreservedByteArray))
        }
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
            val usuario = Usuario()
            //añadir boton para indicar que se quiere poner un objeto
            if(nameObjeto == "" ||imageActividad?.drawable == null || name == "" || image?.drawable == null){
                Toast.makeText(this, getString(R.string.toast_rellenar_campos), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val imagenBlob = CommonUtils.bitmapToByteArray((image!!.drawable as BitmapDrawable).bitmap)
            val idUsuarioTEA = usuario.crearUsuarioTEA(name, imagenBlob, "default", viewModel.idUsuario, this)

            if(idUsuarioTEA == ""){
                Toast.makeText(this, getString(R.string.error_usuario_tea), Toast.LENGTH_SHORT).show()
            }else{
                val actividad = Actividad()
                val imagenBlobActividad = CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
                val idActividad = actividad.crearActividad(nameObjeto, imagenBlobActividad, idUsuarioTEA, this)
                val newActividad = Actividad(idActividad, nameObjeto, (imageActividad!!.drawable as BitmapDrawable).bitmap, null, idUsuarioTEA)
                val listaActividades = ArrayList<Actividad>()
                listaActividades.add(newActividad)
                prefs.edit().putString("idUsuarioTEA", idUsuarioTEA).apply()
                if(viewModel.usersTEA!!.size >=3){
                    viewModel.usersTEA!!.removeLast()
                    viewModel.usersTEA!!.add(Usuario(name, (image!!.drawable as BitmapDrawable).bitmap, listaActividades,  "default"))
                    viewModel.adapterUsers?.notifyItemChanged(viewModel.usersTEA!!.size-1)
                }else{
                    val penultimatePosition = viewModel.getPenultimatePosition(viewModel.usersTEA!!.size)
                    viewModel.usersTEA!!.add(penultimatePosition, Usuario(name, (image!!.drawable as BitmapDrawable).bitmap, listaActividades,  "default"))
                    viewModel.adapterUsers?.notifyItemInserted(penultimatePosition)
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

    override fun onNuevoUser() {
        dialogoNewUser()
    }

    override fun changeConfigPicto(position: Int) {
        val intent = Intent(this, ConfiguracionPictogramasActivity::class.java)
        intent.putExtra("editPreferences", true)
        viewModel.userSelectPicto = position
        resultLauncherConfigPicto!!.launch(intent)
    }

    override fun onBorrarUser(position: Int) {
        val usuario = Usuario()
        val idUsuarioTEA = viewModel.usersTEA!![position].id
        if(idUsuarioTEA != ""){
            usuario.borrarUsuarioTEA(viewModel.idUsuario,  idUsuarioTEA, this)
            viewModel.usersTEA!!.removeAt(position)
            viewModel.adapterUsers?.notifyItemRemoved(position)
        }

        if(viewModel.usersTEA!!.size <3 && viewModel.usersTEA!![viewModel.usersTEA!!.size - 1].name != null){
            viewModel.usersTEA!!.add(Usuario())
            viewModel.adapterUsers?.notifyItemInserted(viewModel.usersTEA!!.size - 1)
        }
    }

    override fun onEditName(position: Int, name: String) {
        viewModel.usersTEA!![position].name = name
     // viewModel.adapterUsers?.notifyItemChanged(position)
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

        frame.findViewById<LinearLayout>(R.id.linearLayoutUser).visibility = View.GONE
        imageActividad = frame.findViewById(R.id.imagenActividad)
        val card = frame.findViewById<MaterialCardView>(R.id.cardActividad)
        val nombre = frame.findViewById<TextInputLayout>(R.id.txt_NameActividad)
        val guardar = frame.findViewById<MaterialButton>(R.id.btn_guardar)
        val borrar = frame.findViewById<MaterialButton>(R.id.btn_borrar)
        val actividad = Actividad()
        val categoriaActividad = CategoriaActividad()
        viewModel.selectedCategoriasNueva.clear()

        card.setOnClickListener {
            val intent = Intent(this, MenuObjetosActivity::class.java)
            intent.putExtra("editPreferences", true)
            resultLauncher!!.launch(intent)
        }

        viewModel.arrayCategorias = categoriaActividad.getCategorias(viewModel.usersTEA!![positionUser].id, this)
        val chipGroup = frame.findViewById<ChipGroup>(R.id.chipGroup)

        if (viewModel.arrayCategorias!!.size == 0) {
            chipGroup.visibility = View.GONE
            dialog.findViewById<TextView>(R.id.lbl_categoria).visibility = View.GONE
        }else{
            for (i in viewModel.arrayCategorias!!.indices) {
                chipGroup?.addView(viewModel.createTagChip(this, viewModel.arrayCategorias!![i].name!!, viewModel.arrayCategorias!![i].id!!.toInt()))
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
            nombre.editText?.setText(viewModel.usersTEA!![positionUser].actividades!![position].name)

            guardar.text = getString(R.string.actualizar)
            borrar.visibility = View.VISIBLE

            guardar.setOnClickListener {
                if(nombre.editText?.text.toString() == ""){
                    Toast.makeText(this, getString(R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
//                val imagenBlobActividad = if(imagenUriActividad != null){
//                    CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
//                }else{
//                    CommonUtils.bitmapToByteArray(viewModel.usersTEA!![positionUser].actividades!![position].imagen)
//                }

                val imagenBlobActividad = CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
                actividad.actualizarActividad(viewModel.usersTEA!![positionUser].actividades!![position].id, nombre.editText?.text.toString(), imagenBlobActividad , this)
                val nuevasCategorias = viewModel.selectedCategoriasNueva
                val antiguasCategorias = viewModel.usersTEA!![positionUser].actividades!![position].idCategoria!!

                for (categoria in nuevasCategorias) {
                    if (!antiguasCategorias.contains(categoria)) {
                        actividad.addCategoria(viewModel.usersTEA!![positionUser].actividades!![position].id, categoria, this)
                        viewModel.usersTEA!![positionUser].actividades!![position].idCategoria!!.add(categoria)
                    }
                }

                for (categoria in antiguasCategorias) {
                    if (!nuevasCategorias.contains(categoria)) {
                        actividad.removeCategoria(viewModel.usersTEA!![positionUser].actividades!![position].id, categoria, this)
                        viewModel.usersTEA!![positionUser].actividades!![position].idCategoria!!.remove(categoria)
                    }
                }

                viewModel.usersTEA!![positionUser].actividades!![position].name = nombre.editText?.text.toString()
                viewModel.usersTEA!![positionUser].actividades!![position].imagen = (imageActividad!!.drawable as BitmapDrawable).bitmap ?: viewModel.usersTEA!![positionUser].actividades!![position].imagen
                viewModel.adapterUsers?.notifyItemChanged(positionUser)

                dialog.dismiss()
            }

            borrar.setOnClickListener {
                actividad.borrarActividad(viewModel.usersTEA!![positionUser].actividades!![position].id, this)
                viewModel.usersTEA!![positionUser].actividades!!.removeAt(position)
                viewModel.adapterUsers?.notifyItemChanged(positionUser)
                dialog.dismiss()
            }

        }else{
            guardar.setOnClickListener {
                if(nombre.editText?.text.toString() == ""){
                    Toast.makeText(this, getString(R.string.toast_nombre_vacio), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val imagenBlobActividad = CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
                val idActividad = actividad.crearActividad(nombre.editText?.text.toString(), imagenBlobActividad, viewModel.usersTEA!![positionUser].id, this)
                for(i in viewModel.selectedCategoriasNueva.indices){
                    actividad.addCategoria(idActividad, viewModel.selectedCategoriasNueva[i], this)
                }

                val listaActividades = viewModel.usersTEA!![positionUser].actividades
                val nuevaActividad = Actividad(idActividad, nombre.editText?.text.toString(), (imageActividad!!.drawable as BitmapDrawable).bitmap, viewModel.selectedCategoriasNueva, viewModel.usersTEA!![positionUser].id)
                listaActividades?.add(nuevaActividad)
                viewModel.usersTEA!![positionUser].actividades = listaActividades
                viewModel.adapterUsers?.notifyItemChanged(positionUser)

                dialog.dismiss()
            }
        }

        dialog.show()

    }


}