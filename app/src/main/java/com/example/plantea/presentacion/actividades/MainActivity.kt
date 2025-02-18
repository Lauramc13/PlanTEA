@file:Suppress("SpellCheckingInspection")

package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedByteArray
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.example.plantea.presentacion.adaptadores.AdaptadorUserMainClass
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

/**
 * Clase que representa la actividad principal de la aplicación, donde se muestran los usuarios TEA
 *
 * @property imagePlanificador ImageView
 */
class MainActivity : AppCompatActivity(), AdaptadorUserMainClass.OnItemSelectedListener {
    private lateinit var imagePlanificador: ImageView
    private lateinit var nombrePlanificador: TextView
    private lateinit var cardUsuarioPlanificador: CardView
    private lateinit var spinner : Spinner
    private lateinit var imageSpinner : ImageView

    private var navigationHandler = NavegacionUtils()
    private var dialogLogout : Dialog? = null
    private lateinit var prefs: SharedPreferences

    var image :ShapeableImageView? = null
    private var imageActividad :ShapeableImageView? = null
    private var usersTEA : ArrayList<Usuario>? = null
    private var adapterUsers: AdaptadorUserMainClass? = null
    lateinit var recyclerView : RecyclerView

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if(result.data?.extras?.getByteArray("selectedImageUsuario") != null){
                val imagenUriUsuario = result.data?.extras?.getByteArray("selectedImageUsuario") as ByteArray
                image?.setImageBitmap(CommonUtils.byteArrayToBitmap(imagenUriUsuario))
            }else {
                val imagenUriActividad = result.data?.extras?.getByteArray("selectedImageActividad") as ByteArray
                imageActividad?.setImageBitmap(CommonUtils.byteArrayToBitmap(imagenUriActividad))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        configurarDatos()
        dialogFirstTime()
    }

