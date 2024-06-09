package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class Sch_Request(
    @SerializedName("title") val title: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("day") val day: String
)
