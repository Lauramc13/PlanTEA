package com.example.plantea.presentacion.actividades.ninio

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.GestionNavegacion
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.ApiInterface
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.UUID


class TraductorActivity : AppCompatActivity(), AdaptadorPictogramasTraductor.OnItemSelectedListener{

    var listaPictogramas: ArrayList<Pictograma> = ArrayList()
    private var listaTraducir : ArrayList<String> = ArrayList()
    private var navigationHandler = GestionNavegacion()
    private lateinit var textoATraducir : TextInputLayout
    private lateinit var adaptador: AdaptadorPictogramasTraductor
    private lateinit var recyclerView: RecyclerView
    private var listaPictoBuscador: MutableList<MutableMap<Bitmap, String>> = mutableListOf()

    override fun onResume() {
        super.onResume()
        navigationHandler.configurarDatos(this, R.id.actividades)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationHandler.destroyPopup()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traductor)

        navigationHandler.inicializarVariables(this, R.id.traductor, TraductorActivity::class.java)

        val traducirButton: Button = findViewById(R.id.traducirButton)
        val backButton: Button = findViewById(R.id.goBackButton)
        textoATraducir = findViewById(R.id.textoTraducir)
        recyclerView = findViewById(R.id.recycler_plan)
        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = layoutManagerLinear

        backButton.setOnClickListener {
            finish()
        }

        traducirButton.setOnClickListener {
                listaTraducir.clear()
                listaPictogramas.clear()
                val texto = textoATraducir.editText?.text?.trim()
                if (!texto.isNullOrBlank()) {
                    val words = texto.split("\\s+".toRegex())
                    listaTraducir.addAll(words)
                    getPictogramas()
                }
        }

        textoATraducir.editText?.setOnKeyListener{_, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Simulate button click when "Enter" is pressed
                traducirButton.performClick()
                return@setOnKeyListener true
            }
            false
        }
    }

        private fun getPictogramas() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.arasaac.org/api/")
            .build()
            .create(ApiInterface::class.java)

        val retrofitBuilderImg = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://static.arasaac.org/pictograms/")
            .build()
            .create(ApiInterface::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            for (word in listaTraducir) {
                //var wordFound = true
                val dict = mutableMapOf<Bitmap,String>()
                val listaIds = mutableListOf<Int>()
                val listaTitulos = mutableListOf<String>()
                try {
                    val retrofitData = retrofitBuilder.getData(word)
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
                    }else{
                        Log.d("pruebas", "no se ha encontrado imagen")
                        //wordFound = false
                    }
                } catch (e: IOException) {
                    // Handle exception
                }
                //if(wordFound){
                    for (id in listaIds) {
                        val keyword = listaTitulos[listaIds.indexOf(id)]
                        try {
                            val retrofitImage = retrofitBuilderImg.getImage(id.toString())
                            val response = retrofitImage.execute()

                            if (response.isSuccessful) {
                                val bitmap = BitmapFactory.decodeStream(response.body()!!.byteStream())
                                dict[bitmap] = keyword
                            } else {
                                Log.d("pruebas", "Error de la llamada a las imagenes")
                            }
                        } catch (e: IOException) {
                            // Handle exception
                        }
                    }

                    val image = textAsBitmap(word)
                    dict[image] = word

                    Log.d("pruebas", dict.values.toString())
                    withContext(Dispatchers.Main) {
                        dict.keys.first().let{ key ->
                            dict[key]?.let { value ->
                                crearPictoBusqueda(key, value, null)
                                mostrarBusqueda()
                            }
                        }
                    }
                /*}else{
                    val image = textAsBitmap(word)
                    dict[image] = word
                    crearPictoBusqueda(image, word, null)
                    mostrarBusqueda()
                }*/

                listaPictoBuscador.add(dict)
            }
        }
    }

    private fun mostrarBusqueda() {
        Handler(Looper.getMainLooper()).post {
            adaptador = AdaptadorPictogramasTraductor(listaPictogramas, this)
            recyclerView.adapter = adaptador

        }
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

   private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, posicion: Int?) {
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
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            outputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val tituloMayus = titulo?.uppercase()
        val archivo = getFileStreamPath(filename).absolutePath
        val id = generateBitmapId(bitmap)


        if(posicion != null){
            listaPictogramas[posicion] = Pictograma(id, tituloMayus, archivo, 0, 0, false)
        }else {
            listaPictogramas.add(Pictograma(id, tituloMayus, archivo, 0, 0, false))
        }
    }

    //Genero un id unico para cada bitmap
    private fun generateBitmapId(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(byteArray)

        return digest.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    override fun onItemSeleccionado(posicion: Int) {
        try {
            val entryList =  listaPictoBuscador[posicion].entries.toList()
            val desiredId = listaPictogramas[posicion].id
            var position = 0

            for ((index, entry) in entryList.withIndex()) {
                val bitmapId = generateBitmapId(entry.key)
                if (bitmapId == desiredId) {
                    position = index
                    break
                }
            }

            if (position == entryList.lastIndex) {
                position = 0
            } else {
                position++
            }

            val desiredEntry = entryList[position]

            crearPictoBusqueda(desiredEntry.key, desiredEntry.value, posicion)

            adaptador.notifyItemChanged(posicion)
        }catch (e: Exception){
            Log.d("pruebas", e.toString())
        }


    }

}