package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class card_RegisterRequest (
    @SerializedName("cardCompany") val cardCompany: String, // 카드종류
    @SerializedName("cardType") val cardType: String, //고정값 p
    @SerializedName("cardNum") val cardNum: String, //카드번호
    @SerializedName("webId") val webId: String, // 카드사이트아이디
    @SerializedName("webPwd") val webPwd: String // 카드사이트 비번
)