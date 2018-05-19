package com.sama.android.views

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.sama.android.R
import com.sama.android.domain.Ngo
import kotlinx.android.synthetic.main.view_ngo_preview.view.*

class NgoPreviewView(context: Context?, ngo: Ngo) : LinearLayout(context) {

    init {
        View.inflate(getContext(), R.layout.view_ngo_preview, this)

        root.addView(NgoHeaderView(context, ngo), 0)

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
}