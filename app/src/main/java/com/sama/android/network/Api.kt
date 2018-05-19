package com.sama.android.network

import com.sama.android.domain.Ngo
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*


interface ApiHeader {
    companion object {
        const val ACCEPT_HEADER = "Accept: application/json"
        const val CONTENT_TYPE_HEADER = "Content-type: application/json"
    }
}

interface Api {

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("sign-in")
    fun login(@Body authRequest: AuthRequest): Observable<AuthResponse>

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("sign-up")
    fun signUp(@Body signUpRequest: SignUpRequest): Observable<ResponseBody>

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @GET("ngos")
    fun ngos(): Observable<List<Ngo>>

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @GET("ngos/{id}")
    fun ngo(@Path("id") id: String): Observable<Ngo>

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("ngos/{ngoId}/donate")
    fun donate(@Path("ngoId") ngoId: String, @Body donateRequest: DonateRequest): Observable<ResponseBody>
}

class DonateRequest(val funds: Int)

class AuthRequest(val email: String, val password: String)

class AuthResponse(val accessToken: String)

class SignUpRequest(val email: String, val password: String) {
    val role = "user"
}
