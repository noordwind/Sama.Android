package com.sama.android.views

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import com.sama.android.R
import com.sama.android.domain.NgoDonation
import kotlinx.android.synthetic.main.view_ngo_donation.view.*

class NgoDonationView(context: Context?, donation: NgoDonation, profileDonation : Boolean = false) : LinearLayout(context) {

    init {
        View.inflate(getContext(), R.layout.view_ngo_donation, this)
        donationAmount.text = "+${donation.value.toInt()}₹"
        if (profileDonation) {
            donationLabel.text = Html.fromHtml("donated for <b>${donation.ngoName}</b>")
        } else {
            donationLabel.text = Html.fromHtml("donated by <b>${donation.username}</b>")
        }
    }

}