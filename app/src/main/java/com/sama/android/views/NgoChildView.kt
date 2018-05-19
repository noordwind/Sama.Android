package com.sama.android.views

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.sama.android.R
import com.sama.android.domain.NgoChild
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_ngo_child.view.*

class NgoChildView(context: Context?, val child: NgoChild) : LinearLayout(context) {

    init {
        View.inflate(getContext(), R.layout.view_ngo_child, this)
        fullName.text = child.fullName
        funds.setValue(child.funds)
        neededFunds.text = "/" + String.format("%.0f", child.neededFunds)+ "$"

        var url = "https://scontent-frx5-1.xx.fbcdn.net/v/t1.0-9/13707543_1113779778680046_7388140466043605584_n.jpg?_nc_cat=0&oh=e040642b3449396bfa4f92464bd7f0d0&oe=5B88FBE7"
        Picasso.get().load(url).into(profileImage)

    }

    fun setFunds(newFunds: Float) {
        funds.setValue(newFunds)
    }

    fun setNeededFunds(newNeededFunds: Float) {
        neededFunds.text = "/" + String.format("%.0f", newNeededFunds)+ "$"
    }
}