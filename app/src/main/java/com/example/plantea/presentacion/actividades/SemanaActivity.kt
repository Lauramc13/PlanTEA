package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.plantea.R
import com.example.plantea.dominio.Semana
import com.example.plantea.presentacion.viewModels.SemanaViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class SemanaActivity: AppCompatActivity() {

    private val viewModel by viewModels<SemanaViewModel>()

    private lateinit var lunesText: TextView
    private lateinit var martesText: TextView
    private lateinit var miercolesText: TextView
    private lateinit var juevesText: TextView
    private lateinit var viernesText: TextView
    private lateinit var sabadoText: TextView
    private lateinit var domingoText: TextView

    private lateinit var lunesImageText: ImageView
    private lateinit var martesImageText: ImageView
    private lateinit var miercolesImageText: ImageView
    private lateinit var juevesImageText: ImageView
    private lateinit var viernesImageText: ImageView
    private lateinit var sabadoImageText: ImageView
    private lateinit var domingoImageText: ImageView

    private lateinit var lunesImage: ImageView
    private lateinit var martesImage: ImageView
    private lateinit var miercolesImage: ImageView
    private lateinit var juevesImage: ImageView
    private lateinit var viernesImage: ImageView
    private lateinit var sabadoImage: ImageView
    private lateinit var domingoImage: ImageView

    lateinit var week: MutableList<Semana>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semana)

        viewModel.configureUser(getSharedPreferences("Preferencias", MODE_PRIVATE))

        lunesText = findViewById(R.id.mondayText)
        martesText = findViewById(R.id.tuesdayText)
        miercolesText = findViewById(R.id.wednesdayText)
        juevesText = findViewById(R.id.thursdayText)
        viernesText = findViewById(R.id.fridayText)
        sabadoText = findViewById(R.id.saturdayText)
        domingoText = findViewById(R.id.sundayText)

        lunesImageText = findViewById(R.id.mondayTextImage)
        martesImageText = findViewById(R.id.tuesdayTextImage)
        miercolesImageText = findViewById(R.id.wednesdayTextImage)
        juevesImageText = findViewById(R.id.thursdayTextImage)
        viernesImageText = findViewById(R.id.fridayTextImage)
        sabadoImageText = findViewById(R.id.saturdayTextImage)
        domingoImageText = findViewById(R.id.sundayTextImage)

        lunesImage = findViewById(R.id.mondayImage)
        martesImage = findViewById(R.id.tuesdayImage)
        miercolesImage = findViewById(R.id.wednesdayImage)
        juevesImage = findViewById(R.id.thursdayImage)
        viernesImage = findViewById(R.id.fridayImage)
        sabadoImage = findViewById(R.id.saturdayImage)
        domingoImage = findViewById(R.id.sundayImage)

        val buttonGuardar = findViewById<Button>(R.id.btnGuardar)
        val buttonEditar = findViewById<Button>(R.id.btnEditar)
        val buttonCancelar = findViewById<Button>(R.id.btnCancelar)
        val layoutOptions = findViewById<LinearLayout>(R.id.optionsEditar)

        val listaImages = arrayOf(lunesImage, martesImage, miercolesImage, juevesImage, viernesImage, sabadoImage, domingoImage)
        val listaImagesText = arrayOf(lunesImageText, martesImageText, miercolesImageText, juevesImageText, viernesImageText, sabadoImageText, domingoImageText)
        val listaText = arrayOf(lunesText, martesText, miercolesText, juevesText, viernesText, sabadoText, domingoText)

        configurarDaysWeek(listaText, listaImagesText)
        AniadirPictoUtils.createPickMedia(viewModel, this)

        week = obtenerImagenes(listaImages)

        buttonEditar.setOnClickListener {
            layoutOptions.visibility = LinearLayout.VISIBLE
            buttonEditar.visibility = Button.GONE
            changeImageClick(listaImages, true, this)
        }

        buttonCancelar.setOnClickListener {
            layoutOptions.visibility = LinearLayout.GONE
            buttonEditar.visibility = Button.VISIBLE

            for ((index, dia) in week.withIndex()) {
                // restaurar imagenes de la semana a las originales y quitar las nuevas
                listaImages[index].setImageBitmap(dia.imagen)
            }
            changeImageClick(listaImages, false, this)

        }

        buttonGuardar.setOnClickListener {
            val semana = Semana()

            for(i in 0..6){
                if(listaImages[i].tag == "add_image"){
                    listaImages[i].setImageDrawable(null)
                }else{
                    val drawableImage = (listaImages[i].drawable)
                    val bitmapImage = vectorToBitmap(drawableImage)

                    if(week[i].imagen != bitmapImage){
                        val imageBlob = bitmapImage?.let { it1 -> viewModel.bitmapToByteArray(it1)}
                        semana.guardarImagen(viewModel.idUsuario, imageBlob, week[i].dia.toString(), this)
                    }
                }
            }

            changeImageClick(listaImages, false, this)
            layoutOptions.visibility = LinearLayout.GONE
            buttonEditar.visibility = Button.VISIBLE
        }

        for(i in 0..6){
            listaImages[i].setOnClickListener {
                AniadirPictoUtils.initializeDialog(viewModel, this, null, false)
                viewModel.daySelected = week[i].dia.toString()
            }
        }

        observers(listaImages)

    }

    private fun vectorToBitmap(image: Drawable): Bitmap? {
        try {
            val bitmap = Bitmap.createBitmap(image.intrinsicWidth, image.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            image.setBounds(0, 0, canvas.width, canvas.height)
            image.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            // Handle the error
            return null
        }
    }

    fun observers(listaImages: Array<ImageView>) {
        viewModel._imageSelected.observe(this) {
            //on viewModel.diaSelected find in the week and update image
            for(i in 0..6){
                if(week[i].dia == viewModel.daySelected){
                   // week[i].imagen = it
                    listaImages[i].setImageBitmap(it)
                    listaImages[i].tag = "new_image"
                }
            }
        }
    }

    private fun changeImageClick(listaImages: Array<ImageView>, enabled: Boolean, activity: Activity) {
        for(i in 0..6){
            listaImages[i].isEnabled = enabled
            if(enabled) {
                if(listaImages[i].drawable == null || listaImages[i].tag == "new_image"){
                    listaImages[i].setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.svg_add_image))
                    listaImages[i].tag = "add_image"
                }
            }else{
                if(listaImages[i].drawable != null && listaImages[i].tag == "add_image"){
                    listaImages[i].setImageResource(0)

                }
            }
        }
    }

    private fun configurarDaysWeek(listaText: Array<TextView>, listaImages: Array<ImageView>) {
        val semana = Semana()
        val language = Locale.getDefault().language
        val configuration = semana.obtenerconfig(viewModel.idUsuario, this)
        val listaDays = if(language == "es"){
            arrayOf("L", "M", "X", "J", "V", "S", "D")
        }else {
            arrayOf("M", "T", "W", "T", "F", "S", "S")
        }

        if(configuration == 2){
            for(i in 0..6){
                listaText[i].text = listaDays[i]
            }
        }else if(configuration == 3){
            for(i in 0..6){
                listaText[i].visibility = TextView.GONE
                listaImages[i].visibility = ImageView.VISIBLE
            }
        }
    }

    private fun obtenerImagenes(listaImages: Array<ImageView>): MutableList<Semana> {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var date = monday
        val days = mutableListOf<String>()
        while (!date.isAfter(sunday)) {
            days.add(date.format(formatter))
            date = date.plusDays(1)
        }

        val semana = Semana()
        val diasSemana = mutableListOf<Semana>()
        val imagenes = semana.obtenerImagenes(viewModel.idUsuario, days, this)
        for(i in 0..6){
            val bitmap = viewModel.byteArrayToBitmap(imagenes[i])
            val dia = Semana(days[i], bitmap)
            diasSemana.add(dia)

            if(bitmap != null){
                listaImages[i].setImageBitmap(bitmap)
            }
        }
        return diasSemana
    }
}