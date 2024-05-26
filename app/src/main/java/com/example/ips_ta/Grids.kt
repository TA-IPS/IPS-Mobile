package com.example.ips_ta

import com.google.gson.annotations.SerializedName

data class Grids(
    @SerializedName("0") val zero: List<Coordinate>,
    @SerializedName("1") val one: List<Coordinate>,
    @SerializedName("2") val two: List<Coordinate>,
    @SerializedName("3") val three: List<Coordinate>,
    @SerializedName("4") val four: List<Coordinate>?,
    @SerializedName("5") val five: List<Coordinate>?,
    @SerializedName("6") val six: List<Coordinate>?,
    @SerializedName("7") val seven: List<Coordinate>?,
)
