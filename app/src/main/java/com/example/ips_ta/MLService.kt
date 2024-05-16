package com.example.ips_ta

import retrofit2.http.Body
import retrofit2.http.POST

interface MLService {
    @POST("rf")
    suspend fun predictRf(@Body accessPoint: AccessPoint): Prediction

    @POST("gnb")
    suspend fun predictGnb(@Body accessPoint: AccessPoint): Prediction

    @POST("svm")
    suspend fun predictSvm(@Body accessPoint: AccessPoint): Prediction

    @POST("knn")
    suspend fun predictKnn(@Body accessPoint: AccessPoint): Prediction

    @POST("cnn")
    suspend fun predictCnn(@Body accessPoint: AccessPoint): Prediction

    @POST("rfo")
    suspend fun predictRfV2(@Body accessPoint: AccessPoint): PredictionList


}