package com.example.plantea.presentacion.actividades

import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var cardPlanficacion : MaterialCardView
    private lateinit var cardCuaderno : MaterialCardView
    private lateinit var cardTraductor : MaterialCardView
    //private lateinit var cardConfiguracion : MaterialCardView

    private lateinit var iconEvento : ImageView
    private lateinit var iconPlanificacion : ImageView
    private lateinit var iconCuaderno : ImageView
    private lateinit var iconTraductor : ImageView
   // private lateinit var iconConfiguracion : ImageView

    private lateinit var textEvento : RecyclerView
    private lateinit var textPlanificacion : RecyclerView
    private lateinit var textCuaderno : RecyclerView
    private lateinit var textTraductor : RecyclerView
    //private lateinit var textConfiguracion : RecyclerView

    private lateinit var scrollView : ScrollView

    private var fragmentImgenEvento : GifImageView? = null
    private var fragmentImgenPlanificacion : GifImageView? = null
    private var fragmentImgenCuaderno : GifImageView? = null
    private var fragmentImgenTraductor : GifImageView? = null
    //private var fragmentImgenConfiguracion : GifImageView? = null
    private var fragmentImagen: GifImageView? = null

    var isOpen  = false
    var cardOpened : MaterialCardView? = null
    var iconOpened : ImageView? = null
    private lateinit var btnTutorial : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        cardEvento = findViewById(R.id.card_evento)
        cardPlanficacion = findViewById(R.id.card_planificacion)
        cardCuaderno = findViewById(R.id.card_cuaderno)
        cardTraductor = findViewById(R.id.card_traductor)
      //  cardConfiguracion = findViewById(R.id.card_configuracion)

        iconEvento = findViewById(R.id.icon_evento)
        iconPlanificacion = findViewById(R.id.icon_planificacion)
        iconCuaderno = findViewById(R.id.icon_cuaderno)
        iconTraductor = findViewById(R.id.icon_traductor)
       // iconConfiguracion = findViewById(R.id.icon_configuracion)

        textEvento = findViewById(R.id.text_evento)
        textPlanificacion = findViewById(R.id.text_planificacion)
        textCuaderno = findViewById(R.id.text_cuaderno)
        textTraductor = findViewById(R.id.text_traductor)
        //textConfiguracion = findViewById(R.id.text_configuracion)

        fragmentImgenEvento = findViewById(R.id.fragment_imagen_evento)
        fragmentImgenPlanificacion = findViewById(R.id.fragment_imagen_planificacion)
        fragmentImgenCuaderno = findViewById(R.id.fragment_imagen_cuaderno)
        fragmentImgenTraductor = findViewById(R.id.fragment_imagen_traductor)
        //fragmentImgenConfiguracion = findViewById(R.id.fragment_imagen_configuracion)
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


            val gif = GifDrawable(resources, R.drawable.manual_evento)
            fragmentImgenEvento?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardEvento, iconEvento, textEvento, fragmentImgenEvento)
        }

        cardPlanficacion.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_crearPlanificacion).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textPlanificacion.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textPlanificacion.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.manual_planificacion)
            fragmentImgenPlanificacion?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardPlanficacion, iconPlanificacion, textPlanificacion, fragmentImgenPlanificacion)
        }

        cardCuaderno.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_cuaderno).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textCuaderno.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textCuaderno.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.manual_cuaderno)
            fragmentImgenCuaderno?.setImageDrawable(gif)
            fragmentImagen?.setImageDrawable(gif)
            toggleCardHeight(cardCuaderno, iconCuaderno, textCuaderno, fragmentImgenCuaderno)
        }

        cardTraductor.setOnClickListener {
            val pasos = resources.getStringArray(R.array.str_traductor).toList()

            val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            textTraductor.layoutManager = layoutManagerLinear
            val adapter = AdaptadorListaManual(pasos)
            textTraductor.adapter = adapter

            val gif = GifDrawable(resources, R.drawable.manual_traductor)
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
            animateCard(cardView, icon, dpToPx(70), 0f)
        } else {
            // Cierra la tarjeta previamente abierta si existe
            cardOpened?.let { iconOpened?.let { it1 -> animateCard(it, it1, dpToPx(70), 0f) } }

            textView.measure(View.MeasureSpec.makeMeasureSpec(textView.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
            val height = textView.measuredHeight

            if (CommonUtils.isMobile(this)) {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    animateCard(cardView, icon, height + dpToPx(70) + dpToPx(210), 90f) // 210 por la altura del gif
                } else {
                    animateCard(cardView, icon, height + dpToPx(70) + dpToPx(20), 90f) // 20dp como margen inferior
                }
            } else {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    animateCard(cardView, icon, height + dpToPx(90) + dpToPx(400), 90f) // 400 por la altura del gif
                } else {
                    animateCard(cardView, icon,  height + dpToPx(90), 90f)
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

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

}