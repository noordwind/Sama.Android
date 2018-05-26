package com.sama.android.domain

import java.io.Serializable

data class NgoChild(
        val id: String? = null,
        val birthDate: String,
        val gender: String,
        val fullName: String,
        val gatheredFunds: Float,
        val neededFunds: Float
) : Serializable