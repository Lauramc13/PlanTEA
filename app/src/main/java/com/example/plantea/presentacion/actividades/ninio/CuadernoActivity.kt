package com.example.plantea.presentacion.actividades.ninio

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.CuadernoInterface
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.fragmentos.cuaderno.CuadernoPictoEditFragment
import com.example.plantea.presentacion.fragmentos.cuaderno.CuadernoPictogramasFragment
import com.example.plantea.presentacion.fragmentos.cuaderno.PrincipalFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class CuadernoActivity : AppCompatActivity(), CuadernoInterface  {
    private var transaction: FragmentTransaction? = null
    private var fragmentPrincipal: Fragment? = null
    private var fragmentCuadernoPictogramas: Fragment? = null
    private var fragmentCuadernoPictoEdit: CuadernoPictoEditFragment? = null
    var listaPictogramas: ArrayList<Pictograma>? = null
    private var originalPictogramas: ArrayList<Pictograma>? = null
    private var listaPictosAgregados: ArrayList<String> = ArrayList()
    private var idCuaderno: Int = 0

    var listaCuadernos: ArrayList<Cuaderno>? = null
    private var listaEscala: ArrayList<Pictograma>? = null

    private var picto = Pictograma()
    private var navigationHandler = NavegacionUtils()
    private var cuaderno = Cuaderno()
    private var isPlanificador = true
    private var isBusqueda = false

    companion object {
        const val ISBUSQUEDA_KEY = false
        val PICTOGRAMS_KEY = ArrayList<Pictograma>()
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ISBUSQUEDA_KEY.toString(), isBusqueda)
        outState.putSerializable(PICTOGRAMS_KEY.toString(), originalPictogramas)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        originalPictogramas = savedInstanceState.getSerializable(PICTOGRAMS_KEY.toString()) as ArrayList<Pictograma>?

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuaderno)
        navigationHandler.inicializarVariables(this, R.id.cuaderno, CuadernoActivity::class.java)
        val backButton: Button = findViewById(R.id.goBackButton)

        listaCuadernos = ArrayList()
        listaEscala = ArrayList()
        fragmentCuadernoPictogramas = CuadernoPictogramasFragment()
        fragmentCuadernoPictoEdit = CuadernoPictoEditFragment()
        fragmentPrincipal = PrincipalFragment()

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")


        if (idUsuario != null) {
            listaCuadernos = cuaderno.consultarCuadernos(this, idUsuario)
        }
        listaCuadernos!!.removeAt(0)

        isPlanificador = prefs.getBoolean("PlanificadorLogged", false)
        if(isPlanificador){
            listaCuadernos!!.add(Cuaderno(0, "AÑADIR CUADERNO", "archivo", false))
        }

        if (savedInstanceState == null) {
            // La primera vez que se crea la actividad, se añade el fragmento principal
            val bundle = Bundle()
            bundle.putSerializable("key", listaCuadernos)
            bundle.putSerializable("isPlan", isPlanificador)
            fragmentPrincipal!!.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.add(R.id.layout_fragments, fragmentPrincipal as PrincipalFragment)
            transaction!!.commit()
        } else {
            // Si la actividad se ha recreado, se recupera el fragmento correspondiente
            fragmentPrincipal = supportFragmentManager.findFragmentById(R.id.layout_fragments)
            if( fragmentPrincipal is PrincipalFragment){
                val bundle = Bundle()
                bundle.putSerializable("key", listaCuadernos)
                bundle.putSerializable("isPlan", isPlanificador)
                fragmentPrincipal!!.arguments = bundle
            }else{
                iniciarFragment(originalPictogramas, false, "")
            }
        }

        //Pictogramas en la parte de arriba del cuaderno
        //iniciarListaEscala()

        backButton.setOnClickListener{
            finish()
        }
    }

    //Menu principal
