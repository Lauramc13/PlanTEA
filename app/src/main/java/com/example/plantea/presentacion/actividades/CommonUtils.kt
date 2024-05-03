package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.JsonPictogramaItem
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.ApiInterface
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment
import com.example.plantea.presentacion.viewModels.CrearPlanViewModel
import com.example.plantea.presentacion.viewModels.CuadernoViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.intellij.lang.annotations.Language
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID

class CommonUtils{

    interface TextToSpeechListener {
        fun onSpeechDone()
    }

    companion object {
        lateinit var textToSpeech: TextToSpeech
        val handler = Handler()
        var listener: TextToSpeechListener? = null
        lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
        private lateinit var imgPicto: ImageView

        // to call the listener when the speech is done
        private val textToSpeechOnInitListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        Log.d("prueba", "onStart: $utteranceId")
                    }

                    override fun onDone(utteranceId: String) {
                        Log.d("prueba", "onDone: $utteranceId")
                        listener?.onSpeechDone()
                    }

                    override fun onError(utteranceId: String) {
                        Log.e("prueba", "onError: $utteranceId")
                    }
                })
            } else {
                Log.e("prueba", "TextToSpeech initialization failed")

            }
        }

        // check if the device is a mobile or a tablet
        fun isMobile(context: Context): Boolean {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            val density = displayMetrics.density
            val widthPixels = displayMetrics.widthPixels
            val heightPixels = displayMetrics.heightPixels
            val shortestDimensionInPixels = (Math.min(widthPixels, heightPixels) /density).toInt()

            return shortestDimensionInPixels < 700
        }


        fun getGridValueCuaderno(vista: View, context: Context?, recyclerView: RecyclerView, constraintLayout: ConstraintLayout, widthItem: Int, widthItemTablet: Int){
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

        private fun getRetrofitBuilder(): ApiInterface {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.arasaac.org/api/")
                .build()
                .create(ApiInterface::class.java)
        }

        private fun getRetrofitBuilderImg(): ApiInterface {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://static.arasaac.org/pictograms/")
                .build()
                .create(ApiInterface::class.java)
        }

        fun getDataApi(query: String): MutableMap<Bitmap, Pair<String, Int>> {
            val retrofitBuilder = getRetrofitBuilder()
            val retrofitBuilderImg = getRetrofitBuilderImg()

            val dict = mutableMapOf<Bitmap, Pair<String, Int>>()
            val listaIds = mutableListOf<Int>()
            val listaTitulos = mutableListOf<String>()

            try {
                val language = Locale.getDefault().language
                var retrofitData: retrofit2.Call<List<JsonPictogramaItem>>
                retrofitData = retrofitBuilder.getData(query, language)
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
            val idImageCreated = wordToId(query)
            dict[image] = Pair(query, idImageCreated)
            return dict

        }

        private fun wordToId(word: String): Int {
            var id = 0
            for (char in word) {
                id = id * 31 + char.code
            }
            return id
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

        private fun textAsBitmapOLD(text: String?): Bitmap {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = 100f
            paint.color = Color.BLACK
            paint.textAlign = Paint.Align.LEFT
            val baseline = -paint.ascent() // ascent() is negative
            val width = (paint.measureText(text) + 0.5f).toInt() // round
            val height = (baseline + paint.descent() + 0.5f).toInt()
            val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            canvas.drawText(text!!, 0f, baseline, paint)
            return image
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
                val textSize = 300f / width * 100
                paint.textSize = textSize
                width = 300
            }


            val image = Bitmap.createBitmap(width, 300, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            canvas.drawText(text, 0f, 150f - textBounds.exactCenterY(), paint)

            return image
        }

        fun crearImagen(bitmap: Bitmap, titulo: String?, context: Context): String {
            val width = bitmap.width
            val height = bitmap.height
            val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            val numero = UUID.randomUUID()

            val filename = "$titulo$numero.jpg"
            val outputStream: FileOutputStream
            try {
                outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
                outputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return context.getFileStreamPath(filename).absolutePath
        }

        fun initializeTextToSpeech(context: Context) {
            val language = Locale.getDefault().language
            textToSpeech = TextToSpeech(context, textToSpeechOnInitListener)
            textToSpeech.language = Locale(language)
        }

        fun textToSpeechOn(listaPictogramas: ArrayList<Pictograma>){

            listaPictogramas.forEachIndexed { index, pictograma ->
                    if (index == listaPictogramas.lastIndex) {
                        textToSpeech.speak(pictograma.titulo, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
                    }else{
                        textToSpeech.speak(pictograma.titulo, TextToSpeech.QUEUE_ADD, null, null)
                    }
            }
        }

        fun hideKeyboard(context: Context, view: View){
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun textToSpeechWord(word: String?){
           textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
        }

        fun crearRuta(context: Context, image: Bitmap, nombreImagen: String): String{
            //Escalar imagen
            val proporcion = 500 / image.width.toFloat()
            val imagenFinal = Bitmap.createScaledBitmap(image, 500, (image.height * proporcion).toInt(), false)

            //Guardar imagen
            return guardarImagen(context, nombreImagen, imagenFinal)
        }

        fun crearRuta(context: Context, imagen: ImageView?, nombreImagen: String): String {
            val image = (imagen!!.drawable as BitmapDrawable).bitmap

            //Escalar imagen
            val proporcion = 500 / image.width.toFloat()
            val imagenFinal = Bitmap.createScaledBitmap(image, 500, (image.height * proporcion).toInt(), false)

            //Guardar imagen
            return guardarImagen(context, nombreImagen, imagenFinal)
        }

        fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
            val cw = ContextWrapper(context)
            val dirImages = cw.getDir("Imagenes", AppCompatActivity.MODE_PRIVATE)
            val myPath = File(dirImages, "$nombre.png")
            val fos: FileOutputStream?
            try {
                val resizedBitmap = Bitmap.createScaledBitmap(imagen, 300, 300, false)
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

     /*   private fun Snackbar.setIcon(drawable: Drawable, @ColorInt colorTint: Int): Snackbar {
            return this.apply {
                setAction(" ") {dismiss()}
                val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                textView.text = ""

                drawable.setTint(colorTint)
                drawable.setTintMode(PorterDuff.Mode.SRC_ATOP)
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        }*/

       /* fun showSnackbar(view: View, context: Context, message: String){
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            var color = ContextCompat.getColor(context, R.color.md_theme_light_primary)

            if(Configuration.UI_MODE_NIGHT_YES == context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                color = ContextCompat.getColor(context, R.color.md_theme_dark_onTertiary)
            }

            val drawable = ContextCompat.getDrawable(context, R.drawable.svg_close)
            if (drawable != null) {
                snackbar.setIcon(drawable, color)
            }
            snackbar.show()
        }*/

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

    }
}