package com.sama.android.network

import com.sama.android.domain.Ngo
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


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
}

class AuthRequest(val email: String, val password: String)

class AuthResponse(val accessToken: String)

class SignUpRequest(val email: String, val password: String) {
    val role = "user"
}
