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

class CreateNgoDialog : DialogFragment() {

    companion object {
        fun newInstance(): CreateNgoDialog = CreateNgoDialog()
    }

    interface OnAccept {
        fun onAccept(name: String, description: String, fundsPerChild: Int)
    }

    var onAccept: OnAccept? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.framgment_ngo_dialog, container, false)

        val max = 3000
        val min = 0
        val total = max - min

        var fundsLabel = rootView.findViewById<EditText>(R.id.funds)
        var slider = rootView.findViewById<FluidSlider>(R.id.slider)

        slider.positionListener = { pos ->
            var donationAmount = min + (total * pos).toInt()
            slider.bubbleText = "${donationAmount}"
            fundsLabel.setText("${donationAmount}", TextView.BufferType.NORMAL)
        }
        slider.startText = "$min"
        slider.endText = "$max"

        rootView.findViewById<View>(R.id.accept).setOnClickListener({
            onAccept?.let {
                var fullName = rootView.findViewById<TextView>(R.id.fullName).text.toString()
                var funds = rootView.findViewById<TextView>(R.id.funds).text.toString().toInt()
                var description = rootView.findViewById<TextView>(R.id.descriptionLabel).text.toString()

                onAccept?.let {
                    it.onAccept(name = fullName, description = description, fundsPerChild = funds)
                }
            }
            dismiss()
        })

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