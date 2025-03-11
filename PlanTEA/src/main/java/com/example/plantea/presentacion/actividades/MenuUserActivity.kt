package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionUsuarios
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedString
import com.example.plantea.presentacion.adaptadores.AdaptadorMenuUser


class MenuUserActivity : AppCompatActivity(), AdaptadorMenuUser.OnItemSelectedListener {

        private var usersTEA: ArrayList<Usuario>? = null
        private lateinit var adapterUsers: AdaptadorMenuUser

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_usertea)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        getUsers(recyclerView)

    }

    private fun getUsers(recyclerView: RecyclerView) {
        val gUsuario = GestionUsuarios()
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        usersTEA = gUsuario.obtenerUsuariosTEA(prefs.getString("idUsuario", ""), this)
        if (usersTEA!!.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            adapterUsers = AdaptadorMenuUser(usersTEA, this)
            if(CommonUtils.isMobile(this)) {
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            } else {
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }

            recyclerView.adapter = adapterUsers
        } else {
            recyclerView.visibility = View.GONE
        }
    }

    override fun onClickUser(position: Int) {
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        val editor = prefs.edit()
        editor.putString("idUsuarioTEA", usersTEA!![position].id)
        editor.putString("nombreUsuarioTEA", usersTEA!![position].name)
        editor.putString("imagenUsuarioTEA", CommonUtils.bitmapToByteArray(usersTEA!![position].imagen!!).toPreservedString)
        editor.putString("configPictogramas", usersTEA!![position].configPictograma)

        editor.apply()

        val intent = Intent(this, EventosPlanificadorActivity::class.java)
        startActivity(intent)
    }
}