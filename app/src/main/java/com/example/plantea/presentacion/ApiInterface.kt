package com.example.plantea.presentacion

import com.example.plantea.dominio.JsonPictogramaItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

   @GET("pictograms/{language}/bestsearch/{query}")
   fun getData(@Path("query") query: String, @Path("language") language: String): Call<List<JsonPictogramaItem>>

   @GET("pictograms/{language}/search/{query}")
   fun getAllData(@Path("query") query: String, @Path("language") language: String): Call<List<JsonPictogramaItem>>

   @GET("{query}/{query}_300.png")
   fun getImage(@Path("query") query: String): Call<ResponseBody>
}