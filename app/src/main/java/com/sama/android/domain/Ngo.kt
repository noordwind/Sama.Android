package com.sama.android.domain

import java.io.Serializable

data class Ngo (
        val id: String,
        val ownerId: String,
        val name: String,
        val description: String,
        val latitude: Double,
        val longitude: Double,
        val funds: Float,
        val donatedFunds: Float,
        val approved: Boolean,
        val children: List<NgoChild>
) : Serializable
