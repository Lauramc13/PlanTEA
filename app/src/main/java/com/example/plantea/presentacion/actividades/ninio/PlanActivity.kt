package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import java.util.*

class PlanActivity : AppCompatActivity(), AdaptadorPresentacion.OnItemSelectedListener {
    lateinit var listaPictogramas: ArrayList<Pictograma>
    var plan = Planificacion()
    lateinit var titulo: TextView
    lateinit var mensajePremio: TextView
    lateinit var txt_objetoAyuda: TextView
    lateinit var lblMensaje: TextView
    lateinit var tituloObtenido: String
    lateinit var iconoCuaderno: ImageView
    lateinit var iconoDeshacer: ImageView
    lateinit var imagenConfeti: ImageView
    lateinit var img_objetoAyuda: ImageView
    lateinit var card: CardView
    lateinit var objetoAyuda: LinearLayout
    lateinit var pasosCompletados: Stack<Int>
    lateinit var adaptador: AdaptadorPresentacion

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    private var recyclerViewState: Parcelable? = null


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }

        super.onConfigurationChanged(newConfig)
        // Update the number of columns in the GridLayoutManager dynamically
        val gridValueManager = if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            3
        } else {
            5
        }
        layoutManager.spanCount = gridValueManager

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the RecyclerView state before the activity is destroyed
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        outState.putParcelable("recycler_view_state", recyclerViewState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Pila de los pasos completados en el seguimiento de un plan
        pasosCompletados = Stack<Int>()
        iconoCuaderno = findViewById(R.id.img_cuaderno)
        iconoDeshacer = findViewById(R.id.icon_deshacer)
        //img_objetoAyuda = findViewById(R.id.img_objetoAyuda)
        titulo = findViewById(R.id.lbl_titulo)
        //txt_objetoAyuda = findViewById(R.id.txt_objetoAyuda)
        lblMensaje = findViewById(R.id.lbl_mensajeNinio)
        recyclerView = findViewById(R.id.recycler_plan)
        val orientation = resources.configuration.orientation
        val gridValueManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            3
        } else {
            5
        }
        layoutManager = GridLayoutManager(this, gridValueManager)
        recyclerView.layoutManager = layoutManager

        // Restore the RecyclerView state if it was saved before
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recycler_view_state") //TODO, QUITAR EL PARCEABLE POR GETPARCEABLEEXTRA
            recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
       // objetoAyuda = findViewById(R.id.layout_objetoAyuda)

        //Obtener preferencias objeto tranquilizador
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        img_objetoAyuda.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
        txt_objetoAyuda.text = prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())

        //Comprobar si hay parametros en caso de llamada desde el planificador
        val parametros = this.intent.extras
        if (parametros != null) {
            titulo.text = intent.getStringExtra("titulo")
            listaPictogramas = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
        } else {
            //Mostrar la planificación a seguir para el niño
            listaPictogramas = ArrayList()
            listaPictogramas = plan.mostrarPlanificacion(this) as ArrayList<Pictograma>
            //Mostrar título de la planificación
            tituloObtenido = plan.obtenerTituloPlan(this)
            titulo.text = tituloObtenido
        }
        adaptador = AdaptadorPresentacion(listaPictogramas, this)
        recyclerView.adapter = adaptador
        //Mostrar mensaje si no hay plan
        if (listaPictogramas.isEmpty()) {
            lblMensaje.visibility = View.VISIBLE
        } else {
            lblMensaje.visibility = View.INVISIBLE
        }

        //Este método se ejecutará al seleccionar el icono cuaderno para acceder
        iconoCuaderno.setOnClickListener {
            val intent = Intent(applicationContext, CuadernoActivity::class.java)
            startActivity(intent)
        }

        //Este método se ejecutará al seleccionar el icono deshacer para volver un paso atrás en el seguimiento
        iconoDeshacer.setOnClickListener {
            if (!pasosCompletados.empty()) {
                val posicionUndo = pasosCompletados.pop() as Int
                val viewHolderPictogramas =
                    recyclerView.findViewHolderForAdapterPosition(posicionUndo) as AdaptadorPresentacion.ViewHolderPictogramas?
                viewHolderPictogramas!!.itemView.findViewById<View>(R.id.id_Imagen).alpha = 1f
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_Texto).alpha = 1f
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_personalizado)
            }
        }
        objetoAyuda.setOnClickListener {
            val dialog = Dialog(this@PlanActivity)
            dialog.setContentView(R.layout.dialogo_presentacion)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val pictograma = dialog.findViewById<ImageView>(R.id.img_pictograma)
            val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
            pictograma.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
            tituloPictograma.text =
                prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_ayuda, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda_menu -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onItemSeleccionado(posicion: Int) {
        //Añade a la pila el paso completado
        pasosCompletados.push(posicion)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_presentacion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        imagenConfeti = dialog.findViewById(R.id.img_confeti)
        mensajePremio = dialog.findViewById(R.id.txt_premio)
        card = dialog.findViewById(R.id.card_presentacion)
        val animFondo = AnimationUtils.loadAnimation(applicationContext, R.anim.confeti)
        val animCard = AnimationUtils.loadAnimation(applicationContext, R.anim.card)


        //Si es recompensa mostramos el dialogo diferente
        if (listaPictogramas[posicion].categoria == 7) {
            imagenConfeti.visibility = View.VISIBLE
            mensajePremio.visibility = View.VISIBLE
            imagenConfeti.animation = animFondo
            card.animation = animCard
            mensajePremio.animation = animFondo
        } else if (listaPictogramas[posicion].categoria == 6) {
            imagenConfeti.visibility = View.VISIBLE
            mensajePremio.visibility = View.VISIBLE
            imagenConfeti.setImageResource(R.drawable.espera)
            mensajePremio.text = "¡Mientras esperamos!"
            imagenConfeti.animation = animCard
            card.animation = animCard
            mensajePremio.animation = animFondo
        } else {
            imagenConfeti.visibility = View.INVISIBLE
            mensajePremio.visibility = View.INVISIBLE
        }
        val pictograma = dialog.findViewById<ImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
        tituloPictograma.text = listaPictogramas[posicion].titulo
        dialog.show()
    }
}