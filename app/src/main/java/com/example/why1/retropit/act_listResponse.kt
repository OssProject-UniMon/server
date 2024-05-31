package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class act_listitem(
    @SerializedName("deposit") val deposit: String, //입금
    @SerializedName("withdraw") val withdraw: String, //출금, 즉 가격
    @SerializedName("balance") val balance: String, //결제 후 잔고
    @SerializedName("date") val date: String, //결제일
    @SerializedName("useStoreName") val useStoreName: String, //카테고리, 즉 분류된 결과
    @SerializedName("category") val category: String //결제기관
)

data class act_listResponse(
        @SerializedName("logList") val logList: List<act_listitem>
        )
