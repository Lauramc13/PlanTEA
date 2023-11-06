package com.example.plantea.presentacion.actividades

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var popupWindow: PopupWindow
    lateinit var buttonMenu : ImageView
    lateinit var buttonAccount: LinearLayout
    lateinit var prefs: SharedPreferences
    private var isExpanded = true
    private lateinit var firebaseAuth: FirebaseAuth


    lateinit var btn_logout : Button
    var popupView : View? = null

    fun crearDialogoLogin(context: AppCompatActivity) {
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
                Toast.makeText(context.applicationContext, "Introduce la contraseña", Toast.LENGTH_LONG)
                    .show()
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
                            context.startActivity(Intent(context.baseContext, PlanActivity::class.java))
                            context.finish()
                            context.finishAffinity()
                            dialogLogin.dismiss()
                        }else{
                            Toast.makeText(context.applicationContext, "Error en la contraseña", Toast.LENGTH_LONG).show()

                        }
                    }

                }
            }
        }
        icono_cerrar_login.setOnClickListener { dialogLogin.dismiss() }
        dialogLogin.show()
    }

        fun hashPassword(password: String): String {
            val bytes = password.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("", { str, it -> str + "%02x".format(it) })
        }

        fun buttonAccountNavigation(context: AppCompatActivity, it: View, buttonAccount: LinearLayout){
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
                        context.startActivity(Intent(context.baseContext, PlanActivity::class.java))
                        context.finish()
                        context.finishAffinity()
                    }else{
                        crearDialogoLogin(context)
                    }
                }
            }

            val logout = popupView?.findViewById<LinearLayout>(R.id.logout)
            logout?.setOnClickListener{
                val dialogLogout = Dialog(context)
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
                        GoogleSignIn.getClient(context, gso)
                    googleSignInClient.signOut()
                    prefs.edit().clear().apply()
                    dialogLogout.dismiss()
                    context.startActivity(Intent(context.baseContext, PreLoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                    context.finish()
                    context.finishAffinity()
                }
                icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                dialogLogout.show()

            }
        }

    fun configurarDatos(context: AppCompatActivity, id: Int){

        val info_usuario = prefs.getBoolean("PlanificadorLogged", false)

        navigationView = context.findViewById(R.id.navigationView)

        iconoRol = navigationView.findViewById(R.id.iconoRol)
        textoRol = navigationView.findViewById(R.id.textRol)
        navigationView.menu.clear()
        if (info_usuario) {
            navigationView.inflateMenu(R.menu.navigation_rail_menu)
            textoRol.text = prefs.getString("nombrePlanificador", "")!!.uppercase(Locale.getDefault())
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
        } else {
            navigationView.inflateMenu(R.menu.navigation_rail_menu_tea)
            textoRol.text = prefs.getString("nombreUsuarioTEA", "")!!.uppercase(Locale.getDefault())
            iconoRol.setImageURI(Uri.parse(prefs.getString("imagenUsuarioTEA", "")))
        }

        val isUsuarioTEA = prefs.getBoolean("info_usuario", false)

        if (isUsuarioTEA) {
            popupView = LayoutInflater.from(context).inflate(R.layout.header_navigation_drawer_tea, null)
        }else{
            popupView = LayoutInflater.from(context).inflate(R.layout.header_navigation_drawer, null)
        }
        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupView?.visibility = View.GONE

        var menu: Menu = navigationView.menu
        menu.findItem(id).isChecked = true

    }

    fun onNavigationItemSelected(itemId: Int, context: AppCompatActivity, currentActivity: Class<*>): Boolean {
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
        context.startActivity(Intent(context.applicationContext, targetActivityClass))
        return true
    }

    fun animateNavigationViewWidth(targetWidth: Int) {
        val animator = ValueAnimator.ofInt(navigationView.width, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = navigationView.layoutParams as ViewGroup.LayoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            navigationView.layoutParams = layoutParams
        }
        animator.duration = 300
        animator.start()
    }

    fun setNavigationViewWidth(width: Int) {
        val layoutParams = navigationView.layoutParams as ViewGroup.LayoutParams
        layoutParams.width = width
        navigationView.layoutParams = layoutParams
    }

    fun inicializarVariables(context: AppCompatActivity, id:Int, currentActivity: Class<*>){
        buttonMenu = context.findViewById(R.id.item_menu)
        navigationView = context.findViewById(R.id.navigationView)
        buttonAccount = navigationView.findViewById(R.id.accountButton)


        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener { item ->
                onNavigationItemSelected(item.itemId, context, currentActivity)
            }
        }

        prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)

        iconoRol = navigationView.findViewById(R.id.iconoRol)
        textoRol = navigationView.findViewById(R.id.textRol)

        val initialWidth = context.resources.getDimensionPixelSize(R.dimen.old_navigation_width)
        setNavigationViewWidth(initialWidth)
        configurarDatos(context, id)

        buttonMenu.setOnClickListener {
            var targetWidth: Int
            if(isExpanded){
                textoRol.visibility = View.VISIBLE
                targetWidth = context.resources.getDimensionPixelSize(R.dimen.new_navigation_width)
                buttonMenu.setImageResource(R.drawable.svg_close)
            }else{
                textoRol.visibility = View.GONE
                popupView?.visibility = View.INVISIBLE
                targetWidth = context.resources.getDimensionPixelSize(R.dimen.old_navigation_width)
                buttonMenu.setImageResource(R.drawable.svg_menu)
            }
            animateNavigationViewWidth(targetWidth)
            isExpanded = !isExpanded
        }

        buttonAccount.setOnClickListener {
            buttonAccountNavigation(context, it, buttonAccount)
        }
    }

    fun destroyPopup(){
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }


}