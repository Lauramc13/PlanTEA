package com.example.plantea.presentacion.actividades

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorListaEventos
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasPlan
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import com.example.plantea.presentacion.viewModels.EventosPlanificadorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class EventosPlanificadorActivity : AppCompatActivity(), AdaptadorListaEventos.OnItemSelectedListener, AdaptadorPictogramasPlan.OnItemSelectedListener {

    val viewModel: EventosPlanificadorViewModel by viewModels()

    lateinit var listaEventos: RecyclerView
    private lateinit var adaptador: AdaptadorListaEventos
    private lateinit var fragmentEdit: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_planificador)
        val atras = findViewById<Button>(R.id.atras)
        val nuevoEvento = findViewById<Button>(R.id.btn_eventos)
        fragmentEdit = findViewById(R.id.linearLayout16)

        // Si se va hacia atras y no hay nada en la cola, se redirige a MainActivity
        val callback = viewModel.backCallBack(this)
        onBackPressedDispatcher.addCallback(this, callback)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs)

        listaEventos = findViewById(R.id.recycler_eventos)

        atras?.setOnClickListener {
            finish()
        }

        nuevoEvento.setOnClickListener {
            val intent = Intent(this, CalendarioActivity::class.java)
            startActivity(intent)
        }

        iniciarListaPlanificaciones()
    }

    private fun iniciarListaPlanificaciones() {
        listaEventos.layoutManager = LinearLayoutManager(this)
        viewModel.eventos = viewModel.evento.obtenerTodosEventos(viewModel.idUsuario, this)
        if(viewModel.eventos.isEmpty()){
            findViewById<LinearLayout>(R.id.layout_no_eventos).visibility = View.VISIBLE
        }
        adaptador = AdaptadorListaEventos(viewModel.eventos, this)
        listaEventos.adapter = adaptador
    }

    override fun eventoSeleccionado(posicion: Int, recyclerPictogramas: RecyclerView, context: Context) {
        viewModel.posicionEvento = posicion
        val plan = Planificacion()
        val pictosEvento = plan.obtenerPictogramasPlanificacion(this, viewModel.eventos[posicion].idPlan, Locale.getDefault().language, viewModel.idUsuario)

        for (pictogram in pictosEvento) {
            if (pictogram.idAPI != 0)
                pictogram.imagen = BitmapFactory.decodeResource(resources, R.drawable.loading_placeholder)
        }

        val adaptadorPictogramas = AdaptadorPictogramasPlan(pictosEvento, this)
        recyclerPictogramas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerPictogramas.adapter = adaptadorPictogramas

        CoroutineScope(Dispatchers.Main).launch {
            pictosEvento.forEach { pictogram ->
                if (pictogram.idAPI != 0) {
                    pictogram.imagen = BitmapFactory.decodeResource(resources, R.drawable.loading_placeholder)
                    pictogram.imagen = withContext(Dispatchers.IO) {
                        CommonUtils.getImagenAPI(pictogram.idAPI)
                    }
                }
                adaptadorPictogramas.notifyItemChanged(pictosEvento.indexOf(pictogram))
            }
        }
    }

    override fun verEvento(posicion: Int, context: Context) {
        val plan = Planificacion()
        val pictogramas = plan.obtenerPictogramasPlanificacion(this, viewModel.eventos[posicion].idPlan, Locale.getDefault().language, viewModel.idUsuario)

        val intent = Intent(this, EventosActivity::class.java)
        intent.putExtra("titulo", viewModel.eventos[posicion].nombre)
        intent.putExtra("pictogramas", pictogramas)
        pictogramas.forEachIndexed { index, pictogram ->
            intent.putExtra("imagen_$index", CommonUtils.bitmapToByteArray(pictogram.imagen))
        }
        intent.putExtra("fecha", viewModel.eventos[posicion].fecha)

        startActivity(intent)
    }

    override fun eventoEditado(posicion: Int, context: Context) {
        CalendarioUtilidades.fechaSeleccionada = viewModel.eventos[posicion].fecha!!
        if(CommonUtils.isMobile(this) && CommonUtils.isPortrait(this)) {
            bottomSheetDialog(context, viewModel.eventos[posicion])
        }else{
            val evento = viewModel.eventos[posicion]
            viewModel.fragment = NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.hora, evento.idPlan, evento.reminder, evento.cambiarVisibilidad)
            CalendarioUtilidades.fechaSeleccionada = evento.fecha!!
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            ft.replace(R.id.linearLayout16, viewModel.fragment)
            ft.addToBackStack(null)
            ft.commit()

            expand(true, CommonUtils.isPortrait(this))
        }
    }

    private fun bottomSheetDialog(context: Context, evento: Evento?){
        val fragment = if (evento == null){
            NuevoEventoFragment()
        }else{
            NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.hora, evento.idPlan, evento.reminder, evento.cambiarVisibilidad)
        }
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
    }

    fun expand(isExpand: Boolean, isVertical: Boolean) {
        var valueAnimator: ValueAnimator
        if(isExpand) {
            fragmentEdit.visibility = View.VISIBLE
            if(isVertical){
                val targetHeigt = ((fragmentEdit.parent as View).height * 0.55).toInt()
                fragmentEdit.layoutParams.height = 0
                valueAnimator = ValueAnimator.ofInt(0, targetHeigt)
                valueAnimator.addUpdateListener { animation ->
                    fragmentEdit.layoutParams.height = animation.animatedValue as Int
                    fragmentEdit.requestLayout()
                }
            }else {
                val targetWidth = ((fragmentEdit.parent as View).width * 0.4).toInt()
                fragmentEdit.layoutParams.width = 0
                valueAnimator = ValueAnimator.ofInt(0, targetWidth)
                valueAnimator.addUpdateListener { animation ->
                    fragmentEdit.layoutParams.width = animation.animatedValue as Int
                    fragmentEdit.requestLayout()
                }
            }

            valueAnimator.addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    viewModel.fragment.listaPlanificaciones.adapter?.notifyDataSetChanged()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            valueAnimator.interpolator = AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator)
            valueAnimator.duration = 500
            valueAnimator.start()
        }else{
            fragmentEdit.removeAllViews()
            if(isVertical){
                valueAnimator = ValueAnimator.ofInt(fragmentEdit.height, 0)
                valueAnimator.addUpdateListener { animation ->
                    fragmentEdit.layoutParams.height = animation.animatedValue as Int
                    fragmentEdit.requestLayout()
                }
            }else{
                valueAnimator = ValueAnimator.ofInt(fragmentEdit.width, 0)
                valueAnimator.addUpdateListener { animation ->
                    fragmentEdit.layoutParams.width = animation.animatedValue as Int
                    fragmentEdit.requestLayout()
                }
            }
            valueAnimator.interpolator = AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator)
            valueAnimator.duration = 300
            valueAnimator.start()
        }
    }

    private fun configurarEventos() {
        iniciarListaPlanificaciones()
        viewModel.eventos.clear()
        viewModel.eventos.addAll(viewModel.evento.obtenerTodosEventos(viewModel.idUsuario, this))
        adaptador.notifyDataSetChanged()
    }

    override fun pictogramaSeleccionado(posicion: Int, context: Context) {
        Log.d("Pictograma", "Pictograma seleccionado")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        configurarEventos()
    }

}