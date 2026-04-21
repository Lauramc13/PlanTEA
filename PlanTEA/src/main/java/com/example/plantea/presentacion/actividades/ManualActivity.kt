package com.example.plantea.presentacion.actividades

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.adaptadores.AdaptadorListaManual
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView


class ManualActivity : AppCompatActivity() {

    private lateinit var cardEvento: MaterialCardView
    private lateinit var cardEventoEdit: MaterialCardView
    private lateinit var cardPlanficacion : MaterialCardView
    private lateinit var cardCalendarioSemanal : MaterialCardView
    private lateinit var cardCalendarioMensual : MaterialCardView
    private lateinit var cardActividades : MaterialCardView
    private lateinit var cardTraductor : MaterialCardView

    private lateinit var iconEvento : ImageView
    private lateinit var iconEventoEdit : ImageView
    private lateinit var iconPlanificacion : ImageView
    private lateinit var iconCalendarioSemanal : ImageView
    private lateinit var iconCalendarioMensual : ImageView
    private lateinit var iconActividades : ImageView
    private lateinit var iconTraductor : ImageView

    private lateinit var textEvento : RecyclerView
    private lateinit var textEventoEdit : RecyclerView
    private lateinit var textPlanificacion : RecyclerView
    private lateinit var textCalendarioSemanal : RecyclerView
    private lateinit var textCalendarioMensual : RecyclerView
    private lateinit var textActividades : RecyclerView
    private lateinit var textTraductor : RecyclerView

    private lateinit var scrollView : ScrollView

    private var fragmentImgenEvento : GifImageView? = null
    private var fragmentImgenEventoEdit : GifImageView? = null
    private var fragmentImgenPlanificacion : GifImageView? = null
    private var fragmentImgenCalendarioSemanal : GifImageView? = null
    private var fragmentImgenCalendarioMensual : GifImageView? = null
    private var fragmentImgenActividades : GifImageView? = null
    private var fragmentImgenTraductor : GifImageView? = null
    private var fragmentImagen: GifImageView? = null

    var isOpen  = false
    var cardOpened : MaterialCardView? = null
    var iconOpened : ImageView? = null
    private lateinit var btnTutorial : Button

