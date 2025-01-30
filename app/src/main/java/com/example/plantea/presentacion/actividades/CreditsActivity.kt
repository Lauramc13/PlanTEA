package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R

class CreditsActivity : AppCompatActivity(){

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        val arasaacLink : TextView = findViewById(R.id.arasaacLink)
        val manyLink : TextView = findViewById(R.id.manypixelsLink)
        val flaticonLink : TextView = findViewById(R.id.flatIconLink)
        val freepikLink : TextView = findViewById(R.id.freepikLink)
        val svgRepoLink: TextView = findViewById(R.id.svgRepoLink)

        arasaacLink.paintFlags = arasaacLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        arasaacLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://arasaac.org/terms-of-use"))
            startActivity(intent)
        }

        manyLink.paintFlags = manyLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        manyLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.manypixels.co/terms-of-service"))
            startActivity(intent)
        }

        flaticonLink.paintFlags = flaticonLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        flaticonLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freepikcompany.com/legal#nav-flaticon"))
            startActivity(intent)
        }

        freepikLink.paintFlags = freepikLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        freepikLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freepikcompany.com/legal#nav-freepik"))
            startActivity(intent)
        }

        svgRepoLink.paintFlags = svgRepoLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        svgRepoLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.svgrepo.com/page/licensing/"))
            startActivity(intent)
        }
    }
}