package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class EmailResponse(
    @SerializedName("serverCode") val serverCode: Int
)
