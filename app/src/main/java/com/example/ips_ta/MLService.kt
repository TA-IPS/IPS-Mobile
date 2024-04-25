package com.example.ips_ta

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
interface MLService {
    @POST("rf")
    suspend fun predict(@Body accessPoint: AccessPoint): Prediction
}