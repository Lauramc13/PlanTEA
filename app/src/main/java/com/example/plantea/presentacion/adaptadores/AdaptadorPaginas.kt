package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.PagerAdapter
import com.example.plantea.R
class AdaptadorPaginas(private val context: Context) : PagerAdapter() {

    private val slideLayouts = arrayOf(
        R.layout.fragment_imagen1,
        R.layout.fragment_imagen2,
        R.layout.fragment_imagen3
    )
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val slideLayout = inflater.inflate(slideLayouts[position], container, false)

        val imageViewLight = slideLayout.findViewById<ImageView>(R.id.frame_light)
        val imageViewNight = slideLayout.findViewById<ImageView>(R.id.frame_night)

        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            imageViewLight.visibility = View.GONE
            imageViewNight.visibility = View.VISIBLE
        } else {
            imageViewLight.visibility = View.VISIBLE
            imageViewNight.visibility = View.GONE
        }
        container.addView(slideLayout)
        return slideLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val slideLayout = `object` as View
        container.removeView(slideLayout)
    }

    override fun getCount(): Int {
        return slideLayouts.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}

