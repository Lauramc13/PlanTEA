package com.example.plantea.presentacion

import com.example.plantea.dominio.JsonPictogramaItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
    * Interfaz que define los métodos que se pueden llamar a través de Retrofit
    * @function getData: obtiene los pictogramas que mejor coinciden con la búsqueda
    * @function getAllData: obtiene todos los pictogramas que coinciden con la búsqueda, se llama si no se ha encontrado nada con getData
    * @function getImage: obtiene la imagen del pictograma
 */
interface ApiInterface {

   @GET("pictograms/{language}/bestsearch/{query}")
   fun getData(@Path("query") query: String, @Path("language") language: String): Call<List<JsonPictogramaItem>>

   @GET("pictograms/{language}/search/{query}")
   fun getAllData(@Path("query") query: String, @Path("language") language: String): Call<List<JsonPictogramaItem>>

   @GET("{query}/{query}_300.png")
   fun getImage(@Path("query") query: String): Call<ResponseBody>
}