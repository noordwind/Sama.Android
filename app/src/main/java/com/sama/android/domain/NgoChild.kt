package com.sama.android.domain

import java.io.Serializable

data class NgoChild(
        val id: String,
        val birthdate: String,
        val fullName: String,
        val funds: Float,
        val neededFunds: Float
) : Serializable