package com.sama.android.domain

import java.io.Serializable

data class Payment(
        val value: Float,
        val createdAt: String
) : Serializable