package com.sama.android.views

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import com.sama.android.R
import com.sama.android.domain.NgoChild
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_ngo_child.view.*
import java.util.*

class NgoChildView(context: Context?, val child: NgoChild) : LinearLayout(context) {

    var childFunds: Float = 0f
    var newChildNeededFunds: Float = 0f

    init {
        View.inflate(getContext(), R.layout.view_ngo_child, this)
        fullName.text = child.fullName
        setFunds(child.funds)
        setNeededFunds(child.neededFunds)
        invalidateFunds()


        var urls = arrayOf(
                "https://cdn.pixabay.com/photo/2016/11/29/20/22/child-1871104_1280.jpg",
                "https://cdn.pixabay.com/photo/2016/10/05/17/26/indian-1717192_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/05/19/04/29/baby-3412739_1280.jpg",
                "https://cdn.pixabay.com/photo/2017/09/16/01/03/girl-2754233_1280.jpg",
                "https://cdn.pixabay.com/photo/2017/09/01/17/15/children-2704878_1280.jpg")

        Picasso.get().load(urls[Random().nextInt(5)]).into(profileImage)
    }

    fun setFunds(newFunds: Float) {
        childFunds = newFunds
        funds.setValue(newFunds)
    }

    fun setNeededFunds(newNeededFunds: Float) {
        newChildNeededFunds = newNeededFunds
        neededFunds.text = "/" + String.format("%.0f", newNeededFunds) + "$"
    }

    fun invalidateFunds() {
        if (childFunds >= newChildNeededFunds) {
            funds_active.visibility = View.VISIBLE
            funds_notactive.visibility = View.GONE
            funds.setTypeface(funds.typeface, Typeface.BOLD);
            funds.setTextColor(resources.getColor(R.color.vote_down_remark_color))
        } else {
            funds_active.visibility = View.GONE
            funds_notactive.visibility = View.VISIBLE
            funds.setTypeface(funds.typeface, Typeface.NORMAL);
            funds.setTextColor(resources.getColor(R.color.half_black))
        }
    }

}