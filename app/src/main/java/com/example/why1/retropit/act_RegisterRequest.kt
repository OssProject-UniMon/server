package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class act_RegisterRequest (
    @SerializedName("bank") val bank: String,
    @SerializedName("bankAccountType") val bankAccountType: String, //은행 종류
    @SerializedName("bankAccountNum") val bankAccountNum: String, //계좌 번호
    @SerializedName("bankAccountPwd") val bankAccountPwd: String, //계좌 비번
    @SerializedName("webId") val webId: String, //은행 빠른조회 아이디
    @SerializedName("webPwd") val webPwd: String, // 은행빠른조회 비번
    @SerializedName("identityNum") val identityNum: String //주민번호 앞자리
)