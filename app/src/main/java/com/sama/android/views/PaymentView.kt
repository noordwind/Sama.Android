package com.sama.android.views

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import com.sama.android.R
import com.sama.android.domain.NgoDonation
import com.sama.android.domain.Payment
import kotlinx.android.synthetic.main.view_ngo_donation.view.*

class PaymentView(context: Context?, payment: Payment) : LinearLayout(context) {

    init {
        View.inflate(getContext(), R.layout.view_ngo_donation, this)
        donationAmount.text = "+${payment.value.toInt()}$"
        donationLabel.text = Html.fromHtml(" <b>${payment.createdAt.split("T")[0]}</b>")
    }

    fun hideDelimiter() {
        delimiter.visibility = View.GONE
    }
}