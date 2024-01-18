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
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.ninio.ActividadActivity
import com.example.plantea.presentacion.actividades.ninio.CuadernoActivity
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.example.plantea.presentacion.actividades.ninio.TraductorActivity
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest
import java.util.*

class NavegacionUtils {
    var usuario = Usuario()
    private lateinit var password: TextInputLayout
    private lateinit var btn_acceder: Button
    private lateinit var icono_cerrar_login: ImageView
    lateinit var iconoRol: ImageView
    lateinit var textoRol: TextView
    lateinit var navigationView: NavigationView
    lateinit var navigationViewBottom: BottomNavigationView
    lateinit var popupWindow: PopupWindow
    lateinit var buttonMenu : ImageView
    lateinit var buttonAccount: LinearLayout
    lateinit var prefs: SharedPreferences
    private var isExpanded = true
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var btn_logout : Button
    var popupView : View? = null

    fun crearDialogoLogin(context: Context) {
        prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        val dialogLogin = Dialog(context)
        dialogLogin.setContentView(R.layout.dialogo_login)
        dialogLogin.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        password = dialogLogin.findViewById(R.id.txt_Password)
        btn_acceder = dialogLogin.findViewById(R.id.btn_login)
        icono_cerrar_login = dialogLogin.findViewById(R.id.icono_CerrarDialogo)
        btn_acceder.setOnClickListener {
            if (password.editText?.text.toString() == "") {
                password.error = "El campo no puede estar vacío"

            } else {
                val email = prefs.getString("email", "")
                if(email != null){
                   // val passCifrada = hashPassword(password.editText?.text.toString())
                    //val passCorrecta = usuario.comprobarPass(email, passCifrada, context)
                    firebaseAuth.signInWithEmailAndPassword(email, password.editText?.text.toString())
                        .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val editor = prefs.edit()
                            editor.putBoolean("PlanificadorLogged", true)
                            editor.apply()
                            context.startActivity(Intent((context as? Activity)?.baseContext, PlanActivity::class.java))
                            (context as? Activity)?.finish()
                            (context as? Activity)?.finishAffinity()
                            dialogLogin.dismiss()
                        }else{
                            password.error = "La contraseña introducida no es correcta"
                        }
                    }

                }
            }
        }
        icono_cerrar_login.setOnClickListener { dialogLogin.dismiss() }
        dialogLogin.show()
    }

       /* fun hashPassword(password: String): String {
            val bytes = password.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("", { str, it -> str + "%02x".format(it) })
        }*/

        private fun buttonAccountNavigation(fragment: Fragment, it: View, buttonAccount: LinearLayout){
            if (popupView?.visibility == View.GONE){
                popupView!!.visibility = View.VISIBLE
            }else{
                popupView?.visibility = View.GONE
            }

            val buttonLocation = IntArray(2)
            it.getLocationOnScreen(buttonLocation)
            val x = buttonLocation[0] + buttonAccount.width / 2 + 50
            val y = buttonLocation[1] - 50
            popupWindow.elevation = 3f

            popupWindow.update(x, y, -1, -1)
            popupWindow.showAtLocation(it, Gravity.NO_GRAVITY, x, y)

            val isUsuarioTEA = prefs.getBoolean("info_usuario", false)

            if(isUsuarioTEA){
                val rol = popupView?.findViewById<LinearLayout>(R.id.cambiarRol)
                rol?.setOnClickListener{
                    val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)
                    if(infoUsuario){
                        val editor = prefs.edit()
                        editor.putBoolean("PlanificadorLogged", false)
                        editor.apply()
                        popupView?.visibility = View.INVISIBLE
                        fragment.requireContext().startActivity(Intent(fragment.activity?.baseContext, PlanActivity::class.java))
                        fragment.activity?.finish()
                        fragment.activity?.finishAffinity()
                    }else{
                        crearDialogoLogin(fragment.requireContext())
                    }
                }
            }

            val logout = popupView?.findViewById<LinearLayout>(R.id.logout)
            logout?.setOnClickListener{
                cerrarSesion(fragment)
            }
        }

    fun cerrarSesion(fragment: Fragment){
        val dialogLogout = Dialog(fragment.requireContext())
        dialogLogout.setContentView(R.layout.dialogo_logout)
        dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_logout = dialogLogout.findViewById(R.id.btn_logout)
        icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
        btn_logout.setOnClickListener {
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()

            val googleSignInClient: GoogleSignInClient =
                GoogleSignIn.getClient(fragment.requireContext(), gso)
            googleSignInClient.signOut()
            //prefs.edit().clear().apply()
            dialogLogout.dismiss()
            fragment.requireContext().startActivity(Intent(fragment.activity?.baseContext, PreLoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            fragment.activity?.finish()
            fragment.activity?.finishAffinity()
        }
        icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
        dialogLogout.show()
    }

    fun configurarDatos(vista: View, fragment: Fragment, id: Int){
        val infoUsuario = prefs.getBoolean("PlanificadorLogged", false)

        iconoRol = vista.findViewById(R.id.iconoRol)
        textoRol = vista.findViewById(R.id.textRol)
        if (infoUsuario) {
            textoRol.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
        } else {
            textoRol.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
        }

        val isUsuarioTEA = prefs.getBoolean("info_usuario", false)
        if (isUsuarioTEA) {
            popupView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.header_navigation_drawer_tea, null)
        }else{
            popupView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.header_navigation_drawer, null)
        }
        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupView?.visibility = View.GONE

    }

    fun hostingId(hostingActivityClass: Class<FragmentActivity>) : Int {
        val idActivity = when (hostingActivityClass) {
            MainActivity::class.java -> R.id.home
            PlanActivity::class.java -> R.id.planificacion
            TraductorActivity::class.java -> R.id.traductor
            CalendarioActivity::class.java -> R.id.calendar
            ActividadActivity::class.java -> R.id.actividades
            CuadernoActivity::class.java -> R.id.cuaderno
            else -> R.id.planificacion
        }
        return idActivity
    }

    private fun onNavigationItemSelected(itemId: Int, fragment: Fragment, currentActivity: Class<*>): Boolean {
        val targetActivityClass = when (itemId) {
            R.id.home -> MainActivity::class.java
            R.id.calendar -> CalendarioActivity::class.java
            R.id.planificacion -> PlanActivity::class.java
            R.id.actividades -> ActividadActivity::class.java
            R.id.cuaderno -> CuadernoActivity::class.java
            R.id.user -> ConfiguracionActivity::class.java
            R.id.help -> ManualActivity::class.java
            R.id.traductor -> TraductorActivity::class.java
            else -> return true
        }
        if (currentActivity == targetActivityClass) {
            return true
        }
        fragment.startActivity(Intent(fragment.requireContext().applicationContext, targetActivityClass))
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
        val animator = ValueAnimator.ofInt(navigationView.width, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = navigationView.layoutParams as ViewGroup.LayoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            navigationView.layoutParams = layoutParams
        }
        animator.duration = 300
        animator.start()
    }

    private fun setNavigationViewWidth(width: Int) {
        val layoutParams = navigationView.layoutParams as ViewGroup.LayoutParams
        layoutParams.width = width
        navigationView.layoutParams = layoutParams
    }

    fun inicializarVariables(view: View, fragment: Fragment, currentActivity: Class<*>, id:Int){
        val contextFragment = fragment.requireContext()
        buttonMenu = view.findViewById(R.id.item_menu)
        buttonAccount = view.findViewById(R.id.accountButton)

        navigationView = view.findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener {item ->
            onNavigationItemSelected(item.itemId, fragment, currentActivity)
            true
        }

        prefs = contextFragment.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        iconoRol = view.findViewById(R.id.iconoRol)
        textoRol = view.findViewById(R.id.textRol)

        val initialWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.old_navigation_width)
        setNavigationViewWidth(initialWidth)
        configurarDatos(view, fragment, id)

        buttonMenu.setOnClickListener {
            var targetWidth: Int
            if(isExpanded){
                textoRol.visibility = View.VISIBLE
                targetWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.new_navigation_width)
                buttonMenu.setImageResource(R.drawable.svg_close)
            }else{
                textoRol.visibility = View.GONE
                popupView?.visibility = View.INVISIBLE
                targetWidth = contextFragment.resources.getDimensionPixelSize(R.dimen.old_navigation_width)
                buttonMenu.setImageResource(R.drawable.svg_menu)
            }
            animateNavigationViewWidth(targetWidth)
            isExpanded = !isExpanded
        }

        buttonAccount.setOnClickListener {
            buttonAccountNavigation(fragment, it, buttonAccount)
        }
        val menu: Menu = navigationView.menu
        menu.findItem(id).isChecked = true
    }



    fun inicializarVariablesBottom(view: View, fragment: Fragment, currentActivity: Class<*>, id: Int){
        navigationViewBottom = view.findViewById(R.id.bottom_navigation)
        navigationViewBottom.setOnItemSelectedListener { item ->
            onNavigationItemSelected(item.itemId, fragment, currentActivity)
            true
        }

        prefs = fragment.requireContext().getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        val menu: Menu = navigationViewBottom.menu
        menu.findItem(id).isChecked = true
    }

    fun destroyPopup(){
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }


}