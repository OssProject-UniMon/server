package com.example.why1.retropit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ManageService {
    @POST("/user/login")
    fun login(@Body credential: LoginRequest) : Call<LoginResponse>

    @GET
    fun emailresponse(@Url url: String, @Query("email") email: String): Call<EmailResponse>
}