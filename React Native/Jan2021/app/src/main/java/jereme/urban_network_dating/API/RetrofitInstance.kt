package com.example.messengerapp

import jereme.urban_network_dating.API.ApiService
import jereme.urban_network_dating.LoginActivity.base_url
import jereme.urban_network_dating.LoginActivity.nodebase_url
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class RetrofitInstance {

  companion object {
    val retrofit: ApiService by lazy {
      val httpClient = OkHttpClient.Builder()
      val builder = Retrofit.Builder()
          //  .baseUrl("http://192.168.0.105:3000")
          .baseUrl(nodebase_url)
          .addConverterFactory(ScalarsConverterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())

      val retrofit = builder
          .client(httpClient.build())
          .build()
      retrofit.create(ApiService::class.java)
    }

    val retrofituser: ApiService by lazy {
      val httpClient = OkHttpClient.Builder()
      val builder = Retrofit.Builder()
              .baseUrl(base_url)
              .addConverterFactory(ScalarsConverterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())

      val retrofit = builder
              .client(httpClient.build())
              .build()
      retrofit.create(ApiService::class.java)
    }
  }

}