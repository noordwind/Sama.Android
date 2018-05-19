package com.sama.android.views

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.sama.android.R
import com.sama.android.domain.NgoDonation
import kotlinx.android.synthetic.main.view_ngo_donation.view.*

class NgoDonationView(context: Context?, donation: NgoDonation) : LinearLayout(context) {

    init {
        View.inflate(getContext(), R.layout.view_ngo_donation, this)
        donationLabel.text = "${donation.value.toInt()}$ donation by ${donation.username}"
    }

}