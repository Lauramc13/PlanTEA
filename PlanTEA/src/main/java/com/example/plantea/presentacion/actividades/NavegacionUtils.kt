package com.example.plantea.presentacion.actividades

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionUsuarios
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedByteArray
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale


class NavegacionUtils {
    var gUsuario = GestionUsuarios()
    private lateinit var iconoRol: ImageView
    private lateinit var textoRol: TextView
    private lateinit var navigationView: NavigationView
    private lateinit var navigationViewBottom: BottomNavigationView
    private lateinit var fragmentSide : ConstraintLayout
    private lateinit var buttonMenu : Button
    private lateinit var buttonAccount: LinearLayout
    lateinit var prefs: SharedPreferences
    private var isExpanded = false
    private var popupView : View? = null
    private var activePopupWindow : PopupWindow? = null

    private fun crearDialogoLogin(context: Context, activity: Activity) {
        prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)

        val dialogLogin = Dialog(context)
        dialogLogin.setContentView(R.layout.dialogo_login)
        dialogLogin.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val password = dialogLogin.findViewById<TextInputLayout>(R.id.txt_Password)
        val btnAcceder = dialogLogin.findViewById<MaterialButton>(R.id.btn_login)
        val iconoCerrarLogin = dialogLogin.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        btnAcceder.setOnClickListener {
            if (password.editText?.text.toString() == "") {
                password.error = "El campo no puede estar vacío"
            } else {
                val email = prefs.getString("email", "")
                if(email != null){
                    val idUser = prefs.getString("idUsuario", "")
                    val passwordCifrada = EncryptionUtils.getEncrypt(password.editText?.text.toString(), context, idUser!!)

                    if(gUsuario.checkCredentials(email, passwordCifrada, activity)){
                        val editor = prefs.edit()
                        editor.putBoolean("PlanificadorLogged", true)
                        editor.putString("configPictogramas", "default")
                        editor.apply()
                        //context.startActivity(Intent((context as? Activity)?.baseContext, EventosPlanificadorActivity::class.java))
                        context.startActivity(Intent((context as? Activity)?.baseContext, MenuUserActivity::class.java))
                        (context as? Activity)?.finish()
                        (context as? Activity)?.finishAffinity()
                        dialogLogin.dismiss()
                    }else{
                        password.error = "Contraseña incorrecta"
                    }

                }
            }
        }
        iconoCerrarLogin.setOnClickListener { dialogLogin.dismiss() }
        dialogLogin.show()
    }

    fun crearDialogoLoginMain(context: Context, activity: Activity, usersTEA: ArrayList<Usuario>?) {
        prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)

        val dialogLogin = Dialog(context)
        dialogLogin.setContentView(R.layout.dialogo_login)
        dialogLogin.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val password = dialogLogin.findViewById<TextInputLayout>(R.id.txt_Password)
        val btnAcceder = dialogLogin.findViewById<MaterialButton>(R.id.btn_login)
        val iconoCerrarLogin = dialogLogin.findViewById<ImageView>(R.id.icono_CerrarDialogo)

        btnAcceder.setOnClickListener {
            if (password.editText?.text.toString() == "") {
                password.error = "El campo no puede estar vacío"
            } else {
                val email = prefs.getString("email", "")
                if(email != null){
                    val idUser = prefs.getString("idUsuario", "")
                    val passwordCifrada = EncryptionUtils.getEncrypt(password.editText?.text.toString(), context, idUser!!)

                    if(gUsuario.checkCredentials(email, passwordCifrada, activity)){
                        val editor = prefs.edit()
                        editor.putBoolean("PlanificadorLogged", true)
                        if(usersTEA!!.size >1){
                            context.startActivity(Intent((context as? Activity)?.baseContext, MenuUserActivity::class.java))
                        }else{
                            editor.putString("idUsuarioTEA", usersTEA[0].id)
                            editor.putString("nombreUsuarioTEA", usersTEA[0].name)
                            editor.putString("imagenUsuarioTEA", CommonUtils.bitmapToByteArray(usersTEA[0].imagen!!).toPreservedString)
                            editor.putString("configPictogramas", usersTEA[0].configPictograma)
                            context.startActivity(Intent((context as? Activity)?.baseContext, EventosPlanificadorActivity::class.java))
                        }
                        editor.apply()
                        (context as? Activity)?.finish()
                        (context as? Activity)?.finishAffinity()
                        dialogLogin.dismiss()
                    }else{
                        password.error = "Contraseña incorrecta"
                    }

                }
            }
        }
        iconoCerrarLogin.setOnClickListener { dialogLogin.dismiss() }
        dialogLogin.show()
    }

    private fun cerrarSesion(fragment: Fragment){
        val dialogLogout = Dialog(fragment.requireContext())
        dialogLogout.setContentView(R.layout.dialogo_logout)
        dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnLogout = dialogLogout.findViewById<MaterialButton>(R.id.btn_logout)
        val iconoCerrarLogin = dialogLogout.findViewById<ImageView>(R.id.icono_CerrarDialogo)

        btnLogout.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(fragment.requireContext(), gso)
            googleSignInClient.signOut()

            val editor = prefs.edit()

            for (key in prefs.all.keys) {
                if (!(key.startsWith("initialization_vector") || key.startsWith("secret_key") || key.endsWith("FirstTime"))) {
                    editor.remove(key)
                }
            }
            editor.apply()
            fragment.requireContext().startActivity(Intent(fragment.activity?.baseContext, PreLoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            fragment.activity?.finish()
            fragment.activity?.finishAffinity()
        }

        iconoCerrarLogin.setOnClickListener { dialogLogout.dismiss() }
        dialogLogout.show()
    }

    private fun configurarDatos(vista: View){
        iconoRol = vista.findViewById(R.id.iconoRol)
        iconoRol.setImageURI(null)
        textoRol = vista.findViewById(R.id.textRol)
        val image = prefs.getString("imagenUsuarioTEA", "")
        if(image == ""){
            iconoRol.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenPlanificador", "")!!.toPreservedByteArray))
            textoRol.text = prefs.getString("nombrePlanificador", "")
        }else{
            iconoRol.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenUsuarioTEA", "")!!.toPreservedByteArray))
            textoRol.text = prefs.getString("nombreUsuarioTEA", "")
        }
    }

    fun hostingId(hostingActivityClass: Class<FragmentActivity>) : Int {
        val idActivity = when (hostingActivityClass) {
            MainActivity::class.java -> R.id.home
            EventosActivity::class.java -> R.id.planificacion
            EventosPlanificadorActivity::class.java -> R.id.planificacion
            TraductorActivity::class.java -> R.id.traductor
           // CalendarioActivity::class.java -> R.id.calendario
            CalendarioMensualActivity::class.java -> R.id.calendario
            ActividadActivity::class.java -> R.id.actividades
            SemanaActivity::class.java -> R.id.calendario
            PlanificacionesActivity::class.java -> R.id.planificacion
            // CuadernoActivity::class.java -> R.id.cuaderno
            else -> R.id.planificacion
        }
        return idActivity
    }

    private fun onNavigationItemSelected(itemId: Int, fragment: Fragment, currentActivity: Class<*>, isPlanificador: Boolean): Boolean {
        val targetActivityClass = when (itemId) {
            R.id.user -> ConfiguracionActivity::class.java
            R.id.home -> MainActivity::class.java
            R.id.actividades -> ActividadActivity::class.java
            R.id.traductor -> TraductorActivity::class.java
            R.id.calendario -> CalendarioMensualActivity::class.java
            R.id.planificacion -> if(isPlanificador) EventosPlanificadorActivity::class.java else EventosActivity::class.java
            else -> return true
        }

        if(itemId == R.id.planificacion && isPlanificador){
            val inflater = LayoutInflater.from(fragment.requireContext())
            val popupView = inflater.inflate(R.layout.popup_menu_plan, null)

            popupView.findViewById<MaterialCardView>(R.id.item_eventos).setOnClickListener {
                val intent = Intent(fragment.requireContext().applicationContext, targetActivityClass)
                fragment.requireContext().startActivity(intent)
                activePopupWindow?.dismiss()
            }

            popupView.findViewById<MaterialCardView>(R.id.item_planificaciones).setOnClickListener {
                val intent = Intent(fragment.requireContext().applicationContext, PlanificacionesActivity::class.java)
                fragment.requireContext().startActivity(intent)
                activePopupWindow?.dismiss()
            }

            activePopupWindow?.dismiss()
            activePopupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            if (fragment.requireContext().resources.configuration.orientation == 1) {
                val dimen = fragment.requireContext().resources.getDimensionPixelSize(R.dimen.popup_y)
                activePopupWindow?.showAtLocation(fragment.requireView(),  Gravity.START or Gravity.BOTTOM , 30, dpToPx(dimen, fragment.requireContext()))
            } else {
                activePopupWindow?.showAsDropDown(fragment.requireView().findViewById(itemId), 50, 0)
            }
            return true
        }

        if(itemId == R.id.calendario){
            val inflater = LayoutInflater.from(fragment.requireContext())
            val popupView = inflater.inflate(R.layout.popup_menu_calendario, null)

            popupView.findViewById<MaterialCardView>(R.id.item_mes).setOnClickListener {
                val intent = Intent(fragment.requireContext().applicationContext, targetActivityClass)
                fragment.requireContext().startActivity(intent)
                activePopupWindow?.dismiss()
            }

            popupView.findViewById<MaterialCardView>(R.id.item_semana).setOnClickListener {
                val intent = Intent(fragment.requireContext().applicationContext, SemanaActivity::class.java)
                fragment.requireContext().startActivity(intent)
                activePopupWindow?.dismiss()
            }

            activePopupWindow?.dismiss()
            activePopupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            if (fragment.requireContext().resources.configuration.orientation == 1) {
                //width of the screen
                val width = fragment.requireContext().resources.displayMetrics.widthPixels/5
                val dimen = fragment.requireContext().resources.getDimensionPixelSize(R.dimen.popup_y)
                activePopupWindow?.showAtLocation(fragment.requireView(), Gravity.START or Gravity.BOTTOM , width-30, dpToPx(dimen, fragment.requireContext()))
            } else {
                activePopupWindow?.showAsDropDown(fragment.requireView().findViewById(itemId), 50, 0)
            }

            return true
        }

        if (currentActivity == targetActivityClass) {
            return true
        }

        val intent = Intent(fragment.requireContext().applicationContext, targetActivityClass)
        fragment.requireContext().startActivity(intent)

        return true
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val resources = context.resources
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun restoreNavigationItemClicked(id: Int){
        val menu: Menu = navigationView.menu
        menu.findItem(id).isChecked = true
    }

    fun restoreNavigationItemClickedBottom(id: Int){
        val menu: Menu = navigationViewBottom.menu
        menu.findItem(id).isChecked = true
    }

    private fun animateNavigationViewWidth(targetWidth: Int) {
        val animator = ValueAnimator.ofInt(fragmentSide.width, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = fragmentSide.layoutParams as ViewGroup.LayoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            fragmentSide.layoutParams = layoutParams

            val layoutParams2 = navigationView.layoutParams as ViewGroup.LayoutParams
            layoutParams2.width = valueAnimator.animatedValue as Int
            navigationView.layoutParams = layoutParams2
        }

        animator.duration = 400
        animator.interpolator = PathInterpolator(0.05f, 0.7f, 0.1f, 1f)

        animator.start()
    }

    private fun setNavigationViewWidth(width: Int) {
        val layoutParams = navigationView.layoutParams as ViewGroup.LayoutParams
        layoutParams.width = width
        navigationView.layoutParams = layoutParams
    }

    fun inicializarVariables(view: View, fragment: Fragment, currentActivity: Class<*>, id:Int, isPlanificador: Boolean){
        val contextFragment = fragment.requireContext()
        buttonMenu = view.findViewById(R.id.item_menu)
        buttonAccount = view.findViewById(R.id.accountButton)
        navigationView = view.findViewById(R.id.navigationView)
        fragmentSide = view.findViewById(R.id.fragment_navigation_side)
        navigationView.setNavigationItemSelectedListener {item ->
            onNavigationItemSelected(item.itemId, fragment, currentActivity, isPlanificador)
            true
        }

        prefs = contextFragment.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        iconoRol = view.findViewById(R.id.iconoRol)
        textoRol = view.findViewById(R.id.textRol)

        if(isExpanded){
            val initialWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.new_navigation_width)
            setNavigationViewWidth(initialWidth)
        }else{
            val initialWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.old_navigation_width)
            setNavigationViewWidth(initialWidth)
        }

        configurarDatos(view)

        buttonMenu.setOnClickListener {

            val targetWidth: Int
            if(isExpanded){
                textoRol.visibility = View.GONE
                popupView?.visibility = View.INVISIBLE
                targetWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.old_navigation_width)
                buttonMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.svg_menu, 0, 0)
            }else{

                textoRol.visibility = View.VISIBLE
                targetWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.new_navigation_width)
                buttonMenu.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.svg_close, 0)
            }
            animateNavigationViewWidth(targetWidth)
            isExpanded = !isExpanded
        }

        val menu: Menu = navigationView.menu
        menu.findItem(id).isChecked = true
    }

    /*fun closeDrawer(fragment: Fragment){
        val contextFragment = fragment.requireContext()
        textoRol.visibility = View.GONE
        popupView?.visibility = View.INVISIBLE
        val targetWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.old_navigation_width)
        buttonMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.svg_menu, 0, 0)
        animateNavigationViewWidth(targetWidth)
        isExpanded = false

    }*/

    fun menuUsuario(fragment: Fragment, anchorView: View){
        val inflater = LayoutInflater.from(fragment.requireContext())
        prefs = fragment.requireContext().getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        val isUsuarioTEA = prefs.getBoolean("info_usuario", false)
        val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
        val customView = inflater.inflate(R.layout.popup_menu_usuario, null)
        val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        //si la pantalla esta en horizontal
        //case if its mobile, if its tablet and in portrait mode or if its tablet and in landscape mode

        val isPortrait = fragment.requireContext().resources.configuration.orientation == 1
        val isMobile = CommonUtils.isMobile(fragment.requireContext())

        when {
            isMobile -> {
                //width of the screen

                popupWindow.showAtLocation(anchorView, Gravity.END  or Gravity.BOTTOM, 30, 250)
            }
            isPortrait && !isMobile -> {
                popupWindow.showAtLocation(anchorView, Gravity.END  or Gravity.TOP, 20, 100)
            }
            else -> {
                popupWindow.showAtLocation(anchorView, Gravity.START  or Gravity.BOTTOM, 100, 120)
            }
        }
//        if(fragment.requireContext().resources.configuration.orientation == 1 && !CommonUtils.isMobile(fragment.requireContext())){
//            popupWindow.showAtLocation(anchorView, Gravity.END  or Gravity.TOP, 20, 100)
//        }else{
//            popupWindow.showAtLocation(anchorView, Gravity.START  or Gravity.BOTTOM, 100, 120)
//
//        }

        //Si estamos en el usuarioTEA
        if(!infoUsuario) {
            customView.findViewById<MaterialCardView>(R.id.item_user).visibility = View.GONE
            customView.findViewById<MaterialCardView>(R.id.item_cerarSesion).visibility = View.GONE
            customView.findViewById<MaterialDivider>(R.id.divider).visibility = View.GONE
            customView.findViewById<MaterialDivider>(R.id.divider2).visibility = View.GONE
        }

        //Si no existe usuario TEA
        if(!isUsuarioTEA && infoUsuario) {
            customView.findViewById<MaterialCardView>(R.id.item_cuenta).visibility = View.GONE
            customView.findViewById<View>(R.id.divider).visibility = View.GONE
        }

        customView.findViewById<MaterialCardView>(R.id.item_cuenta).setOnClickListener {
            if(isUsuarioTEA && infoUsuario){
                val editor = prefs.edit()
                editor.putBoolean("PlanificadorLogged", false)
                editor.apply()
                fragment.requireContext().startActivity(Intent(fragment.requireContext().applicationContext, EventosActivity::class.java))
                fragment.activity?.finish()
                fragment.activity?.finishAffinity()
            }else{
                crearDialogoLogin(fragment.requireContext(), fragment.requireActivity())
            }
        }

        if(Locale.getDefault().language == "es"){
            customView.findViewById<ImageView>(R.id.languageImage).setImageResource(R.drawable.ic_es)
            customView.findViewById<TextView>(R.id.languageText).text = "Español"
        }else if (Locale.getDefault().language == "en"){
            customView.findViewById<ImageView>(R.id.languageImage).setImageResource(R.drawable.ic_en)
            customView.findViewById<TextView>(R.id.languageText).text = "English"
        }

        customView.findViewById<MaterialCardView>(R.id.item_idioma).setOnClickListener {
            //change language
            if(Locale.getDefault().language == "es"){
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag("en")))
            }else{
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag("es")))
            }
        }

        customView.findViewById<MaterialCardView>(R.id.item_cerarSesion).setOnClickListener {
            cerrarSesion(fragment)
        }

        customView.findViewById<MaterialCardView>(R.id.item_user).setOnClickListener {
            fragment.requireContext().startActivity(Intent(fragment.requireContext().applicationContext, ConfiguracionActivity::class.java))
        }

        customView.findViewById<MaterialCardView>(R.id.item_ayuda).setOnClickListener {
            fragment.requireContext().startActivity(Intent(fragment.requireContext().applicationContext, ManualActivity::class.java))
        }

        popupWindow.showAsDropDown(anchorView)
    }

    fun inicializarVariablesBottom(view: View, fragment: Fragment, currentActivity: Class<*>, id: Int, isPlanificador: Boolean){
        navigationViewBottom = view.findViewById(R.id.bottom_navigation)
        navigationViewBottom.setOnItemSelectedListener { item ->
            if(item.itemId == R.id.user){
                menuUsuario(fragment, view)
            }else{
                onNavigationItemSelected(item.itemId, fragment, currentActivity, isPlanificador)
            }
            true
        }

        prefs = fragment.requireContext().getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        val menu: Menu = navigationViewBottom.menu
        menu.findItem(id).isChecked = true
    }

}