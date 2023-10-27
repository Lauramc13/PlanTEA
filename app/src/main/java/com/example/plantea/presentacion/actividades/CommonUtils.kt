package com.example.plantea.presentacion.actividades

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.ApiInterface
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
        private lateinit var textToSpeech: TextToSpeech
        val handler = Handler()
        val delayedSpeechRunnables = mutableListOf<Runnable>()
        var listener: TextToSpeechListener? = null

        val textToSpeechOnInitListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        //Not implemented
                    }

                    override fun onDone(utteranceId: String) {
                        listener?.onSpeechDone()
                    }

                    override fun onError(utteranceId: String) {
                        //Not implemented
                    }
                })
            } else {
                // Initialization failed. Handle the error.
            }
        }

        fun getRetrofitBuilder(): ApiInterface {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.arasaac.org/api/")
                .build()
                .create(ApiInterface::class.java)
        }

        fun getRetrofitBuilderImg(): ApiInterface {
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
                val retrofitData = retrofitBuilder.getData(query)
                val response = retrofitData.execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        for (jsonData in responseBody) {
                            val id = jsonData._id
                            listaIds.add(id)
                            val keyword = jsonData.keywords[0].keyword
                            listaTitulos.add(keyword)
                        }
                    }
                    Log.d("TAG", listaTitulos.toString())
                }else{
                    Log.d("ERROR", "NO SE HA ENCONTRADO NADA")
                    // busquedaNula.visibility = View.VISIBLE
                }
            } catch (e: IOException) {
                // Handle exception
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
            dict[image] = Pair(query, 0)
            return dict

        }

        private fun textAsBitmap(text: String?): Bitmap {
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
            textToSpeech = TextToSpeech(context, textToSpeechOnInitListener)
            textToSpeech.language = Locale("es", "ES")
        }

        fun textToSpeechOn(listaPictogramas: ArrayList<Pictograma>, delay: Int){
           // textToSpeech = TextToSpeech(context, textToSpeechOnInitListener)
           // textToSpeech.language = Locale("es", "ES")

            listaPictogramas.forEachIndexed { index, pictograma ->
                val runnable = Runnable {
                    if (index == listaPictogramas.lastIndex) {
                        textToSpeech.speak(pictograma.titulo, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
                    }else{
                        textToSpeech.speak(pictograma.titulo, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }

                delayedSpeechRunnables.add(runnable)
                handler.postDelayed(runnable, index * delay.toLong())
        }

    }

        fun textToSpeechOff(){
            handler.removeCallbacksAndMessages(null)
            delayedSpeechRunnables.clear()
        }

        fun hideKeyboard(context: Context, view: View){
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun textToSpeechWord(word: String?){
           textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
        }

        fun crearRuta(context: Context, imagen: ImageView?, nombreImagen: String): String {
            val image = (imagen!!.drawable as BitmapDrawable).bitmap

            //Escalar imagen
            val proporcion = 500 / image.width.toFloat()
            val imagenFinal = Bitmap.createScaledBitmap(image, 500, (image.height * proporcion).toInt(), false)

            //Guardar imagen
            return guardarImagen(context, nombreImagen, imagenFinal)
        }

        private fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
            val cw = ContextWrapper(context)
            val dirImages = cw.getDir("Imagenes", AppCompatActivity.MODE_PRIVATE)
            val myPath = File(dirImages, "$nombre.png")
            val fos: FileOutputStream?
            try {
                fos = FileOutputStream(myPath)
                imagen.compress(Bitmap.CompressFormat.PNG, 10, fos) // calidad a 0 imagen mas pequeña
                fos.flush()
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            return myPath.absolutePath
        }

         fun cambioOrientacion(context: Context): Int {
            val orientation = context.resources.configuration.orientation
            val gridValueManager: Int = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                3
            } else {
                4
            }
            return gridValueManager
        }

    }


}