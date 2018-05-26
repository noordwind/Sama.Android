package com.sama.android.views

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.sama.android.NgosPreviewActivity
import com.sama.android.R
import com.sama.android.domain.Ngo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_ngo_header.view.*
import java.util.*

class NgoHeaderView(context: Context?, var ngo: Ngo, headerClickable: Boolean = true) : LinearLayout(context) {

    init {
        View.inflate(getContext(), R.layout.view_ngo_header, this)
        invalidateNgo(ngo)

        name.text = ngo.name

        if (headerClickable) {
            setOnClickListener(OnClickListener { NgosPreviewActivity.show(getContext() as AppCompatActivity, ngo)  })
        }

        if (!headerClickable) {
            name.visibility = View.GONE
        }

        var resources = arrayOf(R.drawable.ngo2, R.drawable.ngo3, R.drawable.ngo4)
        Picasso.get().load(resources[Random().nextInt(3)]).into(ngoImage)
    }

    fun invalidateNgo(newNgo: Ngo) {
        ngo = newNgo
        name.text = ngo.name
        funds.setValue(newNgo.donatedFunds)
        children.text = "₹ raised for " + newNgo.children.size + " children"

        var fullyDonated = true

        for (child in newNgo.children)  {
            if (child.gatheredFunds < child.neededFunds) {
                fullyDonated = false
                break
            }
        }

        if (fullyDonated) {
            funds_active.visibility = View.VISIBLE
            funds_notactive.visibility = View.GONE
        } else {
            funds_active.visibility = View.GONE
            funds_notactive.visibility = View.VISIBLE
        }
    }
}