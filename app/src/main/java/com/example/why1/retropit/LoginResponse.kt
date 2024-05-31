package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("serverCode") val serverCode: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("nickname") val nickname: String
)
