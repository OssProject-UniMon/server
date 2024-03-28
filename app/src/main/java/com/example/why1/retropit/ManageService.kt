package com.example.why1.retropit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ManageService {
    @POST("/user/login")
    fun login(@Body credential: LoginRequest) : Call<LoginResponse>
}