package com.example.ips_ta.stepdetector

interface StepDetector {

    fun registerListener(stepListener: StepListener): Boolean

    fun unregisterListener()
}