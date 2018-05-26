package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.sama.android.domain.Ngo
import com.sama.android.network.Api
import com.sama.android.network.DonateRequest
import com.sama.android.network.MakePaymentRequest
import com.sama.android.network.NetworkModule
import com.sama.android.views.DonateDialog
import com.sama.android.views.NgoDonationView
import com.sama.android.views.PaymentView
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), NgoPreviewTabsSwitch.OnSwitched, DonateDialog.OnAccept {


    companion object {
        fun show(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
            SamaAnimUtils.overrideEnterTransitionWithHorizontalSlide(context as Activity?)
        }
    }

    private lateinit var api: Api
    var disposable: Disposable? = null
    var donateDisposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = NetworkModule().api(baseContext)
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

        makePaymentButton.setOnClickListener(View.OnClickListener {
            var donateDialog = DonateDialog()
            donateDialog.onAccept = ProfileActivity@ this
            donateDialog.dialogTitle = "Make payment"
            donateDialog.show(supportFragmentManager, "Donate")
        })
    }

    override fun onStart() {
        super.onStart()
        fetchProfile()
    }

    fun fetchProfile() {
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

                    payments.removeViews(0, payments.childCount - 1)

                    for (payment in it.payments.reversed()) {
                        payments.addView(PaymentView(baseContext, payment), payments.childCount - 1)
                    }

                    if (!it.payments.isEmpty()) {
                        var view = payments.getChildAt(payments.childCount - 2) as PaymentView
                        view.hideDelimiter()
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

        donateDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    override fun onAccept(donation: Int) {
        donateDisposable = api.makePaymet(MakePaymentRequest(donation))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action {
                    fetchProfile()
                }, Consumer {
                    it.printStackTrace()
                    Toast.makeText(baseContext, "Could not send a donation", Toast.LENGTH_SHORT).show()
                })
    }
}