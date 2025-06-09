package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.plantea.R
class AdaptadorPaginas(private val context: Context) : PagerAdapter() {

    private val slideLayouts = arrayOf(
        R.layout.fragment_imagen0,
        R.layout.fragment_imagen1,
        R.layout.fragment_imagen2,
        R.layout.fragment_imagen3,
        R.layout.fragment_imagen4
    )
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val slideLayout = inflater.inflate(slideLayouts[position], container, false)
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

