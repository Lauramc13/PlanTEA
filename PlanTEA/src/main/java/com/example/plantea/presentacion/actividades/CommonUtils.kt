package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.JsonPictogramaItem
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.persistencia.ApiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLEncoder
import java.util.Locale
import android.app.NotificationChannel
import android.app.NotificationManager

class CommonUtils{
    companion object {
        val handler = Handler(Looper.getMainLooper())
        lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
        private val wordToLemmaEs = mutableMapOf<String, MutableList<String>>()
        private val wordToLemmaEn = mutableMapOf<String, MutableList<String>>()

        val String.toPreservedByteArray: ByteArray
            get() {
                return this.toByteArray(Charsets.ISO_8859_1)
            }

        val ByteArray.toPreservedString: String
            get() {
                return String(this, Charsets.ISO_8859_1)
            }

        object PreferencesHelper {
            private const val PREF_NAME = "Preferencias"

            fun isFirstTime(context: Context, key: String): Boolean {
                val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                return prefs.getBoolean(key, true)
            }

            fun setFirstTime(context: Context, key: String, isFirstTime: Boolean) {
                val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(key, isFirstTime).apply()
            }
        }

        /**
         * Función que comprueba si el dispositivo es un móvil o una tablet
         */
        fun isMobile(context: Context): Boolean {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            val density = displayMetrics.density
            val widthPixels = displayMetrics.widthPixels
            val heightPixels = displayMetrics.heightPixels
            val shortestDimensionInPixels = (widthPixels.coerceAtMost(heightPixels) /density).toInt()

            return shortestDimensionInPixels < 700
        }

        /**
         * Función que calcula el número de columnas que se mostrarán en el RecyclerView del cuaderno
         */
        fun getGridValueContainer(vista: View, context: Context?, recyclerView: RecyclerView, constraintLayout: ConstraintLayout, widthItem: Int, widthItemTablet: Int){
            vista.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    vista.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    vista.width
                }
            })

            val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Remove the listener to avoid multiple calls
                    constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // Get the width of the ConstraintLayout
                    val density = context?.resources?.displayMetrics?.density ?: 1f
                    val widthRecyclerView = (constraintLayout.width / density).toInt()

                    val gridValue: Int = if (context?.let { isMobile(it) } == true) {
                        widthRecyclerView / widthItem
                    } else {
                        widthRecyclerView / widthItemTablet
                    }

                    if(gridValue == 0){
                        recyclerView.layoutManager = GridLayoutManager(context, 1)
                    }else{
                        recyclerView.layoutManager = GridLayoutManager(context, gridValue)
                    }
                }
            }

            // Add the global layout listener
            constraintLayout.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }

        /**
         * Función que realiza la llamada a la API para obtener los pictogramas
         */
        private fun getRetrofitBuilder(): ApiInterface {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.arasaac.org/api/")
                .build()
                .create(ApiInterface::class.java)
        }

        /**
         * Función que realiza la llamada a la API para obtener las imágenes de los pictogramas
         */
        private fun getRetrofitBuilderImg(): ApiInterface {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://static.arasaac.org/pictograms/")
                .build()
                .create(ApiInterface::class.java)
        }

        /**
         * Función que realiza la lógica para obtener los datos de la API
         */
        fun getDataApi(query: String): MutableMap<Bitmap, Pair<String, Int>> {
            val retrofitBuilderImg = getRetrofitBuilderImg()

            val dict = mutableMapOf<Bitmap, Pair<String, Int>>()
            val listaIds = mutableListOf<Int>()
            val listaTitulos = mutableListOf<String>()

             val cleanQuery  = query.lowercase().replace(",", "").replace(".", "%2E")
            pedirDatosAPI(cleanQuery, listaIds, listaTitulos)

            if(listaIds.isEmpty()){
                val lemmatizedQuery = lemmatizar(cleanQuery)
                for (word in lemmatizedQuery) {
                    pedirDatosAPI(word, listaIds, listaTitulos)
                }
            }

            for (id in listaIds) {
                val keyword = listaTitulos[listaIds.indexOf(id)]
                try {
                    val retrofitImage = retrofitBuilderImg.getImage(id.toString())
                    val response = retrofitImage.execute()

                    if (response.isSuccessful) {
                        val bitmap = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        dict[bitmap] = Pair(keyword, id)
                    } else {
                        Log.d("ERROR", "Error de la llamada a las imagenes")
                    }
                } catch (e: IOException) {
                    Log.d("ERROR", e.toString())
                }
            }

            val image = textAsBitmap(query)
            val idImageCreated = wordToInt(query)
            dict[image] = Pair(query, idImageCreated)

            return dict
        }


        private fun wordToInt(word: String): Int {
            var id= 0
            val base = 128

            for (char in word) {
                id = id * base + char.code
            }
            return id
        }

        private fun intToWord(id: Int): String {
            var currentId = id
            val base = 128
            val wordBuilder = StringBuilder()

            while (currentId > 0) {
                // Get the last encoded character's ASCII value
                val charCode = (currentId % base).toInt()
                wordBuilder.append(charCode.toChar())

                currentId /= base
            }

            // The word is built backwards, so reverse it
            return wordBuilder.reverse().toString()
        }

        /**
         * Función que realiza la lematización del verbo en español o en inglés
         */
        private fun lemmatizar(query: String): MutableList<String> {
            return if (Locale.getDefault().language == "es") {
                wordToLemmaEs[query] ?: mutableListOf(query)
            } else {
                wordToLemmaEn[query] ?: mutableListOf(query)
            }
        }

        private fun pedirDatosAPI(query: String, listaIds: MutableList<Int>, listaTitulos: MutableList<String>) {
            val retrofitBuilder = getRetrofitBuilder()

            val encodedQuery = encodeQuery(query)

            try {
                val language = Locale.getDefault().language
                var retrofitData: retrofit2.Call<List<JsonPictogramaItem>>
                retrofitData = retrofitBuilder.getData(encodedQuery, language)
                var response = retrofitData.execute()
                if (response.isSuccessful) {
                    receiveDataFromAPI(response, query, listaIds, listaTitulos)
                }else{
                    //intentar la busqueda completa
                    retrofitData = retrofitBuilder.getAllData(query, language)
                    response = retrofitData.execute()
                    if(response.isSuccessful) {
                        receiveDataFromAPI(response, query, listaIds, listaTitulos)
                    }else{
                        Log.d("ERROR", "Error de la llamada a la API")
                    }
                }

            } catch (e: IOException) {
                Log.d("ERROR", e.toString())
            }
        }


        private fun encodeQuery(query: String): String {
            val queryEncoded = URLEncoder.encode(query, "utf-8")
            if(queryEncoded.contains(".")){
                return queryEncoded.replace(".", "%2E")
            }

            return queryEncoded
        }

        private fun receiveDataFromAPI(response: retrofit2.Response<List<JsonPictogramaItem>>, query: String, listaIds: MutableList<Int>, listaTitulos: MutableList<String>){
            val responseBody = response.body()

            if (responseBody != null) {
                for (jsonData in responseBody) {
                    val isArticulo = jsonData.tags.contains("article")
                    val isPronombre = jsonData.tags.contains("pronoun")
                    val isArticuloQuery = checkArticulos(query)
                    val isPronombreQuery = checkPronombres(query)

                    if ((isArticulo && isArticuloQuery) || (isPronombreQuery && isPronombre) || (!isArticuloQuery && !isPronombreQuery)) {
                        val id = jsonData._id
                        listaIds.add(id)
                        val keyword = jsonData.keywords[0].keyword
                        listaTitulos.add(keyword)
                    }
                }
            }
        }

        private fun textAsBitmap(text: String?): Bitmap {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = 100f
            paint.color = Color.BLACK
            paint.textAlign = Paint.Align.LEFT
            val textBounds = Rect()
            paint.getTextBounds(text, 0, text!!.length, textBounds)
            var width = (paint.measureText(text) + 0.5f).toInt()

            if (width > 300) {
                val textSizePaint = 300f / width * 100
                paint.textSize = textSizePaint
                width = 300
            }
            val image = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
            try {
                val canvas = Canvas(image)
                canvas.drawText(text,  (300 - width) / 2f, 150f - textBounds.exactCenterY(), paint)
            }catch (e: Exception){
                Log.d("ERROR", e.toString())
            }

            return image
        }

        fun hideKeyboard(context: Context, view: View){
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
            val cw = ContextWrapper(context)
            val dirImages = cw.getDir("Imagenes", AppCompatActivity.MODE_PRIVATE)
            val myPath = File(dirImages, "$nombre.png")
            val fos: FileOutputStream?
            try {
                val resizedBitmap = Bitmap.createScaledBitmap(imagen, 700, 700, false)
                fos = FileOutputStream(myPath)
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 10, fos) // calidad a 0 imagen mas pequeña
                fos.flush()
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            return myPath.absolutePath
        }

         fun getPathFromUri(context: Context, uri: Uri): String {
            val filePath: String?
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            if (cursor == null) {
                filePath = uri.path
            } else {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                filePath = cursor.getString(index)
                cursor.close()
            }
            return filePath ?: ""
        }

        private fun checkArticulos(query: String): Boolean{
            val articulos = listOf("el", "la", "los", "las", "un", "una", "unos", "unas")
            return articulos.any { it.equals(query.lowercase(Locale.getDefault()), ignoreCase = true) }
        }

        private fun checkPronombres(query: String): Boolean{
            val pronombres = listOf("yo", "tú", "él", "ella", "nosotros", "nosotras", "vosotros", "vosotras", "ellos", "ellas", "ustedes", "usted")
            return pronombres.any { it.equals(query.lowercase(Locale.getDefault()), ignoreCase = true) }
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun isPortrait(activity: Activity): Boolean {
            return activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }

        fun isDarkMode(activity: Activity): Boolean {
            return Configuration.UI_MODE_NIGHT_YES == activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        }


        fun loadLemmatizer(language: String, context: Context) {
            if(wordToLemmaEn.isNotEmpty() && language == "en" || wordToLemmaEs.isNotEmpty() && language == "es"){
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                try{
                    withContext(Dispatchers.IO) {
                        try {
                            val dictLemmatizer: InputStream = context.assets.open("lemmatization-$language.txt")
                            for (line in dictLemmatizer.bufferedReader().readLines()) {
                                val parts = line.split("\t")
                                if (parts.size == 2) {
                                    val lemma = parts[0]
                                    val word = parts[1]

                                    if (language == "es") {
                                        if (wordToLemmaEs.containsKey(word)) {
                                            wordToLemmaEs[word]?.add(lemma)
                                        } else {
                                            wordToLemmaEs[word] = mutableListOf(lemma)
                                        }
                                    } else {
                                        if (wordToLemmaEn.containsKey(word)) {
                                            wordToLemmaEn[word]?.add(lemma)
                                        } else {
                                            wordToLemmaEn[word] = mutableListOf(lemma)
                                        }
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }

        fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
            val contentResolver: ContentResolver = context.contentResolver
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                    Bitmap.createBitmap(BitmapFactory.decodeStream(stream))
                }
                bitmap
            }
        }

        fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
            val outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }

        fun drawableToByteArray(drawable: Drawable): ByteArray {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun byteArrayToDrawableWithCorner(byteArray: ByteArray?, context: Context): Drawable? {
            val bitmap = byteArrayToBitmap(byteArray)

            bitmap?.let {
                val dpValue = 50f // 20dp corner radius
                val pxValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics)

                val roundedDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, it)
                roundedDrawable.cornerRadius = pxValue
                return roundedDrawable
            }

            return null
        }

        fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
            return if(byteArray == null || byteArray.isEmpty()){
                null
            }else{
                try {
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    Bitmap.createScaledBitmap(bitmap, 300, 300, true)
                }catch (e: Exception){
                    null
                }

            }
        }

        fun getImagenAPI(id: Int?): Bitmap? {
            val retrofitBuilderImg = getRetrofitBuilderImg()
            var bitmap : Bitmap? = null

            try {
                val retrofitImage = retrofitBuilderImg.getImage(id.toString())
                val response = retrofitImage.execute()

                if (response.isSuccessful) {
                    bitmap = BitmapFactory.decodeStream(response.body()!!.byteStream())
                } else if(response.code() == 404){
                    val titulo = intToWord(id!!)
                    bitmap = textAsBitmap(titulo)
                }else {
                    Log.d("ERROR", "Error de la llamada a las imagenes")
                }
            } catch (e: IOException) {
                Log.d("ERROR", e.toString())
            }
            return bitmap
        }

        fun formatTime(input: String): String {
            val parts = input.split(":")
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()

            return if (hours > 0) {
                "$hours:${String.format(Locale.getDefault(), "%02d", minutes)}h"
            } else {
                "$minutes" + "m"
            }
        }

        fun formatTimeSeconds(input: String): Int {
            val parts = input.split(":")
            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()

            return minutes * 60 + seconds

        }

        fun guardarPDF(context: Context, title: String, listaPictogramas: ArrayList<Pictograma>) {
            // Create a new PDF document
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val baseFilename = "Traduccion.pdf"
            var counter = 1
            val pdfDocument = PdfDocument()
            var filename = if( title != "") "$title.pdf" else baseFilename

            while (File(downloadsDirectory, filename).exists()) {
                filename = if (title != "") "${title}_$counter.pdf" else "Traduccion_$counter.pdf"
                counter++
            }

            val outputPath = File(downloadsDirectory, filename).absolutePath

            // Create a new page and
            var columna = 0
            var fila = 0
            val maxFilasPorPagina = 6
            var pageNumber = 1

            // join pictograma.titulo for each pictograma in listaPictogramas and when there is a . add a new line
            val frases = ArrayList<String>()
            var currentFrase = ""
            for (pictograma in listaPictogramas) {
                currentFrase += pictograma.titulo + " "
                Log.d("Frase", currentFrase)

                if (pictograma.titulo!!.endsWith(".")) {
                    frases.add(currentFrase)
                    currentFrase = ""
                }else if(pictograma == listaPictogramas[listaPictogramas.size-1]){
                    frases.add(currentFrase)
                }
            }

            try {
                val pageInfo = PdfDocument.PageInfo.Builder(2480, 3508, 1).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas

                val paint = Paint()
                paint.color = Color.BLACK
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))

                var top = 200f

                if(title != ""){
                    paint.textSize = 100f
                    val textWidth = paint.measureText(title.uppercase())
                    val textX = (2480 - textWidth) / 2 // para calcular el centro del texto
                    canvas.drawText(title.uppercase(), textX, 200f, paint)
                    top = 300f
                }

                var contadorFrase = 0
                for (pictograma in listaPictogramas) {
                    val bitmap = pictograma.imagen
                    val imageWidth = bitmap?.width?.toFloat() ?: 0f
                    val imageX = columna * 400f + (400f - imageWidth) / 2

                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, imageX+50f, fila*520f+top, null)
                    }

                    paint.textSize = 50f
                    val textWidth = paint.measureText(pictograma.titulo!!)

                    val textX = columna * 400f + (400f - textWidth) / 2 // para calcular el centro del texto
                    canvas.drawText(pictograma.titulo!!, textX+50f, (fila*520f)+350f+top, paint)

                    val shouldDrawNextLine = pictograma.titulo!!.endsWith(".")
                    val isFirstPictograma = pictograma == listaPictogramas[0]

                    try{
                        if (isFirstPictograma) {
                            canvas.drawText(frases[contadorFrase], 100f, fila * 520f + top - 30, paint)
                            if (shouldDrawNextLine && pictograma != listaPictogramas[listaPictogramas.size - 1]) {
                                canvas.drawText(frases[contadorFrase+1], 100f, (fila + 1) * 520f + top - 30, paint)
                                contadorFrase++
                            }
                            contadorFrase++
                        } else if (shouldDrawNextLine && frases.size != 2) {
                            canvas.drawText(frases[contadorFrase], 100f, (fila + 1) * 520f + top - 30, paint)
                            contadorFrase++
                        }
                    }catch (e: Exception){
                        Log.d("ERROR", e.toString())
                    }

                    if (columna == 5 || pictograma.titulo!!.endsWith(".") ) {
                        columna = 0
                        fila++
                        if (fila >= maxFilasPorPagina) {
                            pdfDocument.finishPage(page)
                            pageNumber++
                            val newPageInfo = PdfDocument.PageInfo.Builder(2480, 3508, pageNumber).create()
                            page = pdfDocument.startPage(newPageInfo)
                            canvas = page.canvas
                            fila = 0
                            columna = 0
                            top = 200f
                        }

                    }else{
                        columna++
                    }

                }

                pdfDocument.finishPage(page)
                val file = FileOutputStream(outputPath)
                pdfDocument.writeTo(file)
                pdfDocument.close()
                file.close()

                Log.d("PDF", "PDF creado en $outputPath")
                val message = ContextCompat.getString(context, R.string.toast_pdf_creado)
                Toast.makeText(context, message + filename, Toast.LENGTH_SHORT).show()

                mostrarNotificacionPDF(context, outputPath, filename)

            } catch (e: Exception) {
                Log.e("ERROR", "Error creating PDF: ${e.message}", e)
            }
        }

        fun mostrarNotificacionPDF(context: Context, path: String, filename: String) {

            val channelId = "pdf_channel"

            val file = File(path)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 🔔 Canal (Android 8+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "PDFs generados",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_plantea2) // ⚠️ pon un icono válido
                .setContentTitle("PDF listo")
                .setContentText("Toca para abrir $filename")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        }


        fun getColor(context: Context, color: String?): Int {
            return when(color) {
                "red" -> ContextCompat.getColor(context, R.color.redCategoria)
                "orange" -> ContextCompat.getColor(context, R.color.orangeCategoria)
                "yellow" -> ContextCompat.getColor(context, R.color.yellowCategoria)
                "green" -> ContextCompat.getColor(context, R.color.greenCategoria)
                "blue" -> ContextCompat.getColor(context, R.color.blueCategoria)
                "purple" -> ContextCompat.getColor(context, R.color.purpleCategoria)
                "pink" -> ContextCompat.getColor(context, R.color.pinkCategoria)
                "gray" -> ContextCompat.getColor(context, R.color.darkerGray)
                "default" -> ContextCompat.getColor(context, R.color.darkerGray)
                else ->  ContextCompat.getColor(context, R.color.white)
            }
        }

        fun getColorDark(context: Context, color: String?): Int {
            return when(color) {
                "red" -> ContextCompat.getColor(context, R.color.DarkredCategoria)
                "orange" -> ContextCompat.getColor(context, R.color.DarkorangeCategoria)
                "yellow" -> ContextCompat.getColor(context, R.color.DarkyellowCategoria)
                "green" -> ContextCompat.getColor(context, R.color.DarkgreenCategoria)
                "blue" -> ContextCompat.getColor(context, R.color.DarkblueCategoria)
                "purple" -> ContextCompat.getColor(context, R.color.DarkpurpleCategoria)
                "pink" -> ContextCompat.getColor(context, R.color.DarkpinkCategoria)
                "gray" -> ContextCompat.getColor(context, R.color.md_theme_dark_surfaceVariant4)
                else ->  ContextCompat.getColor(context, R.color.md_theme_dark_background2)
            }
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun dpToPx(dp: Int, resources: Resources): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics
            ).toInt()
        }

        fun isSw1000dp(activity: Activity): Boolean {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val widthInDp = displayMetrics.widthPixels / activity.resources.displayMetrics.density
            return widthInDp >= 1000
        }

    }
}