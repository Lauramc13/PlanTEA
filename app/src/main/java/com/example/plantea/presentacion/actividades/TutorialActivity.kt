package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.plantea.R
import com.google.android.material.tabs.TabLayout
import com.example.plantea.presentacion.adaptadores.AdaptadorPaginas
import com.example.plantea.presentacion.viewModels.TutorialViewModel
import com.google.android.material.button.MaterialButton

class TutorialActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: AdaptadorPaginas
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnSkip: MaterialButton
    private var isFromManual = false

    private val viewModel by viewModels<TutorialViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        isFromManual = intent.getBooleanExtra("isFromManual", false)

        viewPager = findViewById(R.id.view_pager)
        adapter = AdaptadorPaginas(this)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_saltar)

        btnPrevious.setOnClickListener {
            viewPager.currentItem -= 1
            btnNext.text = getString(R.string.str_siguiente)
        }

        btnNext.setOnClickListener {
            if(viewPager.currentItem == 3){
                btnNext.text = getString(R.string.str_finalizar)
                isFromManual()
            }else{
                btnNext.text = getString(R.string.str_siguiente)
            }
            viewPager.currentItem += 1
        }

        btnSkip.setOnClickListener {
            isFromManual()
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Not used
            }

            override fun onPageSelected(position: Int) {
                btnNext.text = viewModel.updateButtonText(this@TutorialActivity, position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Not used
            }
        })
    }

    private fun isFromManual(){
        if (isFromManual){
            startActivity(Intent(applicationContext, ManualActivity::class.java))
        }else {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        finish()
    }
}