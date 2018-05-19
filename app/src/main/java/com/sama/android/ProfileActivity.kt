package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.sama.android.network.NetworkModule
import com.sama.android.views.NgoDonationView
import com.sama.android.views.PaymentView
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), NgoPreviewTabsSwitch.OnSwitched {

    companion object {
        fun show(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
            SamaAnimUtils.overrideEnterTransitionWithHorizontalSlide(context as Activity?)
        }
    }

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
        }

        var url = "https://scontent-frx5-1.xx.fbcdn.net/v/t1.0-9/13707543_1113779778680046_7388140466043605584_n.jpg?_nc_cat=0&oh=e040642b3449396bfa4f92464bd7f0d0&oe=5B88FBE7"
        Picasso.get().load(url).into(profileImage)


        var tabsSwitch = NgoPreviewTabsSwitch(this)
        tabsSwitch.listener = this
        tabsSwitch.setChildrenTabName("Payments")
        tabsSwitch.showChildren()
        tabsSwitch.invalidate()

        options.addView(tabsSwitch, 0)
    }

    override fun onStart() {
        super.onStart()
        disposable = NetworkModule().api(this)
                .profile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    donations.removeAllViews()

                    for (donation in it.donations.reversed()) {
                        donations.addView(NgoDonationView(baseContext, donation, true), donations.childCount)
                    }

                    donationsAmount.text = it.donations.size.toString()

                    payments.removeAllViews()

                    for (payment in it.payments.reversed()) {
                        payments.addView(PaymentView(baseContext, payment), payments.childCount)
                    }

                    paymentsAmount.text = it.payments.size.toString()
                }, Consumer {
                    Toast.makeText(baseContext, "Could not fetch Profile", Toast.LENGTH_SHORT).show()
                })
    }

    override fun donationsShown() {
        donations.visibility = View.VISIBLE
        payments.visibility = View.GONE
    }

    override fun childrenShown() {
        donations.visibility = View.GONE
        payments.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        SamaAnimUtils.overrideExitTransitionWithHorizontalSlide(this)
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        disposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }
}