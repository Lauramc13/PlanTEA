package com.example.plantea.presentacion.actividades.ninio

import android.animation.AnimatorSet
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PreLoginActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.google.android.material.imageview.ShapeableImageView
import java.util.*

class PlanActivity : AppCompatActivity(), AdaptadorPresentacion.OnItemSelectedListener {
    lateinit var listaPictogramas: ArrayList<Pictograma>
    var plan = Planificacion()
    lateinit var titulo: TextView
    lateinit var mensajePremio: TextView
    lateinit var lblMensaje: TextView
    lateinit var tituloObtenido: String
    lateinit var iconoCuaderno: LinearLayout
    lateinit var iconoActividad: LinearLayout
    lateinit var iconoDeshacer: Button
    lateinit var iconoReproducir: Button
    lateinit var imagenConfeti: ImageView
    lateinit var card: CardView
    lateinit var card_cuaderno : CardView
    lateinit var card_actividades: CardView
    lateinit var pasosCompletados: Stack<Int>
    lateinit var adaptador: AdaptadorPresentacion

    lateinit var btn_cerrar : ImageView

    private lateinit var recyclerView: RecyclerView
    private var recyclerViewState: Parcelable? = null

    private var dialog: Dialog? = null

    lateinit var btn_logout: Button
    private lateinit var icono_cerrar_login: ImageView

    val handler = Handler()
    private var currentDialog: Dialog? = null
    var runnable: Runnable? = null
    var reproductor: Boolean = false
    private var isRunning = false



    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
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
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Pila de los pasos completados en el seguimiento de un plan
        pasosCompletados = Stack<Int>()
        iconoCuaderno = findViewById(R.id.img_cuaderno)
        iconoActividad = findViewById(R.id.img_actividad)
        card_cuaderno = findViewById(R.id.card_cuaderno)
        iconoDeshacer = findViewById(R.id.icon_deshacer)
        iconoReproducir = findViewById(R.id.icon_reproducir)

        titulo = findViewById(R.id.lbl_titulo)
        lblMensaje = findViewById(R.id.lbl_mensajeNinio)
        recyclerView = findViewById(R.id.recycler_plan)

        card_actividades = findViewById(R.id.card_actividad)
        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = layoutManagerLinear

        // Restore the RecyclerView state if it was saved before
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recycler_view_state") //TODO: QUITAR EL PARCEABLE POR GETPARCEABLEEXTRA
            recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
       // objetoAyuda = findViewById(R.id.layout_objetoAyuda)

        //Obtener preferencias objeto tranquilizador
        //img_objetoAyuda.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
        //txt_objetoAyuda.text = prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())

        //Comprobar si hay parametros en caso de llamada desde el planificador
        val parametros = this.intent.extras
        if (parametros != null) {
            titulo.text = intent.getStringExtra("titulo")
            listaPictogramas = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!

        } else {
            val idUsuario = prefs.getString("idUsuario", "")
            //Mostrar la planificación a seguir para el niño
            listaPictogramas = ArrayList()
            listaPictogramas = idUsuario?.let { plan.mostrarPlanificacion(it, this) } as ArrayList<Pictograma>
            //Mostrar título de la planificación
            tituloObtenido = plan.obtenerTituloPlan(idUsuario, this)
            titulo.text = tituloObtenido

        }
        adaptador = AdaptadorPresentacion(listaPictogramas, this)
        recyclerView.adapter = adaptador
        //Mostrar mensaje si no hay plan
        if (listaPictogramas.isEmpty()) {
            lblMensaje.visibility = View.VISIBLE
            iconoDeshacer.visibility = View.INVISIBLE
            iconoReproducir.visibility = View.INVISIBLE
        } else {
            lblMensaje.visibility = View.INVISIBLE
        }

        //Este método se ejecutará al seleccionar el icono cuaderno para acceder
        card_cuaderno.setOnClickListener {
            val intent = Intent(applicationContext, CuadernoActivity::class.java)
            startActivity(intent)
        }

