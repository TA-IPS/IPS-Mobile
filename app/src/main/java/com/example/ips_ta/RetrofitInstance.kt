package com.example.ips_ta

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BE_BASE_URL = "https://ips-be-oc7uijn2ra-uc.a.run.app/"

    private const val ML_BASE_URL = "https://ips-ml-oc7uijn2ra-uc.a.run.app/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val BEApiService: BEService by lazy {
        retrofit.create(BEService::class.java)
    }

    private val retrofit2: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ML_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val mlApiService: MLService by lazy {
        retrofit2.create(MLService::class.java)
    }
}