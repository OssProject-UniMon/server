package com.example.why1.retropit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface JoinService {
    @POST("/user/signup")
    fun Register(@Body credential: JoinRequest) : Call<JoinResponse>
}