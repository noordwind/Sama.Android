package com.sama.android.views

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.sama.android.R
import com.sama.android.domain.Ngo
import kotlinx.android.synthetic.main.view_ngo_preview.view.*

class NgoPreviewView(context: Context?, ngo: Ngo, headerClickable: Boolean = true) : LinearLayout(context) {

    var headerView : NgoHeaderView

    init {
        View.inflate(getContext(), R.layout.view_ngo_preview, this)

        headerView = NgoHeaderView(context, ngo, headerClickable)
        root.addView(headerView, 0)

        var animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_falldown_animation);
        childrenSection.setLayoutAnimation(animation);

        var number = 1
        for (child in ngo.children) {
            var childView = NgoChildView(context, child)
            childView.visibility = View.GONE
            childrenSection.addView(childView, childrenSection.childCount)

            Handler().postDelayed(Runnable {
                childView.visibility = View.VISIBLE
            }, (number++ * 50).toLong())
        }
    }

    fun setNgo(it: Ngo) {
        for (i in 0..childrenSection.childCount) {
            var view = childrenSection.getChildAt(i)

            if (view is NgoChildView) {
                var childView = view as NgoChildView

                for (child in it.children) {
                    if (childView.child.id.equals(child.id))     {
                        childView.setFunds(child.funds)
                        childView.setNeededFunds(child.neededFunds)
                    }
                }
            }
        }

        headerView.invalidateNgo(it)
    }
}