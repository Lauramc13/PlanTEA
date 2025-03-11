package com.example.plantea.persistencia

import com.example.plantea.dominio.objetos.JsonPictogramaItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/** Interfaz que define los métodos que se pueden llamar a través de Retrofit */
interface ApiInterface {

   /**
    * Devuelve una búsqueda más óptima de pictogramas en función de la consulta y el idioma.
    *
    * @param query El nombre del pictograma que se está buscando.
    * @param language El idioma en el que se está buscando (Ejemplo: "es" para español, "en" para inglés).
    * @return La lista de pictogramas [JsonPictogramaItem] que coinciden con la consulta.
    */

   @GET("pictograms/{language}/bestsearch/{query}")
   fun getData(@Path("query") query: String, @Path("language") language: String): Call<List<JsonPictogramaItem>>

   /**
    * Devuelve todos los pictogramas que coinciden con la consulta y el idioma.
    *
    * @param query El nombre del pictograma que se está buscando.
    * @param language El idioma en el que se está buscando (Ejemplo: "es" para español, "en" para inglés).
    * @return La lista de pictogramas [JsonPictogramaItem] que coinciden con la consulta.
    */

   @GET("pictograms/{language}/search/{query}")
   fun getAllData(@Path("query") query: String, @Path("language") language: String): Call<List<JsonPictogramaItem>>

   /**
    * Devuelve las imagenes de los pictogramas que coinciden con la consulta.
    *
    * @param id El identificador del pictograma.
    * @return La imagen del pictograma.
    */

   @GET("{id}/{id}_300.png")
   fun getImage(@Path("id") id: String): Call<ResponseBody>
}