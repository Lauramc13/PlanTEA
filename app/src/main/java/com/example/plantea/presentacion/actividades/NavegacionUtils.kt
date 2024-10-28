package com.example.plantea.presentacion.actividades

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.viewModels.EventosPlanificadorViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale


class NavegacionUtils {
    var usuario = Usuario()
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

    fun crearDialogoLogin(context: Context, activity: Activity) {
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
                    val passwordCifrada = EncryptionUtils.getEncrypt(password.editText?.text.toString(), context)

                    if(usuario.checkCredentials(email, passwordCifrada, activity)){
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
                    val passwordCifrada = EncryptionUtils.getEncrypt(password.editText?.text.toString(), context)

                    if(usuario.checkCredentials(email, passwordCifrada, activity)){
                        val editor = prefs.edit()
                        editor.putBoolean("PlanificadorLogged", true)
                        if(usersTEA!!.size >1){
                            context.startActivity(Intent((context as? Activity)?.baseContext, MenuUserActivity::class.java))
                        }else{
                            editor.putString("idUsuarioTEA", usersTEA[0].id)
                            editor.putString("nombreUsuarioTEA", usersTEA[0].name)
                            editor.putString("imagenUsuarioTEA", usersTEA[0].imagen)
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
            //prefs.edit().clear().apply()
            dialogLogout.dismiss()
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
       /* val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
        if (infoUsuario) {
            textoRol.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
        } else {
            textoRol.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
        }*/
        val image = prefs.getString("imagenUsuarioTEA", "")
        if(image == ""){
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
        }else{
            iconoRol.setImageURI(Uri.parse(image))

        }

    }

    fun hostingId(hostingActivityClass: Class<FragmentActivity>) : Int {
        val idActivity = when (hostingActivityClass) {
            MainActivity::class.java -> R.id.home
            EventosActivity::class.java -> R.id.planificacion
            EventosPlanificadorActivity::class.java -> R.id.planificacion
            TraductorActivity::class.java -> R.id.traductor
            CalendarioActivity::class.java -> R.id.calendar
            ActividadActivity::class.java -> R.id.actividades
            SemanaActivity::class.java -> R.id.semana
            PlanificacionesActivity::class.java -> R.id.calendar
            // CuadernoActivity::class.java -> R.id.cuaderno
            else -> R.id.planificacion
        }
        return idActivity
    }

    private fun onNavigationItemSelected(itemId: Int, fragment: Fragment, currentActivity: Class<*>, isPlanificador: Boolean): Boolean {
        val targetActivityClass = when (itemId) {
            R.id.home -> MainActivity::class.java
            R.id.calendar -> CalendarioActivity::class.java
            R.id.planificacion -> if (isPlanificador) EventosPlanificadorActivity::class.java else EventosActivity::class.java
            R.id.actividades -> ActividadActivity::class.java
            R.id.semana -> SemanaActivity::class.java
            // R.id.cuaderno -> CuadernoActivity::class.java
            //R.id.user -> ConfiguracionActivity::class.java
            //R.id.help -> ManualActivity::class.java
            R.id.traductor -> TraductorActivity::class.java
            else -> return true
        }

        if (currentActivity == targetActivityClass) {
            return true
        }

        val intent = Intent(fragment.requireContext().applicationContext, targetActivityClass)
        fragment.requireContext().startActivity(intent)

        return true
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
        if(fragment.requireContext().resources.configuration.orientation == 1){
            popupWindow.showAtLocation(anchorView, Gravity.END  or Gravity.TOP, 20, 100)
        }else{
            popupWindow.showAtLocation(anchorView, Gravity.START  or Gravity.BOTTOM, 100, 120)

        }

        //Si estamos en el usuarioTEA
        if(!infoUsuario) {
            customView.findViewById<LinearLayout>(R.id.item_user).visibility = View.GONE
            customView.findViewById<LinearLayout>(R.id.item_cerarSesion).visibility = View.GONE
            customView.findViewById<View>(R.id.divider).visibility = View.GONE
            customView.findViewById<View>(R.id.divider2).visibility = View.GONE
        }

        //Si no existe usuario TEA
        if(!isUsuarioTEA && infoUsuario) {
            customView.findViewById<LinearLayout>(R.id.item_cuenta).visibility = View.GONE
            customView.findViewById<View>(R.id.divider).visibility = View.GONE
        }

        customView.findViewById<LinearLayout>(R.id.item_cuenta).setOnClickListener {
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

        customView.findViewById<LinearLayout>(R.id.item_cerarSesion).setOnClickListener {
            cerrarSesion(fragment)
        }

        customView.findViewById<LinearLayout>(R.id.item_user).setOnClickListener {
            fragment.requireContext().startActivity(Intent(fragment.requireContext().applicationContext, ConfiguracionActivity::class.java))
        }

        customView.findViewById<LinearLayout>(R.id.item_ayuda).setOnClickListener {
            fragment.requireContext().startActivity(Intent(fragment.requireContext().applicationContext, ManualActivity::class.java))
        }

        popupWindow.showAsDropDown(anchorView)
    }



    fun inicializarVariablesBottom(view: View, fragment: Fragment, currentActivity: Class<*>, id: Int, isPlanificador: Boolean){
        navigationViewBottom = view.findViewById(R.id.bottom_navigation)
        navigationViewBottom.setOnItemSelectedListener { item ->
            onNavigationItemSelected(item.itemId, fragment, currentActivity, isPlanificador)
            true
        }

        prefs = fragment.requireContext().getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        val menu: Menu = navigationViewBottom.menu
        menu.findItem(id).isChecked = true
    }

    /*fun destroyPopup(){
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }*/


}