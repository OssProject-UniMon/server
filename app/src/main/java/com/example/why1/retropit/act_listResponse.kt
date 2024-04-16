package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class act_listitem(
    @SerializedName("deposit") val deposit: String, //입금
    @SerializedName("withdraw") val withdraw: String, //출금, 즉 가격
    @SerializedName("balance") val balance: String, //결제 후 잔고
    @SerializedName("trans_dt") val trans_dt: String, //결제일
    @SerializedName("trans_type") val trans_type: String, //결제방식
    @SerializedName("trans_remark") val trans_remark: String //결제기관
)

data class act_listResponse(
        @SerializedName("log_list") val log_list: List<act_listitem>
        )
