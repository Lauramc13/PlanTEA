package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.plantea.R
import com.google.android.material.tabs.TabLayout
import com.example.plantea.presentacion.adaptadores.AdaptadorPaginas

import java.util.*


class TutorialActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: AdaptadorPaginas
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnSkip: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        viewPager = findViewById(R.id.view_pager)
        adapter = AdaptadorPaginas(this)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_skip)

        btnPrevious.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
            btnNext.text = "Siguiente"
        }

        btnNext.setOnClickListener {
            if(viewPager.currentItem == 2){
                btnNext.text = "Finalizar"
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }else{
                btnNext.text = "Siguiente"
            }
            viewPager.currentItem = viewPager.currentItem + 1
        }

        btnSkip.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // No se usa
            }

            override fun onPageSelected(position: Int) {
                updateButtonText(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // No se usa
            }
        })
    }

    private fun updateButtonText(currentItem: Int) {
        if (currentItem == 2) {
            btnNext.text = "Finalizar"
        } else {
            btnNext.text = "Siguiente"
        }
    }
}