package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.gestores.GestionMes
import com.example.plantea.dominio.objetos.DiaMes
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
    private lateinit var dialog: Dialog

    private var isPlanificadorLogged = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_mensual)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        //Seccion de fechas
        val btnNuevaFecha = findViewById<MaterialButton>(R.id.nuevaFecha)
        val btnExportPdf = findViewById<MaterialButton>(R.id.exportPdf)
        val atras : Button? = findViewById(R.id.atras)

        //Calendario
        val btnSiguienteMes = findViewById<Button>(R.id.image_calendar_siguiente)
        val btnAnteriorMes = findViewById<Button>(R.id.image_calendar_anterior)
        calendario = findViewById(R.id.recycler_calendario)
        fechas = findViewById(R.id.recycler_fechas)
        fechaActual = findViewById(R.id.lbl_mes)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !prefs.getBoolean("darkMode", false)
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
           nuevaFecha()
        }

        atras?.setOnClickListener {
            finish()
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
        viewModel.mdFechaActual.observe(this) {
            fechaActual.text = it
        }

        viewModel.mdDias.observe(this) {

            listaDays = if(CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                arrayOf("L", "M", "X", "J", "V", "S", "D")
            }else if (CommonUtils.isMobile(this) && Locale.getDefault().language == "en"){
                arrayOf("M", "T", "W", "T", "F", "S", "S")
            }else if (!CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                arrayOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
            }else{
                arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            }

            val gDiaMes = GestionMes()
            val mes =  String.format(Locale.getDefault(), "%02d", CalendarioUtilidades.fechaSeleccionada.monthValue)
            val anio = CalendarioUtilidades.fechaSeleccionada.year
            viewModel.fechas = gDiaMes.obtenerDiasMes(viewModel.idUsuario,  "$anio-$mes-%", this)

            findViewById<TextView>(R.id.noHayFechas).visibility = if(viewModel.fechas.isEmpty()) View.VISIBLE else  View.GONE

            //Calendario
            val ratio = when (it.size) {
                35 -> "7:6"
                49 -> "7:8"
                else -> "1:1"
            }

            val params = calendario.layoutParams as ConstraintLayout.LayoutParams
            params.dimensionRatio = ratio
            calendario.layoutParams = params

            adaptadorCalendario = AdaptadorCalendarioMensual(it, listaDays, viewModel.fechas, viewModel, false)
            calendario.layoutManager = GridLayoutManager(this, 7)
            calendario.adapter = adaptadorCalendario
            calendario.requestLayout()

            //Fechas
            adaptadorFechas = AdaptadorCalendarioMensualFechas(viewModel.fechas, viewModel, false)
            fechas.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            fechas.adapter = adaptadorFechas
            fechas.requestLayout()
        }

        viewModel.mdFechaSeleccionada.observe(this) { dia ->
            if(isPlanificadorLogged){
                dialogoMostrarDia(dia)
            }else{
                dialogoMostrarDiaTEA(dia)
            }
        }

        viewModel.seNewImage.observe(this) {
            //volver a inicializar la imagen
            val imagen2 = dialog.findViewById<ShapeableImageView>(R.id.imagen)
            imagen2.background = null
            imagen2.setImageBitmap(viewModel.seNuevoPicto.value?.imagen)
        }

        viewModel.seAddedFecha.observe(this) { dia ->
            val gDiaMes = GestionMes()
            val imagenBlob = CommonUtils.bitmapToByteArray((dia.imagen))
            gDiaMes.guardarDia(viewModel.idUsuario, dia.nombre, imagenBlob, dia.color, dia.fecha.toString(), this)
            viewModel.fechas.add(dia)
            viewModel.fechas.sortBy { it.fecha }
            adaptadorFechas.notifyItemInserted(viewModel.fechas.indexOf(dia))

            val positionDiaMes = viewModel.mdDias.value!!.binarySearch(dia.fecha) + 7
            adaptadorCalendario.notifyItemChanged(positionDiaMes, "update")

            findViewById<TextView>(R.id.noHayFechas).visibility = View.GONE
        }

        viewModel.seNuevaFecha.observe(this){
            nuevaFecha()
        }
    }

    private fun nuevaFecha(){
        viewModel.isCalendarioMensual = true
        viewModel.isEditImage = false
        AniadirPictoUtils.initializeDialog(viewModel, this)
    }

    private fun dialogoMostrarDia(dia : DiaMes){
        dialog = Dialog(this)
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

        card.setCardBackgroundColor(resources.getColor(viewModel.getColorID(dia.color, this), null))
        borrarIcono.visibility = ImageView.GONE

        titulo.setText(dia.nombre)
        fecha.text = CalendarioUtilidades.formatoFecha(dia.fecha!!)

        if(dia.imagen != null){
            imagen.setImageBitmap(dia.imagen)
        }else{
            cardView.visibility = ImageView.GONE
        }

        if(!isPlanificadorLogged){
            btnEditar.visibility = View.INVISIBLE
            btnBorrar.visibility = View.GONE
        }

        btnBorrar.setOnClickListener {
            borrarDia(dia, dialog)
        }

        btnEditar.setOnClickListener {
            editarDia(dia, titulo, fecha, imagen, cardView, borrarIcono, btnEditar)
        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun dialogoMostrarDiaTEA(dia : DiaMes){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_fecha_tea)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val titulo = dialog.findViewById<TextView>(R.id.titulo)
        val fecha = dialog.findViewById<TextView>(R.id.fecha)
        val imagen = dialog.findViewById<ShapeableImageView>(R.id.imagen)
        val cardView = dialog.findViewById<MaterialCardView>(R.id.cardView)

        //chage background color of dialog
        val card = dialog.findViewById<ConstraintLayout>(R.id.dialogo_fecha_tea)
        card.backgroundTintList = resources.getColorStateList(viewModel.getColorID(dia.color, this), null)

        titulo.text = dia.nombre
        fecha.text = CalendarioUtilidades.formatoFecha(dia.fecha!!)

        if(dia.imagen != null){
            imagen.setImageBitmap(dia.imagen)
        }else{
            cardView.visibility = ImageView.GONE
            titulo.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val params = titulo.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, CommonUtils.dpToPx(30, resources), 0, 0)
            titulo.layoutParams = params
            fecha.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        dialog.show()
    }

    private fun borrarDia(dia: DiaMes, dialog: Dialog) {
        val dialogBorrar = Dialog(this)
        dialogBorrar.setContentView(R.layout.dialogo_eliminar_dia_mes)
        dialogBorrar.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBorrar.findViewById<Button>(R.id.btn_cancelar).setOnClickListener{
            dialogBorrar.dismiss()
        }

        dialogBorrar.findViewById<Button>(R.id.btn_eliminar).setOnClickListener {
            val gDiaMes = GestionMes()
            gDiaMes.borrarDia(viewModel.idUsuario, dia.fecha.toString(), this)
            dialogBorrar.dismiss()
            dialog.dismiss()

            val positionDia = viewModel.fechas.indexOf(dia)
            viewModel.fechas.remove(dia)
            adaptadorFechas.notifyItemRemoved(positionDia)
            val positionDiaMes = viewModel.mdDias.value!!.binarySearch(dia.fecha) + 7
            adaptadorCalendario.notifyItemChanged(positionDiaMes, "update")

            if(viewModel.fechas.isEmpty()){
                findViewById<TextView>(R.id.noHayFechas).visibility = View.VISIBLE
            }
        }

        dialogBorrar.findViewById<ImageView>(R.id.icono_CerrarDialogo).setOnClickListener {
            dialogBorrar.dismiss()
        }

        dialogBorrar.show()
    }

    private fun editarDia(dia: DiaMes, titulo: TextView, fecha: TextView, imagen: ShapeableImageView, cardView: MaterialCardView, borrarIcono:ImageView, buttonEditar: Button) {
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

            viewModel.seNewImage.observe(this) {
                //volver a inicializar la imagen
                val imagen2 = dialog.findViewById<ShapeableImageView>(R.id.imagen)
                imagen2.background = null
                imagen2.setImageBitmap(viewModel.seNuevoPicto.value?.imagen)
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
            val gDiaMes = GestionMes()
            val imagenByteArray = if(imagen.drawable == null){
                null
            }else{
                CommonUtils.drawableToByteArray(imagen.drawable)
            }

            val positionDia = viewModel.fechas.indexOf(dia)
            if (positionDia != -1) {
                val imageBitmap = if(imagenByteArray == null)
                    null
                else
                    CommonUtils.byteArrayToBitmap(imagenByteArray)

                val fechaNueva = if(dia.fecha != CalendarioUtilidades.fechaSeleccionada){
                     CalendarioUtilidades.fechaSeleccionada
                }else{
                    dia.fecha
                }

                gDiaMes.editarDia(viewModel.idUsuario, titulo.text.toString(), imagenByteArray, viewModel.colorSelected, fechaNueva.toString(), dia.fecha.toString(),this)

                val updatedDia = DiaMes(fechaNueva, titulo.text.toString(), viewModel.colorSelected, imageBitmap)
                viewModel.fechas[positionDia] = updatedDia
                viewModel.fechas.sortBy { it.fecha }

                adaptadorFechas.notifyDataSetChanged()

                val positionDiaMes = viewModel.mdDias.value!!.binarySearch(dia.fecha) + 7
                adaptadorCalendario.notifyItemChanged(positionDiaMes, "update")

                val positionDiaMes2 = viewModel.mdDias.value!!.binarySearch(fechaNueva) + 7
                adaptadorCalendario.notifyItemChanged(positionDiaMes2, "update")
                dialog.dismiss()
            }

        }
    }

    private fun exportarPdfOLD(){
        try {
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var filename = "Calendario_PlanTEA.pdf"
            var counter = 1

            while (File(downloadsDirectory, filename).exists()) {
                filename = "Calendario_PlanTEA_$counter.pdf"
                counter++
            }

            val outputPath = File(downloadsDirectory, filename).absolutePath

            val metrics = resources.displayMetrics
            val scaledX = metrics.xdpi/ 100
            val scaledY = metrics.ydpi / 100

            val pdfDocument = PdfDocument()
            val pageWidth = (675 * scaledX).toInt()
            val pageHeight = (525 * scaledY).toInt()
            val pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            //Calendario
            val calendarioPDF = RecyclerView(this)
            val adaptador = AdaptadorCalendarioMensual(viewModel.mdDias.value!!, listaDays, viewModel.fechas, viewModel, true)
            calendarioPDF.layoutManager = GridLayoutManager(this, 7)
            calendarioPDF.adapter = adaptador
            val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            calendarioPDF.measure(widthSpec, heightSpec)
            calendarioPDF.requestLayout()

            //Mover el canvas para el calendario
            if(viewModel.fechas.isEmpty()){
                //canvas in the middle
                canvas.save()
                val x = (pageWidth - calendarioPDF.measuredWidth) / 2
                canvas.translate(x.toFloat(), 68*scaledY)
                calendarioPDF.draw(canvas)
                canvas.restore()
            }else{
                canvas.save()
                canvas.translate(45*scaledX, 68*scaledY)
                calendarioPDF.draw(canvas)
                canvas.restore()
            }

            //Fecha
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.BLACK
            paint.textSize = 30*scaledX
            paint.typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold)
            val mes = CalendarioUtilidades.fechaSeleccionada.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()).uppercase()
            val anio = CalendarioUtilidades.fechaSeleccionada.year
            val x = (pageWidth - paint.measureText("$mes $anio")) / 2
            canvas.drawText("$mes $anio", x, 55*scaledY, paint)

            //Logo
            val logo = CommonUtils.drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.logo_plantea2, null)!!)
            val scaledLogo = Bitmap.createScaledBitmap(logo, (38*scaledX).toInt(), (30*scaledY).toInt(), false)
            canvas.drawBitmap(scaledLogo, 9*scaledX, 9*scaledX, null)
            paint.textSize = 18*scaledX
            paint.typeface = ResourcesCompat.getFont(this, R.font.tiltneon)
            canvas.drawText(" PlanTEA", 50*scaledX, 30*scaledY, paint)

            //Dias importantes
            if(viewModel.fechas.isNotEmpty()){
                val fechasPDF = RecyclerView(this)
                val adaptadorFechas = AdaptadorCalendarioMensualFechas(viewModel.fechas, viewModel, true)

                fechasPDF.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                fechasPDF.adapter = adaptadorFechas

                val widthSpec2 = View.MeasureSpec.makeMeasureSpec((160*scaledX).toInt(), View.MeasureSpec.EXACTLY)
                val heightSpec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                fechasPDF.measure(widthSpec2, heightSpec2)
                fechasPDF.layout(0, 0, fechasPDF.measuredWidth, fechasPDF.measuredHeight)

                canvas.translate(calendarioPDF.measuredWidth + 70*scaledX, 113*scaledY)
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
            val message = getString(R.string.toast_pdf_exportado)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportarPdf(){
        var outputPath = ""
        var filename = "Calendario_PlanTEA.pdf"
        try {
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var counter = 1
            var pageNumber = 1

            while (File(downloadsDirectory, filename).exists()) {
                filename = "Calendario_PlanTEA_$counter.pdf"
                counter++
            }

            outputPath = File(downloadsDirectory, filename).absolutePath

            val metrics = resources.displayMetrics
            val scaledX = metrics.xdpi/ 100
            val scaledY = metrics.ydpi / 100

            val pdfDocument = PdfDocument()
            val pageWidth = (675 * scaledX).toInt()
            val pageHeight = (525 * scaledY).toInt()
            val pageInfo = PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            if(viewModel.fechas.isNotEmpty()){
                //chucks of arrayList
                val chunks = viewModel.fechas.chunked(5).map { ArrayList(it) }
                for(bloque in chunks){
                    //Calendario
                    val calendarioPDF = RecyclerView(this)
                    val adaptador = AdaptadorCalendarioMensual(viewModel.mdDias.value!!, listaDays, viewModel.fechas, viewModel, true)
                    calendarioPDF.layoutManager = GridLayoutManager(this, 7)
                    calendarioPDF.adapter = adaptador
                    val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    calendarioPDF.measure(widthSpec, heightSpec)
                    calendarioPDF.requestLayout()

                    //Mover el canvas para el calendario
                    if(viewModel.fechas.isEmpty()){
                        //canvas in the middle
                        canvas.save()
                        val x = (pageWidth - calendarioPDF.measuredWidth) / 2
                        canvas.translate(x.toFloat(), 68*scaledY)
                        calendarioPDF.draw(canvas)
                        canvas.restore()
                    }else{
                        canvas.save()
                        canvas.translate(45*scaledX, 68*scaledY)
                        calendarioPDF.draw(canvas)
                        canvas.restore()
                    }

                    //Fecha
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    paint.color = Color.BLACK
                    paint.textSize = 30*scaledX
                    paint.typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold)
                    val mes = CalendarioUtilidades.fechaSeleccionada.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()).uppercase()
                    val anio = CalendarioUtilidades.fechaSeleccionada.year
                    val x = (pageWidth - paint.measureText("$mes $anio")) / 2
                    canvas.drawText("$mes $anio", x, 55*scaledY, paint)

                    //Logo
                    val logo = CommonUtils.drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.logo_plantea2, null)!!)
                    val scaledLogo = Bitmap.createScaledBitmap(logo, (38*scaledX).toInt(), (30*scaledY).toInt(), false)
                    canvas.drawBitmap(scaledLogo, 9*scaledX, 9*scaledX, null)
                    paint.textSize = 18*scaledX
                    paint.typeface = ResourcesCompat.getFont(this, R.font.tiltneon)
                    canvas.drawText(" PlanTEA", 50*scaledX, 30*scaledY, paint)

                    //Dias importantes
                    val fechasPDF = RecyclerView(this)
                    val adaptadorFechas = AdaptadorCalendarioMensualFechas(bloque, viewModel, true)

                    fechasPDF.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    fechasPDF.adapter = adaptadorFechas

                    val widthSpec2 = View.MeasureSpec.makeMeasureSpec((160*scaledX).toInt(), View.MeasureSpec.EXACTLY)
                    val heightSpec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    fechasPDF.measure(widthSpec2, heightSpec2)
                    fechasPDF.layout(0, 0, fechasPDF.measuredWidth, fechasPDF.measuredHeight)

                    canvas.translate(calendarioPDF.measuredWidth + 70*scaledX, 113*scaledY)

                    //if its not the last page draw the first five, create a new page
                    fechasPDF.draw(canvas)
                    pdfDocument.finishPage(page)

                    if(pageNumber < chunks.size){
                        pageNumber++
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        canvas.translate(0f, 0f)
                    }
                }
            }

            val file = FileOutputStream(outputPath)
            pdfDocument.writeTo(file)
            pdfDocument.close()
            file.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            val message = getString(R.string.toast_pdf_exportado)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            CommonUtils.mostrarNotificacionPDF(this, outputPath, filename)
        }
    }
}