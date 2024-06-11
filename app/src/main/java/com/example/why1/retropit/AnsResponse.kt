package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class AnsResponse(
    @SerializedName("responseMessage") val responseMessage: String
)
