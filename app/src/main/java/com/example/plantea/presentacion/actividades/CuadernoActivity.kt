package com.example.plantea.presentacion.actividades

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.fragmentos.cuaderno.CuadernoPictoEditFragment
import com.example.plantea.presentacion.fragmentos.cuaderno.CuadernoPictogramasFragment
import com.example.plantea.presentacion.fragmentos.cuaderno.PrincipalFragment
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CuadernoActivity : AppCompatActivity() {
    //private var transaction: FragmentTransaction? = null
    //private var fragmentCuadernoPictogramas = CuadernoPictogramasFragment()
    private var atras : Button? = null
    private lateinit var transaction: FragmentTransaction
    var fragment = CuadernoPictoEditFragment()

    private val viewModel by viewModels<CuadernoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuaderno)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")

        if (idUsuario != null) {
            viewModel.listaCuadernos = viewModel.cuaderno.consultarCuadernos(this, idUsuario)
        }

        viewModel.listaCuadernos!!.removeAt(0)

        viewModel.isPlanificador = prefs.getBoolean("PlanificadorLogged", false)
        if(viewModel.isPlanificador){
            viewModel.listaCuadernos!!.add(Cuaderno(0, "AÑADIR CUADERNO", "archivo", false))
        }

        atras = findViewById(R.id.atras)

        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putSerializable("key", viewModel.listaCuadernos)
            bundle.putSerializable("isPlan", viewModel.isPlanificador)
            val fragmentPrincipal = PrincipalFragment()
            fragmentPrincipal.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_fragments, fragmentPrincipal)
            transaction.commit()
        } else {
            val fragment = supportFragmentManager.findFragmentById(R.id.layout_fragments)
            if(fragment is PrincipalFragment){
                val bundle = Bundle()
                bundle.putSerializable("key", viewModel.listaCuadernos)
                bundle.putSerializable("isPlan", viewModel.isPlanificador)
                fragment.arguments = bundle
            }else {
                transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.layout_fragments, fragment!!)
                transaction.commit()
            }
        }

        atras?.setOnClickListener {
            finish()
        }

        observers()
    }

    fun observers(){
        viewModel._cerrarFragment.observe(this) {
            val bundle = Bundle()
            val fragment = PrincipalFragment()
            bundle.putSerializable("key", viewModel.listaCuadernos)
            bundle.putSerializable("isPlan", viewModel.isPlanificador)
            fragment.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_fragments, fragment)
            transaction.commit()

        }

        viewModel._queryBusqueda.observe(this) {
            viewModel.listaPictogramas?.clear()
            getPictogramas(it)
            viewModel.isBusqueda = true
        }

        viewModel._pictoBusquedaAdded.observe(this) {
            viewModel.originalPictogramas?.add(it)
            it.id?.let { it1 -> viewModel.listaPictosAgregados.add(it1) }
            viewModel.picto.guardarPictoCuaderno(this, it.id, it.titulo, it.imagen, viewModel.idCuaderno)
        }

        viewModel._removePicto.observe(this) {
             if(viewModel.sourceAPI){
                viewModel.picto.borrarPictoCuadernoBusqueda(this, it.id, viewModel.idCuaderno)
            }else{
                viewModel.picto.borrarPictoCuaderno(this, it.id, viewModel.idCuaderno)
            }

            for (i in 0 until viewModel.listaPictosAgregados.size) {
                if (viewModel.listaPictosAgregados[i] == it.id) {
                    viewModel.listaPictosAgregados.removeAt(i)
                    break
                }
            }

            for (i in 0 until viewModel.originalPictogramas!!.size) {
                if (viewModel.originalPictogramas!![i].id == it.id) {
                    viewModel.originalPictogramas?.removeAt(i)
                    break
                }
            }

        }
    }


    private fun getPictogramas(query: String) {
        viewModel.isBusqueda = true
        viewModel.listaPictogramas?.clear()
        viewModel.listaPictosAgregados.clear()
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.mapNotNull { key ->
                    dict[key]?.let { (value, id) ->
                        viewModel.listaPictogramas?.add(crearPictoBusqueda(key, value, id))
                    }
                }
            }

            if (viewModel.listaPictogramas!!.isNotEmpty()) {
                val fragmentBusqueda = CuadernoPictoEditFragment()
                transaction = supportFragmentManager.beginTransaction()
                val bundle = Bundle()
                bundle.putSerializable("key",  viewModel.listaPictogramas)
                bundle.putSerializable("termometro", false)
                fragmentBusqueda.arguments = bundle
                transaction.replace(R.id.layout_fragments, fragmentBusqueda)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int): Pictograma{
        val tituloMayus = titulo?.uppercase()
        val archivo = CommonUtils.crearImagen(bitmap, titulo, this)
        val exite = viewModel.originalPictogramas?.find { it.id == id.toString() }
        if(exite != null){
            exite.id?.let { viewModel.listaPictosAgregados.add(it) }
        }
        return Pictograma(id.toString(), tituloMayus, archivo, 0, 0, favorito = false, sourceAPI = true)
    }

    fun iniciarFragment(pictogramas: ArrayList<Pictograma>?, termometro: Boolean?, tituloCuaderno: String, posicion: Int) {
        val bundle = Bundle()
        bundle.putSerializable("key", pictogramas ?: ArrayList<Pictograma>())
        bundle.putSerializable("termometro", termometro)
        bundle.putString("tituloCuaderno", tituloCuaderno)
        viewModel.tituloCuaderno = tituloCuaderno

         if (viewModel.isPlanificador && posicion > 2) {
             fragment = CuadernoPictoEditFragment()
             transaction = supportFragmentManager.beginTransaction()
             fragment.arguments = bundle
             transaction.replace(R.id.layout_fragments, fragment)
         } else {
            val fragment = CuadernoPictogramasFragment()
            fragment.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.layout_fragments, fragment)
        }

        transaction.addToBackStack(null)
        transaction.commit()
    }
}