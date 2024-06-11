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
    fun act_list(@Url url: String): Call<act_listResponse>

    @POST
    fun card_register(@Url url: String, @Body credential: card_RegisterRequest): Call<JoinResponse>

    @POST
    fun sch_add(@Url url: String, @Body credential: Sch_Request): Call<JoinResponse>

    @GET
    fun sch_list(@Url url: String): Call<Sch_listResponse>

    @POST
    fun price_list(@Url url: String): Call<price_listResponse>

    @GET
    fun test(@Url url: String): Call<JoinResponse>
}