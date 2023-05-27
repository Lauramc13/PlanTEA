package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CuadernoInterface
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PreLoginActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCuadernoActivity
import com.example.plantea.presentacion.fragmentos.cuaderno.CuadernoPictogramasFragment
import com.example.plantea.presentacion.fragmentos.cuaderno.PrincipalFragment
import com.google.android.material.imageview.ShapeableImageView

class CuadernoActivity : AppCompatActivity(), CuadernoInterface, AdaptadorCuadernoActivity.OnItemSelectedListener {
    private var transaction: FragmentTransaction? = null
    private var fragmentPrincipal: Fragment? = null
    private var fragmentCuadernoPictogramas: Fragment? = null
    var listaPictogramas: ArrayList<Pictograma>? = null
    private var listaEscala: ArrayList<Pictograma>? = null
    private var recyclerView: RecyclerView? = null
    private var adaptador: AdaptadorCuadernoActivity? = null
    private var picto = Pictograma()
    lateinit var btn_cerrar : ImageView
    lateinit var btn_logout: Button
    private lateinit var icono_cerrar_login: ImageView
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuaderno)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        listaPictogramas = ArrayList()
        listaEscala = ArrayList()
        fragmentCuadernoPictogramas = CuadernoPictogramasFragment()
        if (savedInstanceState == null) {
            // Activity is not being recreated, so create a new instance of PrincipalFragment
            fragmentPrincipal = PrincipalFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.add(R.id.layout_fragments, fragmentPrincipal as PrincipalFragment)
            transaction!!.commit()
        } else {
            // Activity is being recreated, so retrieve the existing fragment from the FragmentManager
            val existingFragment = supportFragmentManager.findFragmentById(R.id.layout_fragments)
            fragmentPrincipal = if (existingFragment is PrincipalFragment) {
                existingFragment
            } else {
                PrincipalFragment()
            }
        }
        //Pictogramas en la parte de arriba del cuaderno
        iniciarListaEscala()
    }

    //Menu principal

    private fun iniciarListaEscala() {
        picto = Pictograma()
        listaEscala = picto.obtenerPictogramasCuaderno(this, 1) as ArrayList<Pictograma>?
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView = findViewById(R.id.lst_escala)
        if(recyclerView is RecyclerView){
            recyclerView!!.layoutManager = layoutManager
            adaptador = AdaptadorCuadernoActivity(listaEscala, this)
            recyclerView!!.adapter = adaptador
        }

    }

    override fun mostrarPictogramas(identificador: Int) {
        picto = Pictograma()
        listaPictogramas = picto.obtenerPictogramasCuaderno(this, identificador) as ArrayList<Pictograma>?
        iniciarFragment(listaPictogramas)
    }

    //Método para cerrar fragment correspondiente
    override fun cerrarFragment() {
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.replace(R.id.layout_fragments, fragmentPrincipal!!)
        transaction!!.addToBackStack(null)
        transaction!!.commit()
    }

    private fun iniciarFragment(pictogramas: ArrayList<*>?) {
        val bundle = Bundle()
        bundle.putSerializable("key", pictogramas)
        fragmentCuadernoPictogramas!!.arguments = bundle
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.replace(R.id.layout_fragments, fragmentCuadernoPictogramas!!)
        transaction!!.addToBackStack(null) // Se añade a la pila para poder navegar hacia atrás
        transaction!!.commit()
    }

    override fun pictogramaCuaderno(posicion: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_presentacion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        pictograma.setImageURI(Uri.parse(listaEscala!![posicion].imagen))
        tituloPictograma.text = listaEscala!![posicion].titulo

        val historia = dialog.findViewById<ConstraintLayout>(R.id.Bubble)
        historia.visibility = View.GONE


        //Botón cerrar
        btn_cerrar = dialog.findViewById(R.id.icono_CerrarDialogoEvento)
        btn_cerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
            R.id.item_perfil -> {
                val popupMenu = PopupMenu(this@CuadernoActivity, findViewById(R.id.item_ayuda) )
                popupMenu.inflate(R.menu.popup_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_1 -> {
                            val perfil = Intent(applicationContext, ConfiguracionActivity::class.java)
                            startActivity(perfil)
                            true
                        }
                        // R.id.option_2 -> {
                        //     val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                        //     val isPlanificadorLogged = prefs.getBoolean("PlanificadorLogged", false)
                        //     if(isPlanificadorLogged){
                        //         val editor = prefs.edit()
                        //         editor.putBoolean("PlanificadorLogged", false)
                        //         editor.commit()
                        //         val plan = Intent(applicationContext, PlanActivity::class.java)
                        //         startActivity(plan)
                        //     }else{
                        //         crearDialogoLogin()
                        //     }
                        //     true
                        // }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                val editor = prefs.edit()
                                editor.putBoolean("userAccount", false)
                                editor.apply()
                                val login = Intent(applicationContext, PreLoginActivity::class.java)
                                startActivity(login)
                            }
                            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                            dialogLogout.show()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            android.R.id.home -> finish()
        }
        return true
    }
}