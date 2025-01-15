package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.DiaMes
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendarioMensual
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendarioMensualFechas
import com.example.plantea.presentacion.viewModels.CalendarioMensualViewModel
import com.example.plantea.presentacion.viewModels.SingleLiveEvent
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale


class CalendarioMensualActivity: AppCompatActivity() {
    private val viewModel by viewModels<CalendarioMensualViewModel>()

    private lateinit var calendario: RecyclerView
    private lateinit var fechas : RecyclerView
    private lateinit var fechaActual: TextView
    private var listaDays :Array<String> = arrayOf()

    private lateinit var adaptadorCalendario: AdaptadorCalendarioMensual
    private lateinit var adaptadorFechas: AdaptadorCalendarioMensualFechas

    private var isPlanificadorLogged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_mensual)

        //Seccion de fechas
        val btnNuevaFecha = findViewById<MaterialButton>(R.id.nuevaFecha)
        val btnExportPdf = findViewById<MaterialButton>(R.id.exportPdf)

        //Calendario
        val btnSiguienteMes = findViewById<Button>(R.id.image_calendar_siguiente)
        val btnAnteriorMes = findViewById<Button>(R.id.image_calendar_anterior)
        calendario = findViewById(R.id.recycler_calendario)
        fechas = findViewById(R.id.recycler_fechas)
        fechaActual = findViewById(R.id.lbl_mes)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs)

        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
        viewModel.obtenerVistaMes()

        btnSiguienteMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
            viewModel.obtenerVistaMes()
        }

        btnAnteriorMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
            viewModel.obtenerVistaMes()
        }

        btnExportPdf.setOnClickListener {
            exportarPdf()
        }

        btnNuevaFecha.setOnClickListener {
            viewModel.isCalendarioMensual = true
            viewModel.isEditImage = false
            AniadirPictoUtils.initializeDialog(viewModel, this)
        }

        observer()
        viewModel.createPickMedia(viewModel, this)

        isPlanificadorLogged = prefs.getBoolean("PlanificadorLogged", false)
        if(!isPlanificadorLogged){
            btnExportPdf.visibility = View.GONE
            btnNuevaFecha.visibility = View.GONE
        }
    }

    private fun observer() {
        viewModel._fechaActual.observe(this) {
            fechaActual.text = it
        }

        viewModel._dias.observe(this) {

            listaDays = if(CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                arrayOf("L", "M", "X", "J", "V", "S", "D")
            }else if (CommonUtils.isMobile(this) && Locale.getDefault().language == "en"){
                arrayOf("M", "T", "W", "T", "F", "S", "S")
            }else if (!CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                arrayOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
            }else{
                arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            }

            val diaMes = DiaMes()
            val mes =  String.format(Locale.getDefault(), "%02d", CalendarioUtilidades.fechaSeleccionada.monthValue)
            val anio = CalendarioUtilidades.fechaSeleccionada.year
            viewModel.fechas = diaMes.obtenerDiasMes(viewModel.idUsuario,  "$anio-$mes-%", this)

            findViewById<TextView>(R.id.noHayFechas).visibility = if(viewModel.fechas.isEmpty()) View.VISIBLE else  View.GONE

            //Calendario
            val ratio = if(it.size < 42) "7:6" else "1:1"
            val params = calendario.layoutParams as ConstraintLayout.LayoutParams
            params.dimensionRatio = ratio
            calendario.layoutParams = params

            adaptadorCalendario = AdaptadorCalendarioMensual(it, listaDays, viewModel.fechas, viewModel, false)
            calendario.layoutManager = GridLayoutManager(this, 7)
            calendario.adapter = adaptadorCalendario
            calendario.requestLayout()

            //Fechas
            adaptadorFechas = AdaptadorCalendarioMensualFechas(viewModel.fechas, viewModel)
            fechas.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            fechas.adapter = adaptadorFechas
            fechas.requestLayout()
        }

        viewModel._fechaSeleccionada.observe(this) { dia ->
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialogo_fecha)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val titulo = dialog.findViewById<EditText>(R.id.titulo)
            val fecha = dialog.findViewById<TextView>(R.id.fecha)
            val imagen = dialog.findViewById<ShapeableImageView>(R.id.imagen)
            val cardView = dialog.findViewById<MaterialCardView>(R.id.cardView)
            val btnEditar = dialog.findViewById<Button>(R.id.btn_editar)
            val btnBorrar = dialog.findViewById<Button>(R.id.btn_borrar)
            val btnCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
            val borrarIcono = dialog.findViewById<ImageView>(R.id.borrarIcon)
            val card = dialog.findViewById<MaterialCardView>(R.id.card)

            card.setCardBackgroundColor(CommonUtils.getColor(this, dia.color))
            borrarIcono.visibility = ImageView.GONE

            titulo.setText(dia.titulo)
            fecha.text = CalendarioUtilidades.formatoFecha(dia.fecha!!)

            if(dia.imagen != null){
                imagen.setImageBitmap(dia.imagen)
            }else{
                cardView.visibility = ImageView.GONE
            }

            if(!isPlanificadorLogged){
                btnEditar.visibility = View.GONE
                btnBorrar.visibility = View.GONE
            }

            btnBorrar.setOnClickListener {
                borrarDia(dia, dialog)
            }

            btnEditar.setOnClickListener {
                editarDia(dia, dialog, titulo, fecha, imagen, cardView, borrarIcono, btnEditar)
            }

            btnCerrar.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        viewModel._addedFecha.observe(this) {
            val diaMes = DiaMes()
            val imagenBlob = CommonUtils.bitmapToByteArray((it.imagen))
            diaMes.guardarDia(viewModel.idUsuario, it.titulo, imagenBlob, it.color, it.fecha.toString(), this)
            viewModel.fechas.add(it)
            adaptadorFechas.notifyItemInserted(viewModel.fechas.size - 1)
            adaptadorCalendario.notifyDataSetChanged()
            findViewById<TextView>(R.id.noHayFechas).visibility = View.GONE
        }
    }

    private fun borrarDia(dia: DiaMes, dialog: Dialog) {
        val dialogBorrar = Dialog(this)
        dialogBorrar.setContentView(R.layout.dialogo_eliminar_dia_mes)
        dialogBorrar.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBorrar.findViewById<Button>(R.id.btn_eliminar).setOnClickListener {
            val diaMes = DiaMes()
            diaMes.borrarDia(viewModel.idUsuario, dia.fecha.toString(), this)
            dialogBorrar.dismiss()
            dialog.dismiss()

            val positionDia = viewModel.fechas.indexOf(dia)
            viewModel.fechas.remove(dia)
            adaptadorFechas.notifyItemRemoved(positionDia)
            adaptadorCalendario.notifyDataSetChanged()

            if(viewModel.fechas.isEmpty()){
                findViewById<TextView>(R.id.noHayFechas).visibility = View.VISIBLE
            }

//            val positionDiaMes = viewModel._dias.value!!.binarySearch(dia.fecha) + 7
//            adaptadorCalendario.notifyItemChanged(positionDiaMes, dia)

        }

        dialogBorrar.findViewById<ImageView>(R.id.icono_CerrarDialogo).setOnClickListener {
            dialogBorrar.dismiss()
        }

        dialogBorrar.show()
    }

    private fun editarDia(dia: DiaMes, dialog: Dialog, titulo: TextView, fecha: TextView, imagen: ShapeableImageView, cardView: MaterialCardView, borrarIcono:ImageView, buttonEditar: Button) {
        buttonEditar.text = getString(R.string.btn_Guardar)

        titulo.isEnabled = true
        fecha.isClickable = true
        cardView.isClickable = true
        cardView.visibility = ImageView.VISIBLE

        dialog.findViewById<LinearLayout>(R.id.colors2).visibility = View.VISIBLE
        dialog.findViewById<LinearLayout>(R.id.colors).visibility = View.VISIBLE

        viewModel.colorSelected = dia.color!!
        viewModel.buttonsColors(dialog, this, true)

        //edit text default background
        titulo.background = AppCompatResources.getDrawable(this, R.drawable.edittext_underline)
        fecha.background = AppCompatResources.getDrawable(this, R.drawable.edittext_underline)

        CalendarioUtilidades.fechaSeleccionada = dia.fecha!!

        if(dia.imagen != null){
            borrarIcono.visibility = ImageView.VISIBLE
        }else{
            imagen.setBackgroundResource(R.drawable.svg_add_image)
        }

        cardView.setOnClickListener {
            viewModel.isEditImage = true
            viewModel.isCalendarioMensual = false
            AniadirPictoUtils.initializeDialog(viewModel, this)

            viewModel._newImage.observe(this) { //TODO: Solo se actualiza bien la primera vez
                //volver a inicializar la imagen
                val imagen2 = dialog.findViewById<ShapeableImageView>(R.id.imagen)
                imagen2.background = null
                imagen2.setImageBitmap(viewModel._nuevoPicto.value?.imagen)
                borrarIcono.visibility = ImageView.VISIBLE
            }
        }

        fecha.setOnClickListener {
            val picker = viewModel.createCalendar(this)
            picker.addOnPositiveButtonClickListener {
                CalendarioUtilidades.fechaSeleccionada = Instant.ofEpochMilli(picker.selection!!).atZone(ZoneId.systemDefault()).toLocalDate()
                fecha.text = CalendarioUtilidades.formatoFecha(CalendarioUtilidades.fechaSeleccionada)
            }
        }

        borrarIcono.setOnClickListener {
            imagen.setImageResource(0)
            imagen.setBackgroundResource(R.drawable.svg_add_image)
            borrarIcono.visibility = ImageView.GONE
        }

        buttonEditar.setOnClickListener {
            val diaMes = DiaMes()
            val imagenByteArray = if(imagen.drawable != null){
                 CommonUtils.drawableToByteArray(imagen.drawable)
            }else{
                 null
            }

            val positionDia = viewModel.fechas.indexOf(dia)
            if (positionDia != -1) {
                val imageBitmap = if(imagenByteArray != null)
                     CommonUtils.byteArrayToBitmap(imagenByteArray)
                else
                    null

                diaMes.editarDia(viewModel.idUsuario, titulo.text.toString(), imagenByteArray, viewModel.colorSelected, CalendarioUtilidades.fechaSeleccionada.toString(), this)
                val updatedDia = DiaMes(dia.fecha, titulo.text.toString(), viewModel.colorSelected, imageBitmap)
                viewModel.fechas[positionDia] = updatedDia

                adaptadorFechas.notifyItemChanged(positionDia)
                /*val positionDiaMes = viewModel._dias.value!!.binarySearch(dia.fecha) + 7
                adaptadorCalendario.notifyItemChanged(positionDiaMes)*/
                adaptadorCalendario.notifyDataSetChanged()
                dialog.dismiss()
            }

        }
    }

    private fun exportarPdf(){
        try {
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val filename = "Calendario_PlanTEA.pdf"
            val outputPath = File(downloadsDirectory, filename).absolutePath
            val pdfDocument = PdfDocument()
            val pageWidth = 1485
            val pageHeight = 1050
            val pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            //Calendario
            val calendarioPDF = RecyclerView(this)
            val adaptador = AdaptadorCalendarioMensual(viewModel._dias.value!!, listaDays, viewModel.fechas, viewModel, true)
            calendarioPDF.layoutManager = GridLayoutManager(this, 7)
            calendarioPDF.adapter = adaptador
            var widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            calendarioPDF.measure(widthSpec, heightSpec)
            calendarioPDF.requestLayout()

            //Blobs
            val bitmap = CommonUtils.drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.blob_pdf_2, null)!!)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 450, 450, false)
            val bitmap2 = CommonUtils.drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.blob_pdf, null)!!)
            val scaledBitmap2 = Bitmap.createScaledBitmap(bitmap2, 300, 300, false)
            canvas.drawBitmap(scaledBitmap, 0f, pageHeight - 450f, null)
            canvas.drawBitmap(scaledBitmap2, pageWidth - 300f, 0f, null)

            //Mover el canvas para el calendario

            if(viewModel.fechas.isEmpty()){
                //canvas in the middle
                canvas.save()
                val x = (pageWidth - calendarioPDF.measuredWidth) / 2
                canvas.translate(x.toFloat(), 150f)
                calendarioPDF.draw(canvas)
                canvas.restore()
            }else{
                canvas.save()
                canvas.translate(100f, 150f)
                calendarioPDF.draw(canvas)
                canvas.restore()
            }


            //Fecha
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.BLACK
            paint.textSize = 65f
            paint.typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold)
            val mes = CalendarioUtilidades.fechaSeleccionada.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()).uppercase()
            val anio = CalendarioUtilidades.fechaSeleccionada.year
            val x = (pageWidth - paint.measureText("$mes $anio")) / 2
            canvas.drawText("$mes $anio", x, 120f, paint)

            //Logo
            val logo = CommonUtils.drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.logo_plantea2, null)!!)
            val scaledLogo = Bitmap.createScaledBitmap(logo, 80, 60, false)
            canvas.drawBitmap(scaledLogo, 20f, 20f, null)
            paint.textSize = 40f
            paint.typeface = ResourcesCompat.getFont(this, R.font.tiltneon)
            canvas.drawText(" PlanTEA", 110f, 60f, paint)

            //Dias importantes
            if(viewModel.fechas.isNotEmpty()){
                val fechasPDF = RecyclerView(this)
                val adaptadorFechas = AdaptadorCalendarioMensualFechas(viewModel.fechas, viewModel)

                val params = ViewGroup.LayoutParams(
                    100, // Ancho en píxeles
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                fechasPDF.layoutParams = params
                fechasPDF.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                fechasPDF.adapter = adaptadorFechas

                val widthSpec2 = View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY)
                val heightSpec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                fechasPDF.measure(widthSpec2, heightSpec2)
                fechasPDF.layout(0, 0, fechasPDF.measuredWidth, fechasPDF.measuredHeight)

                canvas.translate(1000f, 250f)
                fechasPDF.draw(canvas)
            }

            //Guardar el documento
            pdfDocument.finishPage(page)
            val file = FileOutputStream(outputPath)
            pdfDocument.writeTo(file)
            pdfDocument.close()
            file.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            Toast.makeText(this, "PDF exportado en la carpeta de descargas", Toast.LENGTH_SHORT).show()
        }

    }
}