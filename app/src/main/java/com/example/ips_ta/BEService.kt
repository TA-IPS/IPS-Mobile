package com.example.ips_ta

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BEService {
    @POST("fingerprint")
    suspend fun postFingerprint(@Body fingerprint: Fingerprint)

    @GET("fingerprint")
    suspend fun getFingerprint(): String
}