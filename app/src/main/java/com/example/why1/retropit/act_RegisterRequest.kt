package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class act_RegisterRequest (
    @SerializedName("bank") val bank: String,
    @SerializedName("bank_account_type") val bank_account_type: String, //은행 종류
    @SerializedName("bank_account_num") val bank_account_num: String, //계좌 번호
    @SerializedName("bank_account_pwd") val bank_account_pwd: String, //계좌 비번
    @SerializedName("web_id") val web_id: String, //은행 빠른조회 아이디
    @SerializedName("web_pwd") val web_pwd: String, // 은행빠른조회 비번
    @SerializedName("identity_num") val identity_num: String //주민번호 앞자리
)