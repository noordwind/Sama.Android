package com.sama.android.domain

/**
 * Created by adriankremski on 20/05/2018.
 */

data class Profile(
        val role: String,
        val payments: List<Payment>,
        val donations: List<NgoDonation>
)
