package com.sama.android.network

import com.sama.android.domain.Ngo
import com.sama.android.domain.NgoChild
import com.sama.android.domain.Profile
import io.reactivex.Completable
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
    fun signUp(@Body signUpRequest: SignUpRequest): Completable

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @GET("ngos")
    fun ngos(): Observable<List<Ngo>>

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @GET("ngos/{id}")
    fun ngo(@Path("id") id: String): Observable<Ngo>

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @GET("me")
    fun profile(): Observable<Profile>

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("me/funds")
    fun makePaymet(@Body makePaymentRequest: MakePaymentRequest): Completable

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("ngos/{ngoId}/donate")
    fun donate(@Path("ngoId") ngoId: String, @Body donateRequest: DonateRequest): Completable

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("ngos/{ngoId}/children/{childId}/donate")
    fun donateChild(@Path("ngoId") ngoId: String, @Path("childId") childId: String,
                    @Body donateRequest: DonateRequest): Completable

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("ngos/{ngoId}/children")
    fun createChild(@Path("ngoId") ngoId: String, @Body createChildRequest: CreateChildRequest): Completable

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("ngos")
    fun createNgo( @Body createNgoRequest: CreateNgoRequest): Completable

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("ngos/{ngoId}/approve")
    fun approveNgo(@Path ("ngoId") ngoId: String, @Body body: ApproveRequest): Completable

    @Headers(ApiHeader.ACCEPT_HEADER, ApiHeader.CONTENT_TYPE_HEADER)
    @POST("ngos/{ngoId}/reject")
    fun rejectNgo(@Path ("ngoId") ngoId: String, @Body body: RejectRequest): Completable
}

class ApproveRequest(val notes: String)
class RejectRequest(val notes: String)

class Location(val latitude: Float, val longitude: Float)

class CreateNgoRequest(val name:String, val description: String, val fundsPerChild: Int, val location: Location)

class CreateChildRequest(val children: List<NgoChild>)

class MakePaymentRequest(val funds: Int)

class DonateRequest(val funds: Int)

class AuthRequest(val email: String, val password: String)

class AuthResponse(val accessToken: String)

class SignUpRequest(val email: String, val password: String, val username: String) {
    val role = "user"
}
