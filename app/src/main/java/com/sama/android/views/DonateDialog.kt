package com.sama.android.views

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.ramotion.fluidslider.FluidSlider
import com.sama.android.R
import kotlinx.android.synthetic.main.framgment_donate_dialog.*

class DonateDialog : DialogFragment() {

    companion object {
        fun newInstance(): DonateDialog = DonateDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.framgment_donate_dialog, container, false)

        val max = 1000
        val min = 10
        val total = max - min

        var donationLabel = rootView.findViewById<EditText>(R.id.donation)
        var slider = rootView.findViewById<FluidSlider>(R.id.slider)
        slider.positionListener = { pos ->
            var donationAmount = min + (total * pos).toInt()
            slider.bubbleText = "${donationAmount}"
            donationLabel.setText("${donationAmount}", TextView.BufferType.NORMAL)
        }
        slider.position = 0.3f
        slider.startText ="$min"
        slider.endText = "$max"

        return rootView
    }

    override fun onStart() {
        super.onStart()
        val d = dialog
        dialog.setCanceledOnTouchOutside(true)
        if (d != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            d.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            d.window!!.setLayout(width, height)
        }
    }
}
