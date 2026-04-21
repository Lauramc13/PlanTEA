package com.example.plantea.presentacion.actividades


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Actividad
import com.example.plantea.dominio.gestores.GestionActividades
import com.example.plantea.dominio.gestores.GestionCategoriasActividad
import com.example.plantea.dominio.gestores.GestionUsuarios
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedByteArray
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.example.plantea.presentacion.adaptadores.ActividadAdapter
import com.example.plantea.presentacion.adaptadores.UserAdapter
import com.example.plantea.presentacion.viewModels.ConfiguracionViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ConfiguracionActivity : AppCompatActivity(), UserAdapter.OnItemSelectedListener, ActividadAdapter.OnItemSelectedListenerActividad {
    lateinit var prefs: SharedPreferences

    private lateinit var imgUsuarioPlanificador: ImageView
    private var nombreTextView: TextView? = null
    private var txtPlanificador : TextInputLayout? = null
    private var txtCorreoPlanificador : TextInputLayout? = null
    private var dialogLogout : Dialog? = null
    private var switchNoti : MaterialSwitch? = null
    private var switchOscuro : MaterialSwitch? = null
    private var adapterUsers: RecyclerView.Adapter<*>? = null

    private var restart = false
    var image : ShapeableImageView? = null
    private var imageActividad : ShapeableImageView? = null

    private var recyclerViewUsers: RecyclerView? = null

    // Para cuando se crea un usuario TEA nuevo
    private var resultLauncher : ActivityResultLauncher<Intent>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
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
    }

    //Cambiar la configuracion de pictogramas de un usuario TEA
    private var resultLauncherConfigPicto :  ActivityResultLauncher<Intent>? = registerForActivityResult(
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
    private var resultLauncherImagen :  ActivityResultLauncher<Intent>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagen = result.data?.extras?.getByteArray("selectedImageUsuario") as ByteArray
            viewModel.usersTEA!![viewModel.userSelectPicto].imagen = CommonUtils.byteArrayToBitmap(imagen)
            adapterUsers?.notifyItemChanged(viewModel.userSelectPicto)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        switchNoti?.isChecked = it
    }

    private val viewModel by viewModels<ConfiguracionViewModel>()

    override fun onResume() {
        super.onResume()
        configurarDatos()
        getImages()
        restart = false
        switchOscuro?.isChecked = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onRestart() {
        super.onRestart()
        restart = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogLogout?.isShowing == true) {
            dialogLogout?.dismiss()
        }

        if (!isFinishing || !viewModel.saved) {
            prefs.edit().remove("imagenPlanificadorConfig").apply()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.name = txtPlanificador?.editText?.text.toString()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_configuracion)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !prefs.getBoolean("darkMode", false)

        imgUsuarioPlanificador = findViewById(R.id.img_FotoPlanificador)
        viewModel.idUsuario = prefs.getString("idUsuario", "").toString()

        //Version tablet
        txtPlanificador = findViewById(R.id.txt_nombrePlanificador)
        txtCorreoPlanificador = findViewById(R.id.txt_correoPlanificador)
        val btnGuardar : Button? = findViewById(R.id.btn_guardarConfiguracion)
        val btnPassword : Button? = findViewById(R.id.buttonContrasenia)

        nombreTextView = findViewById(R.id.txt_nombre)
        val btnEditPerfil : MaterialButton? = findViewById(R.id.btnEdit)
        switchNoti = findViewById(R.id.switch_notificaciones)
        switchOscuro = findViewById(R.id.switch_oscuro)

        val ayuda : MaterialCardView = findViewById(R.id.btn_ayuda)
        val credits : MaterialCardView = findViewById(R.id.btn_credits)
        val btnPolitica : MaterialCardView = findViewById(R.id.btnPolitica)

        val btnLogout : MaterialCardView = findViewById(R.id.btnCerrarSesion)
        val btnUsersTEA : MaterialCardView? = findViewById(R.id.btn_users_tea)

        if(savedInstanceState != null){
            txtPlanificador?.editText?.setText(viewModel.name)
            nombreTextView?.text = viewModel.name
        }else {
            txtPlanificador?.editText?.setText(prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault()))
            nombreTextView?.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())

            val gUsuario = GestionUsuarios()
            viewModel.usersTEA = gUsuario.obtenerUsuariosTEA(viewModel.idUsuario, this)
            if(viewModel.usersTEA!!.size <3)
                viewModel.usersTEA!!.add(Usuario())
        }

        adapterUsers = UserAdapter(viewModel.usersTEA, this, this)  

        if(!CommonUtils.isMobile(this)){
            initRecyclerViewUsers()

            imgUsuarioPlanificador.setOnClickListener {
                val intent = Intent(applicationContext, MenuAvataresPlanActivity::class.java)
                intent.putExtra("editPreferences", true)
                startActivity(intent)
            }
        }else{
            val btnOrden : Spinner? = findViewById(R.id.btn_orden)
            spinnerOrdenPictogramas(btnOrden)
        }

        txtCorreoPlanificador?.editText?.setText(prefs.getString("email", "")!!.lowercase(Locale.getDefault()))

        ayuda.setOnClickListener{
            val intent = Intent(applicationContext, ManualActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        credits.setOnClickListener{
            val intent = Intent(applicationContext, CreditsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        btnPassword?.setOnClickListener {
            val intent = Intent(applicationContext, PasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        btnGuardar?.setOnClickListener {
           guardarConfiguracion()
        }

        btnLogout?.setOnClickListener {
            dialogLogout()
        }

        btnPolitica?.setOnClickListener {
            val intent = Intent(applicationContext, PoliticaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        btnEditPerfil?.setOnClickListener {
            val intent = Intent(applicationContext, EditarPerfilActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        btnUsersTEA?.setOnClickListener {
            val intent = Intent(applicationContext, ConfiguracionUsersTEAActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            switchNoti?.isChecked = true
        }

        switchNoti?.setOnCheckedChangeListener { _, isChecked ->
           changeNotifications(isChecked)
        }

        // if the current theme is dark, set the switch to checked
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            switchOscuro?.isChecked = true
        }
        switchOscuro?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                prefs.edit().putBoolean("darkMode", true).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                prefs.edit().putBoolean("darkMode", false).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        observers()
    }

    private fun initRecyclerViewUsers(){
        recyclerViewUsers = findViewById(R.id.recycler_viewUsers)

        recyclerViewUsers?.layoutManager = if (resources.configuration.orientation == 1) {
            GridLayoutManager(this, 2)
        }else{
            GridLayoutManager(this, 3)
        }

        recyclerViewUsers?.adapter = adapterUsers
    }

    fun observers(){
        viewModel.seToast.observe(this) {
            Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarDatos(){
        if (prefs.getString("imagenPlanificador", "") == "") {
            imgUsuarioPlanificador.setBackgroundResource(R.drawable.svg_add_image)
        } else {
            imgUsuarioPlanificador.background = null
            imgUsuarioPlanificador.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenPlanificador", "")?.toPreservedByteArray))
        }
    }

    private fun guardarConfiguracion(){
        val nombreUsuarioPlanificador = txtPlanificador?.editText?.text.toString()

        recyclerViewUsers?.clearFocus()

        val isValid = viewModel.comprobarCampos(nombreUsuarioPlanificador)

        if(isValid){

            val editor = prefs.edit()
            val imagenConfig = prefs.getString("imagenPlanificadorConfig", "")
            val imagenFinal: String = if(!imagenConfig.isNullOrEmpty() && imagenConfig != "null"){
                imagenConfig
            } else {
                val bitmap = (imgUsuarioPlanificador.drawable as BitmapDrawable).bitmap
                CommonUtils.bitmapToByteArray(bitmap).toPreservedString
            }

            editor.putString("nombrePlanificador", nombreUsuarioPlanificador)
            editor.putString("imagenPlanificador", imagenFinal)

            editor.remove("imagenPlanificadorConfig")

            editor.apply()

            try {
                val gUsuario = GestionUsuarios()
                gUsuario.guardarConfiguracion(nombreUsuarioPlanificador, imagenFinal.toPreservedByteArray, viewModel.idUsuario, this)

                if(viewModel.usersTEA!!.isNotEmpty()){
                    viewModel.usersTEA!!.removeAt(viewModel.usersTEA!!.size - 1)
                    gUsuario.guardarConfiguracionUsersTEA(viewModel.usersTEA!!, viewModel.idUsuario, this)
                }
            }catch (e: Exception){
                Toast.makeText(this, getString(R.string.toast_error_guardar_configuracion), Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    private fun getImages(){
        if (!restart) return

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
                    viewModel.usersTEA!!.removeAt(viewModel.usersTEA!!.size - 1)
                    viewModel.usersTEA!!.add(Usuario(null, name, null, (image!!.drawable as BitmapDrawable).bitmap, listaActividades,  "default"))
                    adapterUsers?.notifyItemChanged(viewModel.usersTEA!!.size-1)
                }else{
                    val penultimatePosition = viewModel.getPenultimatePosition(viewModel.usersTEA!!.size)
                    viewModel.usersTEA!!.add(penultimatePosition, Usuario(idUsuarioTEA, name, null, (image!!.drawable as BitmapDrawable).bitmap, listaActividades,  "default"))
                    adapterUsers?.notifyItemInserted(penultimatePosition)
                }

                dialog.dismiss()
            }
        }

        buttonImagenUsuario.setOnClickListener {
            try{
                val intent = Intent(applicationContext, MenuAvataresTEActivity::class.java)
                intent.putExtra("editPreferences", true)
                resultLauncher!!.launch(intent)
            }catch (e: Exception){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                Log.e("ConfiguracionActivity", "Error al abrir el intent para seleccionar la imagen del usuario TEA", e)
            }

        }

        buttonImagenActividad.setOnClickListener {
            try {
                val intent = Intent(applicationContext, MenuObjetosActivity::class.java)
                intent.putExtra("editPreferences", true)
                resultLauncher!!.launch(intent)
            }catch (e: Exception){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                Log.e("ConfiguracionActivity", "Error al abrir el intent para seleccionar la imagen del usuario TEA", e)
            }
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

    private fun dialogLogout(){
        dialogLogout = Dialog(this)
        dialogLogout?.setContentView(R.layout.dialogo_logout)
        dialogLogout?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonLogout = dialogLogout?.findViewById<MaterialButton>(R.id.btn_logout)
        val iconoCerrar = dialogLogout?.findViewById<ImageView>(R.id.icono_CerrarDialogo)

        buttonLogout?.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()

            val editor = prefs.edit()

            for (key in prefs.all.keys) {
                if (!(key.startsWith("initialization_vector") || key.startsWith("secret_key") || key.endsWith("FirstTime"))) {
                    editor.remove(key)
                }
            }
            editor.apply()
            val intent = Intent(this, PreLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finishAffinity()
        }

        iconoCerrar?.setOnClickListener { dialogLogout?.dismiss() }
        dialogLogout?.show()
    }


    override fun changeConfigPicto(position: Int) {
        try{
            val intent = Intent(this, ConfiguracionPictogramasActivity::class.java)
            intent.putExtra("editPreferences", true)
            viewModel.userSelectPicto = position
            resultLauncherConfigPicto?.launch(intent)
        }catch (e: Exception){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBorrarUser(position: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_eliminar_evento)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<TextView>(R.id.lbl_title).text = getString(R.string.eliminar_usuarioTEA)
        dialog.findViewById<TextView>(R.id.lbl_subtitle).text = getString(R.string.eliminar_usuarioTEA_mensaje)

        val buttonBorrar = dialog.findViewById<MaterialButton>(R.id.btn_eliminar)
        val buttonCancelar = dialog.findViewById<MaterialButton>(R.id.btn_cancelar)
        val iconoCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        buttonBorrar.setOnClickListener {
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
            dialog.dismiss()
        }

        buttonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        iconoCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onEditName(position: Int, name: String) {
        viewModel.usersTEA!![position].name = name
    }

    override fun onEditImage(position: Int) {
        val intent = Intent(this, MenuAvataresTEActivity::class.java)
        intent.putExtra("editPreferences", true)
        viewModel.userSelectPicto = position
        resultLauncherImagen?.launch(intent)
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
        val gActividad = GestionActividades()
        val gCategoriaActividad = GestionCategoriasActividad()
        viewModel.selectedCategoriasNueva.clear()

        card.setOnClickListener {
            val intent = Intent(this, MenuObjetosActivity::class.java)
            intent.putExtra("editPreferences", true)
            resultLauncher?.launch(intent)
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

    private fun changeNotifications(isChecked: Boolean) {
        if(isChecked){
            //Para versiones anteriores no se necesita preguntar por permisos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }else{
            Toast.makeText(this, getString(R.string.toast_notificaciones_activadas), Toast.LENGTH_LONG).show()
            switchNoti?.isChecked = true
        }
    }

    private fun spinnerOrdenPictogramas(spinner: Spinner?){
        val config = ArrayList<String>()
        config.add(getString(R.string.horizontal))
        config.add(getString(R.string.vertical))
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item_idioma, config)
        spinner?.adapter = adapter

        val isVertical = prefs.getBoolean("isVerticalPictogramas", false)
        spinner?.setSelection(if(isVertical) 1 else 0)

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefs.edit().putBoolean("isVerticalPictogramas", position == 1).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

}