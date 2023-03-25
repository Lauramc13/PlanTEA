package com.example.plantea.presentacion.actividades.planificador

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.example.plantea.presentacion.actividades.ManualActivity

class PasswordActivity : AppCompatActivity() {
    private lateinit var viejaPass: TextView
    private lateinit var nuevaPass: TextView
    private lateinit var confirmaPass: TextView
    private lateinit var btn_guardar: Button
    private var actualizado: Boolean = false
    var usuario = Usuario_Planificador()
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viejaPass = findViewById(R.id.txt_PassActual)
        nuevaPass = findViewById(R.id.txt_NuevaPass)
        confirmaPass = findViewById(R.id.txt_RepPass)
        btn_guardar = findViewById(R.id.btn_Guardar)

        //Este método se ejecutará al seleccionar el boton guardar
        btn_guardar.setOnClickListener(View.OnClickListener {
            if (viejaPass.getText().toString() == "" || nuevaPass.getText().toString() == "" || confirmaPass.getText().toString() == "") {
                Toast.makeText(applicationContext, "Debes completar todos los campos", Toast.LENGTH_LONG).show()
            } else {
                actualizado = usuario.confirmarPass(viejaPass.getText().toString(), nuevaPass.getText().toString(), confirmaPass.getText().toString(), this@PasswordActivity)
                if (actualizado!!) {
                    Toast.makeText(applicationContext, "Contraseña actualizada", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Error al actualizar. Introduce de nuevo los datos. ", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    //Menu principal
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_ayuda, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.item_ayuda_menu -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
        }
        return true
    }
}