package com.sama.android.domain

import java.io.Serializable

data class NgoDonation(
        val value: Float,
        val username: String
) : Serializable