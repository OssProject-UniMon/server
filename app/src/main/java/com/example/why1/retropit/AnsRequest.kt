package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class AnsRequest(
    @SerializedName("requestMessage") val requestMessage: String
)