    private fun configurarDatos(){
        val usuario = Usuario()
        usersTEA = usuario.obtenerUsuariosTEA(prefs.getString("idUsuario", ""), this)
        if(usersTEA!!.isNotEmpty()){
            recyclerView.visibility = View.VISIBLE
            adapterUsers = AdaptadorUserMainClass(usersTEA, this)
            val layoutManager = FlexboxLayoutManager(this).apply {
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapterUsers
        }else{
            recyclerView.visibility = View.GONE
        }

        nombrePlanificador.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
        val imagenPlanificador = prefs.getString("imagenPlanificador", "")
        if(imagenPlanificador != ""){
            imagePlanificador.setImageDrawable(null)
            imagePlanificador.setImageBitmap(CommonUtils.byteArrayToBitmap(imagenPlanificador!!.toPreservedByteArray))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

       val accesoConfiguracion = prefs.getBoolean("userAccount", false)
        if (!accesoConfiguracion) {
            val intent = Intent(this@MainActivity, PreLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(prefs.getBoolean("darkMode", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        imagePlanificador = findViewById(R.id.image_RolPlanificador)
        nombrePlanificador = findViewById(R.id.lbl_nombrePlanificador)
        cardUsuarioPlanificador = findViewById(R.id.cardViewPlanificador)
        spinner = findViewById(R.id.spinner_idiomas)
        imageSpinner = findViewById(R.id.image_idioma)
        recyclerView = findViewById(R.id.recyclerView)

        val preferencias: MaterialButton = findViewById(R.id.image_RolPlanificador2)
        val buttonLogout: MaterialButton? = findViewById(R.id.btn_logout)

        //Preferencias
        configurarDatos()

        //Este método se ejecutará al pinchar sobre la imagen del rol planificador
        cardUsuarioPlanificador.setOnClickListener {
            if (usersTEA!!.isEmpty()) {
                val editor = prefs.edit()
                editor.putBoolean("PlanificadorLogged", true)
                editor.putString("configPictogramas", "default")
                editor.putString("idUsuarioTEA", "")
                editor.putString("imagenUsuarioTEA", "")

                editor.apply()
                val intent = Intent(applicationContext, EventosPlanificadorActivity::class.java)
                startActivity(intent)

            }else{
                 navigationHandler.crearDialogoLoginMain(this, this, usersTEA)
            }
        }

        preferencias.setOnClickListener{
            val intent = Intent(applicationContext, ConfiguracionActivity::class.java)
            startActivity(intent)
        }

        buttonLogout?.setOnClickListener{
            dialogLogout()
        }

        configurationLanguage()
    }

    private fun configurationLanguage(){
        val idiomas = ArrayList<String>()
        idiomas.add("Español")
        idiomas.add("English")
        val adapter = ArrayAdapter(applicationContext, R.layout.simple_spinner_item_idioma, idiomas)
        spinner.adapter = adapter

        val currentLanguage = Locale.getDefault().displayLanguage
        val position = adapter.getPosition(currentLanguage)
        spinner.setSelection(position)
        imageSpinner(currentLanguage)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(idiomas[position] == "English"){
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag("en")))
                }else{
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag("es")))
                }
                imageSpinner(idiomas[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }
        }
    }

    private fun dialogFirstTime(){
       if (CommonUtils.Companion.PreferencesHelper.isFirstTime(this, "MainActivityFirstTime")) {
            CommonUtils.Companion.PreferencesHelper.setFirstTime(this, "MainActivityFirstTime", false)
            //create dialog to show the first time
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialogo_bienvenida)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val frame = dialog.findViewById<FrameLayout>(R.id.fragment_nuevoPicto)
            val cerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)

            val viewBienvenida = layoutInflater.inflate(R.layout.view_bienvenida, frame, false) as View
            frame.addView(viewBienvenida)
            val viewUserConfiguration = layoutInflater.inflate(R.layout.fragment_usuario_tea, frame, false) as View

            val chipGroup = viewUserConfiguration.findViewById<ChipGroup>(R.id.chipGroup)
            chipGroup.visibility = View.GONE
            viewUserConfiguration.findViewById<TextView>(R.id.lbl_categoria).visibility = View.GONE

            val userConfigurationIN = TranslateAnimation(Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            val bienvenidaOUT = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
            configurateAnimation(userConfigurationIN, bienvenidaOUT, this)

            val btnSaltar = frame.findViewById<MaterialButton>(R.id.btn_saltar)
            btnSaltar.setOnClickListener {
                dialog.dismiss()
            }

            val btnContinuar = frame.findViewById<MaterialButton>(R.id.btn_continuar)
            btnContinuar.setOnClickListener {
                viewUserConfiguration.startAnimation(userConfigurationIN)
                viewBienvenida.startAnimation(bienvenidaOUT)
                frame.removeAllViews()
                frame.addView(viewUserConfiguration)

                val buttonGuardar = frame.findViewById<MaterialButton>(R.id.btn_guardar)
                val buttonImagenUsuario = frame.findViewById<MaterialCardView>(R.id.cardUsuario)
                val buttonImagenActividad = frame.findViewById<MaterialCardView>(R.id.cardActividad)
                image = frame.findViewById(R.id.imagenUsuario)
                imageActividad = frame.findViewById(R.id.imagenActividad)

                buttonGuardar.setOnClickListener {
                    val name = frame.findViewById<TextInputLayout>(R.id.txt_NameUsuario).editText?.text.toString()
                    val nameObjeto = frame.findViewById<TextInputLayout>(R.id.txt_NameActividad).editText?.text.toString()
                    val idUsuario = prefs.getString("idUsuario", "")
                    val usuario = Usuario()
                    //añadir boton para indicar que se quiere poner un objeto
                    if(nameObjeto == "" ||imageActividad?.drawable == null || name == "" || image?.drawable == null){
                        Toast.makeText(this, getString(R.string.toast_rellenar_campos), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val imagenBlob = CommonUtils.bitmapToByteArray((image!!.drawable as BitmapDrawable).bitmap)
                    val idUsuarioTEA = usuario.crearUsuarioTEA(name, imagenBlob, "default", idUsuario, this)
                    if(idUsuarioTEA == ""){
                        Toast.makeText(this, getString(R.string.error_usuario_tea), Toast.LENGTH_SHORT).show()
                    }else{
                        val actividad = Actividad()
                        val imagenBlobActividad = CommonUtils.bitmapToByteArray((imageActividad!!.drawable as BitmapDrawable).bitmap)
                        actividad.crearActividad(nameObjeto,imagenBlobActividad, idUsuarioTEA,  this)
                        prefs.edit().putString("idUsuarioTEA", idUsuarioTEA).apply()
                    }
                    dialog.dismiss()
                    //update recycler view
                    configurarDatos()
                }

                buttonImagenUsuario.setOnClickListener {
                    val intent = Intent(this, MenuAvataresTEActivity::class.java)
                    intent.putExtra("isFromMain", true)
                    resultLauncher.launch(intent)
                }

                buttonImagenActividad.setOnClickListener {
                    val intent = Intent(this, MenuObjetosActivity::class.java)
                    intent.putExtra("isFromMain", true)
                    resultLauncher.launch(intent)
                }
            }

            cerrar.setOnClickListener {
                dialog.dismiss()
                prefs.edit().putBoolean("firstTime", false).apply()
            }

            dialog.show()
        }
    }

    private fun configurateAnimation(userConfigurationININ: TranslateAnimation, bienvenidaOUT: TranslateAnimation, activity: Activity){
        userConfigurationININ.duration = 300
        bienvenidaOUT.duration = 300
        userConfigurationININ.interpolator = AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator)
        bienvenidaOUT.interpolator = AnimationUtils.loadInterpolator(activity, android.R.anim.decelerate_interpolator)
    }

    private fun imageSpinner(currntLanguage: String) {
        if (currntLanguage == "English") {
            imageSpinner.setImageResource(R.drawable.ic_en)
        } else {
            imageSpinner.setImageResource(R.drawable.ic_es)
        }
    }

    private fun dialogLogout(){
        dialogLogout = Dialog(this)
        dialogLogout?.setContentView(R.layout.dialogo_logout)
        dialogLogout?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnLogout = dialogLogout?.findViewById<MaterialButton>(R.id.btn_logout)
        val iconoCerrar = dialogLogout?.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        btnLogout?.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        if (dialogLogout?.isShowing == true) {
            dialogLogout?.dismiss()
        }
    }

    override fun onClickUser(position: Int) {
        val editor = prefs.edit()
        editor.putString("idUsuarioTEA", usersTEA!![position].id)
        editor.putString("nombreUsuarioTEA", usersTEA!![position].name)
        editor.putString("imagenUsuarioTEA", CommonUtils.bitmapToByteArray(usersTEA!![position].imagen!!).toPreservedString)
        editor.putBoolean("PlanificadorLogged", false)
        editor.putString("configPictogramas", usersTEA!![position].configPictograma)

        editor.apply()
        val intent = Intent(this, EventosActivity::class.java)
        startActivity(intent)
    }
}