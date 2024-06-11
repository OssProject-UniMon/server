package com.example.why1.retropit

import com.google.gson.annotations.SerializedName

data class price_listitem(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("target") val target: String,
    @SerializedName("due") val due: String,
    @SerializedName("url") val url: String,
)

data class price_listResponse(
    @SerializedName("scholarshipList") val scholarshipList: List<price_listitem>
)
