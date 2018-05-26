package com.sama.android.domain

import java.io.Serializable

data class Ngo (
        val id: String,
        val ownerId: String,
        val name: String,
        val description: String,
        val location: Location,
        val state: String,
        val funds: Float,
        val donatedFunds: Float,
        val approved: Boolean,
        val children: List<NgoChild>,
        val donations: List<NgoDonation>
) : Serializable


data class Location(val latitude: Double, val longitude: Double) : Serializable