        card_actividades.setOnClickListener{
            val intent = Intent(applicationContext, ActividadActivity::class.java)
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
                viewHolderPictogramas.itemView.findViewById<View>(R.id.btn_historiaPictoOn).alpha = 1f
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_personalizado)
                viewHolderPictogramas.popListClicked()
            }
        }

        iconoReproducir.setOnClickListener{
            var currentPosition = 0
            currentDialog?.dismiss()

            runnable = object : Runnable {
                override fun run() {
                    if (isRunning) {
                        reproductor = true
                        onItemSeleccionado(currentPosition)
                        currentPosition++

                        if (currentPosition < listaPictogramas.size) {
                            handler.postDelayed(this, 4000L)
                        }
                    }
                }
            }

            isRunning = true
            handler.post(runnable!!)
        }
        //objetoAyuda.setOnClickListener {
          //  val dialog = Dialog(this@PlanActivity)
          //  dialog.setContentView(R.layout.dialogo_presentacion)
          //  dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
          //  val pictograma = dialog.findViewById<ImageView>(R.id.img_pictograma)
        //  val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        //   pictograma.setImageURI(Uri.parse(prefs.getString("imagenObjeto", "")))
        //   tituloPictograma.text =
        //       prefs.getString("nombreObjeto", "")!!.uppercase(Locale.getDefault())
        //    dialog.show()
        //    }
    }


    override fun onItemSeleccionado(posicion: Int) {
        currentDialog?.dismiss()
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dialogo_presentacion)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_cerrar = dialog!!.findViewById(R.id.icono_CerrarDialogoEvento)
        val pictograma = dialog!!.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog!!.findViewById<TextView>(R.id.lbl_pictograma)
        val historia = dialog!!.findViewById<ConstraintLayout>(R.id.Bubble)


        //Añade a la pila el paso completado
        pasosCompletados.push(posicion)
        if (!reproductor){

            imagenConfeti = dialog!!.findViewById(R.id.img_confeti)
            mensajePremio = dialog!!.findViewById(R.id.txt_premio)
            card = dialog!!.findViewById(R.id.card_presentacion)
            val animFondo = AnimationUtils.loadAnimation(applicationContext, R.anim.confeti)
            val animCard = AnimationUtils.loadAnimation(applicationContext, R.anim.card)

            currentDialog = dialog

            //Botón cerrar
            btn_cerrar.setOnClickListener { dialog!!.dismiss() }
            dialog!!.show()

            //Si es recompensa mostramos el dialogo diferente
            if (listaPictogramas[posicion].categoria == 9) {
                imagenConfeti.visibility = View.VISIBLE
                mensajePremio.visibility = View.VISIBLE
                imagenConfeti.animation = animFondo
                card.animation = animCard
                mensajePremio.animation = animFondo
            } else if (listaPictogramas[posicion].categoria == 8) {
                imagenConfeti.visibility = View.VISIBLE
                mensajePremio.visibility = View.VISIBLE
                imagenConfeti.setImageResource(R.drawable.svg_espera)
                mensajePremio.text = "¡Mientras esperamos!"
                imagenConfeti.animation = animCard
                card.animation = animCard
                mensajePremio.animation = animFondo
            } else {
                imagenConfeti.visibility = View.INVISIBLE
                mensajePremio.visibility = View.INVISIBLE
            }

            val textoHistoria = dialog!!.findViewById<TextView>(R.id.lblBubble)
            val avatarHistoria = dialog!!.findViewById<ShapeableImageView>(R.id.avatarBubble)

            val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
            if (prefs.getString("imagenPlanificador", "") === "") {
                avatarHistoria.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            } else {
                avatarHistoria.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
            }

            val orientation = resources.configuration.orientation
            val layoutParams = historia.layoutParams as ConstraintLayout.LayoutParams
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutParams.width = 350.dpToPx(this)
                historia.layoutParams = layoutParams
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutParams.width = 250.dpToPx(this)
                historia.layoutParams = layoutParams
            }

            if (listaPictogramas[posicion].historia != null) {
                textoHistoria.text = listaPictogramas[posicion].historia
                historia.visibility = View.VISIBLE
            } else {
                historia.visibility = View.GONE
            }

            pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
            tituloPictograma.text = listaPictogramas[posicion].titulo
            
        }else{
            card = dialog!!.findViewById(R.id.card_presentacion)

            val showAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_side)
            val dismissAnimation = AnimationUtils.makeOutAnimation(this, false)

            card.visibility = View.VISIBLE
            card.startAnimation(showAnimation)

            val handler = Handler()
            val delayBeforeDismiss = 2000L // Delay before starting the dismiss animation in milliseconds
            val totalDuration = showAnimation.duration + delayBeforeDismiss + dismissAnimation.duration

            handler.postDelayed({
                card.clearAnimation()
                card.startAnimation(dismissAnimation)
            }, showAnimation.duration + delayBeforeDismiss)

            if(posicion != listaPictogramas.size-1){
                handler.postDelayed({
                    card.visibility = View.GONE
                }, totalDuration)
            }

            pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
            tituloPictograma.text = listaPictogramas[posicion].titulo
            historia.visibility = View.GONE

            currentDialog = dialog

            //Botón cerrar
            btn_cerrar.setOnClickListener {
                dialog!!.dismiss()
                isRunning = false
                reproductor = false
            }
        }
        dialog!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
            R.id.item_perfil -> {
                val popupMenu = PopupMenu(this@PlanActivity, findViewById(R.id.item_ayuda) )
                popupMenu.inflate(R.menu.popup_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_1 -> {
                            val perfil = Intent(applicationContext, ConfiguracionActivity::class.java)
                            startActivity(perfil)
                            true
                        }
                        // R.id.option_2 -> {
                        //     val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                        //     val isPlanificadorLogged = prefs.getBoolean("PlanificadorLogged", false)
                        //     if(isPlanificadorLogged){
                        //         val editor = prefs.edit()
                        //         editor.putBoolean("PlanificadorLogged", false)
                        //         editor.commit()
                        //         val plan = Intent(applicationContext, PlanActivity::class.java)
                        //         startActivity(plan)
                        //     }else{
                        //         crearDialogoLogin()
                        //     }
                        //     true
                        // }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                prefs.edit().clear().commit()
                                // val editor = prefs.edit()
                                // editor.putBoolean("userAccount", false)
                                // editor.apply()
                                val login = Intent(applicationContext, PreLoginActivity::class.java)
                                startActivity(login)
                            }
                            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                            dialogLogout.show()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            android.R.id.home -> finish()
        }
        return true
    }
}