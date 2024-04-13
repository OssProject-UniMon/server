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

    @POST
    fun act_register(@Url url: String, @Body credential: act_RegisterRequest): Call<JoinResponse>

    @GET
    fun act_list(@Url url: String, @Query("start_date") start_date: String, @Query("end_date") end_date: String): Call<act_listResponse>
}