    override fun finish() {
        super.finish()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_left, R.anim.slide_out_right)
        }else{
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !prefs.getBoolean("darkMode", false)

        cardEvento = findViewById(R.id.card_evento)
        cardEventoEdit = findViewById(R.id.card_evento_edit)
        cardPlanficacion = findViewById(R.id.card_planificacion)
        cardCalendarioSemanal = findViewById(R.id.card_calendarioSemanal)
        cardCalendarioMensual = findViewById(R.id.card_calendarioMensual)
        cardActividades = findViewById(R.id.card_actividades)
        cardTraductor = findViewById(R.id.card_traductor)

        iconEvento = findViewById(R.id.icon_evento)
        iconEventoEdit = findViewById(R.id.icon_evento_edit)
        iconPlanificacion = findViewById(R.id.icon_planificacion)
        iconCalendarioSemanal = findViewById(R.id.icon_calendarioSemanal)
        iconCalendarioMensual = findViewById(R.id.icon_calendarioMensual)
        iconActividades = findViewById(R.id.icon_actividades)
        iconTraductor = findViewById(R.id.icon_traductor)

        textEvento = findViewById(R.id.text_evento)
        textEventoEdit = findViewById(R.id.text_evento_edit)
        textPlanificacion = findViewById(R.id.text_planificacion)
        textCalendarioSemanal = findViewById(R.id.text_calendarioSemanal)
        textCalendarioMensual = findViewById(R.id.text_calendarioMensual)
        textActividades = findViewById(R.id.text_actividades)
        textTraductor = findViewById(R.id.text_traductor)

        fragmentImgenEvento = findViewById(R.id.fragment_imagen_evento)
        fragmentImgenEventoEdit = findViewById(R.id.fragment_imagen_evento_edit)
        fragmentImgenPlanificacion = findViewById(R.id.fragment_imagen_planificacion)
        fragmentImgenCalendarioSemanal = findViewById(R.id.fragment_imagen_calendario_semanal)
        fragmentImgenCalendarioMensual = findViewById(R.id.fragment_imagen_calendario_mensual)
        fragmentImgenActividades = findViewById(R.id.fragment_imagen_actividades)
        fragmentImgenTraductor = findViewById(R.id.fragment_imagen_traductor)
        fragmentImagen = findViewById(R.id.fragment_imagen)

        scrollView = findViewById(R.id.scrollView)
        btnTutorial = findViewById(R.id.btn_tutorial)

        btnTutorial.setOnClickListener {
            val intent = Intent(applicationContext, TutorialActivity::class.java)
            intent.putExtra("isFromManual", true)
            startActivity(intent)
            finish()
        }

        cardEvento.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_crearEvento).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textEvento.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textEvento.adapter = adapter


            val gif = GifDrawable(resources, R.drawable.crear_evento_manual)
            fragmentImgenEvento?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardEvento, iconEvento, textEvento, fragmentImgenEvento)
        }

        cardEventoEdit.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_editarEvento).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textEventoEdit.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textEventoEdit.adapter = adapter


            val gif = GifDrawable(resources, R.drawable.editar_evento_manual)
            fragmentImgenEventoEdit?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardEventoEdit, iconEventoEdit, textEventoEdit, fragmentImgenEventoEdit)
        }

        cardPlanficacion.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_crearPlanificacion).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textPlanificacion.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textPlanificacion.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.planificacion_manual)
            fragmentImgenPlanificacion?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardPlanficacion, iconPlanificacion, textPlanificacion, fragmentImgenPlanificacion)
        }

        cardCalendarioSemanal.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_calendarioSemanal).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textCalendarioSemanal.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textCalendarioSemanal.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.calendario_semanal_manual)
            fragmentImgenCalendarioSemanal?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardCalendarioSemanal, iconCalendarioSemanal, textCalendarioSemanal, fragmentImgenCalendarioSemanal)
        }

        cardCalendarioMensual.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_calendarioMensual).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textCalendarioMensual.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textCalendarioMensual.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.calendario_mensual_manual)
            fragmentImgenCalendarioMensual?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardCalendarioMensual, iconCalendarioMensual, textCalendarioMensual, fragmentImgenCalendarioMensual)
        }

        cardActividades.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_actividades).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textActividades.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textActividades.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.actividades_manual)
            fragmentImgenActividades?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardActividades, iconActividades, textActividades, fragmentImgenActividades)
        }

        cardTraductor.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_traductor).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textTraductor.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textTraductor.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.traductor_manual)
            fragmentImgenTraductor?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)

            toggleCardHeight(cardTraductor, iconTraductor, textTraductor, fragmentImgenTraductor)
        }
    }

    private fun toggleCardHeight(cardView: MaterialCardView, icon: ImageView, textView: RecyclerView, gifImageView: GifImageView?) {
        if (cardOpened == cardView) {
            // Cierra la tarjeta si está abierta
            cardOpened = null
            iconOpened = null
            isOpen = false
            animateCard(cardView, icon, CommonUtils.dpToPx(70, resources), 0f)
        } else {
            // Cierra la tarjeta previamente abierta si existe
            cardOpened?.let { iconOpened?.let { it1 -> animateCard(it, it1, CommonUtils.dpToPx(70, resources), 0f) } }

            textView.measure(View.MeasureSpec.makeMeasureSpec(textView.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
            val height = textView.measuredHeight

            if (CommonUtils.isMobile(this)) {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    animateCard(cardView, icon, height + CommonUtils.dpToPx(70, resources) + CommonUtils.dpToPx(210, resources), 90f) // 210 por la altura del gif
                } else {
                    animateCard(cardView, icon, height + CommonUtils.dpToPx(70, resources) + CommonUtils.dpToPx(20, resources), 90f) // 20dp como margen inferior
                }
            } else {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    animateCard(cardView, icon, height + CommonUtils.dpToPx(90, resources) + CommonUtils.dpToPx(400, resources), 90f) // 400 por la altura del gif
                } else {
                    animateCard(cardView, icon,  height + CommonUtils.dpToPx(90, resources), 90f)
                }
            }

            cardOpened = cardView
            iconOpened = icon
            isOpen = true
        }
    }

    // Función auxiliar para medir la vista
    private fun measureView(view: View?) {
        view?.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
    }


    // Function to animate the card
    private fun animateCard(cardView: MaterialCardView, icon: ImageView, newHeight: Int, degrees: Float){

        val animator = ValueAnimator.ofInt(cardView.height, newHeight)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = cardView.layoutParams
            layoutParams.height = valueAnimator.animatedValue as Int
            cardView.layoutParams = layoutParams
        }

        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 450
        icon.animate().rotation(degrees).start()

        animator.start()
    }


}