/*
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

    }*/

    override fun mostrarPictogramas(identificador: Int, termometro: Boolean?, tituloCuaderno: String) {
        listaPictogramas = picto.obtenerPictogramasCuaderno(this, identificador) as ArrayList<Pictograma>?
        idCuaderno = identificador
        originalPictogramas = listaPictogramas?.let { ArrayList(it) }
        iniciarFragment(listaPictogramas, termometro, tituloCuaderno)
    }

    override fun mostrarPictogramasBusqueda(query: String) {
        listaPictogramas?.clear()
        getPictogramas(query)
        isBusqueda = true
    }

    override fun addPictoFromBusqueda(pictograma: Pictograma){
        //for picto in originalPictramas log titles
        originalPictogramas?.add(pictograma)
        picto.guardarPictoCuaderno(this, pictograma.id, pictograma.titulo, pictograma.imagen, idCuaderno)
        //fragmentCuadernoPictoEdit!!.updateData(originalPictogramas)
    }

    override fun removePicto(pictograma: Pictograma, sourceAPI: Boolean, isBusqueda: Boolean){
        if(sourceAPI){
            picto.borrarPictoCuadernoBusqueda(this, pictograma.id, idCuaderno)
        }else{
            picto.borrarPictoCuaderno(this, pictograma.id, idCuaderno)
        }
        originalPictogramas?.removeIf { it.id == pictograma.id }
        if(!isBusqueda){
            fragmentCuadernoPictoEdit!!.updateDataRemove(pictograma)
        }
    }

    private fun getPictogramas(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.forEach { key ->
                    dict[key]?.let { (value, id) ->
                        crearPictoBusqueda(key, value, id)
                        fragmentCuadernoPictoEdit!!.mostrarPictogramasBusqueda(listaPictogramas, listaPictosAgregados)
                    }
                }
            }
        }
    }

    private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int) {
        val tituloMayus = titulo?.uppercase()
        val archivo = CommonUtils.crearImagen(bitmap, titulo, this)
        val exite = originalPictogramas?.find { it.id == id.toString() }
        if(exite != null){
            exite.id?.let { listaPictosAgregados.add(it) }
        }
        listaPictogramas?.add(Pictograma(id.toString(), tituloMayus, archivo, 0, 0, false, true))
    }

    //Método para cerrar fragment correspondiente
    override fun cerrarFragment() {
        val bundle = Bundle()
        bundle.putSerializable("key", listaCuadernos)
        bundle.putSerializable("isPlan", isPlanificador)
        fragmentPrincipal!!.arguments = bundle
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.replace(R.id.layout_fragments, fragmentPrincipal!!)
        transaction!!.addToBackStack(null)
        transaction!!.commit()
    }

    override fun atrasFragment() {
        // if fragment exists do updateData, else do newFragment
        originalPictogramas?.let { fragmentCuadernoPictoEdit!!.updateData(it) }
        isBusqueda = false
    }

    override fun addPictoPersonalizado(newPictograma: Pictograma){
        originalPictogramas?.add(newPictograma)
    }

    private fun iniciarFragment(pictogramas: ArrayList<Pictograma>?, termometro: Boolean?, tituloCuaderno: String) {
        val bundle = Bundle()
        bundle.putSerializable("key", pictogramas)
        bundle.putSerializable("termometro", termometro)
        bundle.putSerializable("tituloCuaderno", tituloCuaderno)
        bundle.putSerializable("idCuaderno", idCuaderno)

        if(isPlanificador){
            fragmentCuadernoPictoEdit!!.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.layout_fragments, fragmentCuadernoPictoEdit!!)
        }else{
            fragmentCuadernoPictogramas!!.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.layout_fragments, fragmentCuadernoPictogramas!!)
        }
        transaction!!.addToBackStack(null)
        transaction!!.commit()
    }

    /*
        override fun pictogramaCuaderno(posicion: Int) {
            /*val dialog = Dialog(this)
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
            dialog.show()*/
        }*/


}