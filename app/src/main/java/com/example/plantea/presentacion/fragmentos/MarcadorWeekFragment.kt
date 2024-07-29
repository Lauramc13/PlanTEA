package com.example.plantea.presentacion.fragmentos

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.plantea.R
import java.time.LocalDate

class MarcadorWeekFragment : Fragment() {

    lateinit var vista: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.item_marcador_week, container, false)

        val yesterdayText = vista.findViewById<TextView>(R.id.yesterday)
        val todayText = vista.findViewById<TextView>(R.id.today)
        val tomorrowText = vista.findViewById<TextView>(R.id.tomorrow)
        val yesterdayArrow = vista.findViewById<ImageView>(R.id.yesterdayArrow)
        val tomorrowArrow = vista.findViewById<ImageView>(R.id.tomorrowArrow)

        var elementHide = false

        vista.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                vista.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val date = LocalDate.now().dayOfWeek.value
                when (date) {
                    1 -> {yesterdayText.visibility = View.GONE
                        yesterdayArrow.visibility = View.GONE
                        elementHide = true
                        (todayText.background as GradientDrawable).cornerRadii = floatArrayOf(50f,50f, 0f, 0f, 0f, 0f, 50f, 50f)
                    }
                    7 -> {tomorrowText.visibility = View.GONE
                        tomorrowArrow.visibility = View.GONE
                        elementHide = true
                        (todayText.background as GradientDrawable).cornerRadii = floatArrayOf(0f, 0f, 50f, 50f, 50f, 50f, 0f, 0f)
                    }
                }

                val params = vista.layoutParams
                if(elementHide)
                    params.width = (vista.width / 7) * 2
                else
                    params.width = (vista.width / 7) * 3
                vista.layoutParams = params

                //move vista to the left based on the day
                when (date) {
                    1 -> vista.translationX = 0f
                    2 -> vista.translationX = 0f
                    3 -> vista.translationX = vista.width.toFloat() / 7
                    4 -> vista.translationX = vista.width.toFloat() / 7 * 2
                    5 -> vista.translationX = vista.width.toFloat() / 7 * 3
                    6 -> vista.translationX = vista.width.toFloat() / 7 * 4
                    7 -> vista.translationX = vista.width.toFloat() / 7 * 5
                }
            }
        })

        return vista
    }
}