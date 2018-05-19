package com.sama.android.views

import android.content.Context
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
        name.text = ngo.name
        funds.setValue(ngo.donatedFunds)
        children.text = "$ raised for " + ngo.children.size + " children"

        if (headerClickable) {
            setOnClickListener(OnClickListener { NgosPreviewActivity.show(getContext(), ngo)  })
        }

        var resources = arrayOf(R.drawable.ngo2, R.drawable.ngo3, R.drawable.ngo4)
        Picasso.get().load(resources[Random().nextInt(3)]).into(ngoImage)
    }

    fun invalidateNgo(newNgo: Ngo) {
        ngo = newNgo
        funds.setValue(newNgo.donatedFunds)
        children.text = "$ raised for " + newNgo.children.size + " children"
    }
}