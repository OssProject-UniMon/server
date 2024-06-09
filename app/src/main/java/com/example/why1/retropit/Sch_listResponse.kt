package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class Sch_listitem(
    @SerializedName("title") val title: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("day") val day: String,
)

data class Sch_listResponse(
    @SerializedName("scheduleList") val scheduleList: List<Sch_listitem>
)