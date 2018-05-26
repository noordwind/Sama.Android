package com.sama.android.views

import android.content.Context
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.sama.android.R
import com.sama.android.domain.NgoChild
import com.sama.android.network.Api
import com.sama.android.network.DonateRequest
import com.sama.android.network.MakePaymentRequest
import com.sama.android.network.NetworkModule
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import it.sephiroth.android.library.tooltip.Tooltip
import kotlinx.android.synthetic.main.view_ngo_child.view.*
import java.util.*

class NgoChildView(context: Context?, val child: NgoChild, val ngoId: String) : LinearLayout(context), DonateDialog.OnAccept {
    var childFunds: Float = 0f
    var newChildNeededFunds: Float = 0f
    private lateinit var api: Api

    init {
        View.inflate(getContext(), R.layout.view_ngo_child, this)
        fullName.text = child.fullName
        api = NetworkModule().api(context!!)
        setFunds(child.gatheredFunds)
        setNeededFunds(child.neededFunds)
        invalidateFunds()

        var urls = arrayOf(
                "https://cdn.pixabay.com/photo/2016/11/29/20/22/child-1871104_1280.jpg",
                "https://cdn.pixabay.com/photo/2016/10/05/17/26/indian-1717192_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/05/19/04/29/baby-3412739_1280.jpg",
                "https://cdn.pixabay.com/photo/2017/09/16/01/03/girl-2754233_1280.jpg",
                "https://cdn.pixabay.com/photo/2017/09/01/17/15/children-2704878_1280.jpg")

        Picasso.get().load(urls[Random().nextInt(5)]).into(profileImage)

        funds_active.setOnClickListener(View.OnClickListener {
            var donateDialog = DonateDialog()
            donateDialog.onAccept = NgoChildView@this
            donateDialog.dialogTitle = "Donate for " + child.fullName
            donateDialog.show((context as AppCompatActivity).supportFragmentManager, "Donate")
        })

        funds_notactive.setOnClickListener(View.OnClickListener {
            var donateDialog = DonateDialog()
            donateDialog.onAccept = NgoChildView@this
            donateDialog.dialogTitle = "Donate for " + child.fullName
            donateDialog.show((context as AppCompatActivity).supportFragmentManager, "Donate")
        })
    }

    override fun onAccept(donation: Int) {
        api.donateChild(ngoId, child.id!!, DonateRequest(donation))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action {
                    setFunds(childFunds + donation)
                    invalidateFunds()
                }, Consumer {
                    Toast.makeText(context, "Could not send a donation", Toast.LENGTH_SHORT).show()
                })
    }

    fun showTooltip () {
        if (childFunds >= newChildNeededFunds) {
            Tooltip.make(getContext(),
                    Tooltip.Builder(101)
                            .anchor(funds_active, Tooltip.Gravity.TOP)
                            .closePolicy(Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 3000000)
                            .text("Click to donate")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .withStyleId(R.style.TooltipMainStyle)
                            .build()
            ).show()
        } else {
            Tooltip.make(getContext(),
                    Tooltip.Builder(101)
                            .anchor(funds_notactive, Tooltip.Gravity.TOP)
                            .closePolicy(Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 3000000)
                            .text("Click to donate")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .withStyleId(R.style.TooltipMainStyle)
                            .build()
            ).show()
        }
    }

    fun setFunds(newFunds: Float) {
        childFunds = newFunds
        funds.setValue(newFunds)
    }

    fun setNeededFunds(newNeededFunds: Float) {
        newChildNeededFunds = newNeededFunds
        neededFunds.text = "/" + String.format("%.0f", newNeededFunds) + "₹"
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

    fun hideDelimiter() {
        delimiter.visibility = View.GONE
    }

    fun showDelimiter() {
        delimiter.visibility = View.VISIBLE
    }

}