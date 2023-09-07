package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.GestionNavegacion
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CuadernoInterface
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
    private var navigationHandler = GestionNavegacion()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        // Comprobamos la orientacion de la pantalla
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        navigationHandler.configurarDatos(this, R.id.cuaderno)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationHandler.destroyPopup()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuaderno)
        navigationHandler.inicializarVariables(this, R.id.cuaderno, CuadernoActivity::class.java)
        val backButton: Button = findViewById(R.id.goBackButton)

        listaPictogramas = ArrayList()
        listaEscala = ArrayList()
        fragmentCuadernoPictogramas = CuadernoPictogramasFragment()
        if (savedInstanceState == null) {
            // La primera vez que se crea la actividad, se añade el fragmento principal
            fragmentPrincipal = PrincipalFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.add(R.id.layout_fragments, fragmentPrincipal as PrincipalFragment)
            transaction!!.commit()
        } else {
            // La actividad se está recreando, por lo que se recupera el fragmento existente del FragmentManager
            val existingFragment = supportFragmentManager.findFragmentById(R.id.layout_fragments)
            fragmentPrincipal = if (existingFragment is PrincipalFragment) {
                existingFragment
            } else {
                PrincipalFragment()
            }
        }

        //Pictogramas en la parte de arriba del cuaderno
        iniciarListaEscala()

        backButton.setOnClickListener{
            finish()
        }
    }

    //Menu principal

    private fun iniciarListaEscala() {
        //picto = Pictograma()
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
       // picto = Pictograma()
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
        val btnCerrar : ImageView = dialog.findViewById(R.id.icono_CerrarDialogoEvento)
        btnCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
}