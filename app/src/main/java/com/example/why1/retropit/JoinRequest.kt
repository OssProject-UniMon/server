package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class JoinRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("major") val major: String,
    @SerializedName("grade") val grade: Double,
    @SerializedName("gender") val gender: String,
    @SerializedName("income_bracket") val income_bracket: String,
    @SerializedName("scholarship_status") val scholarship_status: Int,
    @SerializedName("district") val district: String // 인풋 9개
)
