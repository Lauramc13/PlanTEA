package com.example.plantea.presentacion.actividades

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
    private var transaction: FragmentTransaction? = null
    private var fragmentPrincipal: Fragment? = null
    private var fragmentCuadernoPictogramas: Fragment? = null
    private var fragmentCuadernoPictoEdit: CuadernoPictoEditFragment? = null

    private val viewModel by viewModels<CuadernoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuaderno)

        fragmentCuadernoPictogramas = CuadernoPictogramasFragment()
        fragmentCuadernoPictoEdit = CuadernoPictoEditFragment()
        fragmentPrincipal = PrincipalFragment()

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

        if (savedInstanceState == null) {
            // La primera vez que se crea la actividad, se añade el fragmento principal
            val bundle = Bundle()
            bundle.putSerializable("key", viewModel.listaCuadernos)
            bundle.putSerializable("isPlan", viewModel.isPlanificador)
            fragmentPrincipal!!.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.add(R.id.layout_fragments, fragmentPrincipal as PrincipalFragment)
            transaction!!.commit()
        } else {
            val fragment = supportFragmentManager.findFragmentById(R.id.layout_fragments)
            if(fragment is PrincipalFragment){
                val bundle = Bundle()
                bundle.putSerializable("key", viewModel.listaCuadernos)
                bundle.putSerializable("isPlan", viewModel.isPlanificador)
                fragment.arguments = bundle
            }else {
                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.layout_fragments, fragment!!)
                transaction!!.commit()
            }
        }

        observers()
    }

    fun observers(){
        viewModel._posicionPictoClicked.observe(this) {position ->
            viewModel.listaPictogramas = viewModel.picto.obtenerPictogramasCuaderno(this, viewModel.idCuaderno) as ArrayList<Pictograma>?
            viewModel.originalPictogramas = viewModel.listaPictogramas?.let { ArrayList(it) }
            iniciarFragment(viewModel.listaPictogramas, viewModel.listaPictoCuaderno[position].termometro, viewModel.listaPictoCuaderno[position].titulo!!)
        }

        viewModel._pictoBusquedaAdded.observe(this) {
            viewModel.originalPictogramas?.add(it)
            viewModel.picto.guardarPictoCuaderno(this, it.id, it.titulo, it.imagen, viewModel.idCuaderno)
        }

        viewModel._removePicto.observe(this) {
            if(viewModel.sourceAPI){
                viewModel.picto.borrarPictoCuadernoBusqueda(this, it.id, viewModel.idCuaderno)
            }else{
                viewModel.picto.borrarPictoCuaderno(this, it.id, viewModel.idCuaderno)
            }
            viewModel.originalPictogramas?.remove(it)
            if(!viewModel.isBusqueda){
                fragmentCuadernoPictoEdit!!.updateDataRemove(it)
            }
        }

        viewModel._cerrarFragment.observe(this) {
            val bundle = Bundle()
            bundle.putSerializable("key", viewModel.listaCuadernos)
            bundle.putSerializable("isPlan", viewModel.isPlanificador)
            fragmentPrincipal!!.arguments = bundle
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.layout_fragments, fragmentPrincipal!!)
            transaction!!.commit()
        }

        viewModel._queryBusqueda.observe(this) {
            viewModel.listaPictogramas?.clear()
            getPictogramas(it)
            viewModel.isBusqueda = true
        }


    }


    private fun getPictogramas(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.forEach { key ->
                    dict[key]?.let { (value, id) ->
                        crearPictoBusqueda(key, value, id)
                        fragmentCuadernoPictoEdit!!.mostrarPictogramasBusqueda(viewModel.listaPictogramas, viewModel.listaPictosAgregados)
                    }
                }
            }
        }
    }

    private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int) {
        val tituloMayus = titulo?.uppercase()
        val archivo = CommonUtils.crearImagen(bitmap, titulo, this)
        val exite = viewModel.originalPictogramas?.find { it.id == id.toString() }
        if(exite != null){
            exite.id?.let { viewModel.listaPictosAgregados.add(it) }
        }
        viewModel.listaPictogramas?.add(Pictograma(id.toString(), tituloMayus, archivo, 0, 0, favorito = false, sourceAPI = true))
    }


    private fun iniciarFragment(pictogramas: ArrayList<Pictograma>?, termometro: Boolean?, tituloCuaderno: String) {
        val bundle = Bundle()
        bundle.putSerializable("key", pictogramas ?: ArrayList<Pictograma>())
        bundle.putSerializable("termometro", termometro)
        bundle.putString("tituloCuaderno", tituloCuaderno)
        bundle.putSerializable("idCuaderno", viewModel.idCuaderno)
        bundle.putSerializable("isBusqueda", viewModel.isBusqueda)

         if (viewModel.isPlanificador) {
                fragmentCuadernoPictoEdit!!.arguments = bundle
                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.layout_fragments, fragmentCuadernoPictoEdit!!)
            } else {
                fragmentCuadernoPictogramas!!.arguments = bundle
                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.layout_fragments, fragmentCuadernoPictogramas!!)
            }

        transaction!!.addToBackStack(null)
        transaction!!.commit()
    }

}