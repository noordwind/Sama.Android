package com.sama.android.views

import android.content.Context
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.sama.android.NgoPreviewTabsSwitch
import com.sama.android.NgosPreviewActivity
import com.sama.android.R
import com.sama.android.Session
import com.sama.android.domain.Ngo
import com.sama.android.domain.NgoChild
import com.sama.android.network.ApproveRequest
import com.sama.android.network.CreateChildRequest
import com.sama.android.network.NetworkModule
import com.sama.android.network.RejectRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_ngo_preview.view.*
import java.util.*

class NgoPreviewView(context: Context?, val ngo: Ngo, headerClickable: Boolean = true) : LinearLayout(context), NgoPreviewTabsSwitch.OnSwitched {
    override fun childrenShown() {
        childrenSectionWrapper.visibility = View.VISIBLE
        donationsSection.visibility = View.GONE
    }

    override fun donationsShown() {
        childrenSectionWrapper.visibility = View.GONE
        donationsSection.visibility = View.VISIBLE
    }

    var headerView: NgoHeaderView
    var tabsSwitch: NgoPreviewTabsSwitch

    init {
        View.inflate(getContext(), R.layout.view_ngo_preview, this)

        headerView = NgoHeaderView(context, ngo, headerClickable)
        root.addView(headerView, 0)

        tabsSwitch = NgoPreviewTabsSwitch(context)
        root.addView(tabsSwitch, 2)
        tabsSwitch.listener = this
        tabsSwitch.showChildren()
        tabsSwitch.invalidate()

        var animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_falldown_animation);
        childrenSection.setLayoutAnimation(animation);

        toggleButtonsVisibility(ngo)

        var number = 1
        for (child in ngo.children) {
            var childView = NgoChildView(context, child, ngo.id)
            childView.visibility = View.GONE
            childrenSection.addView(childView, childrenSection.childCount)

            Handler().postDelayed(Runnable {
                childView.visibility = View.VISIBLE
            }, (number++ * 50).toLong())
        }

        if (childrenSection.childCount > 0) {
            ((childrenSection.getChildAt(childrenSection.childCount - 1) as NgoChildView)).hideDelimiter()
        }

        if (Session(context!!).isAdmin || Session(context!!).isNgo) {
            addChildButton.visibility = View.VISIBLE
            addChildButton.setOnClickListener(OnClickListener {

                var dialog = CreateChildDialog.newInstance()
                dialog.onAccept = object : CreateChildDialog.OnAccept {
                    override fun onAccept(fullName: String, neededFunds: Int) {
                        var newChild = NgoChild(birthDate = "2011-05-17", fullName = fullName,
                                gender = "male", neededFunds = neededFunds.toFloat(), gatheredFunds = 0F)

                        var children = LinkedList<NgoChild>()

                        children.add(newChild)

                        NetworkModule().api(context)
                                .createChild(ngo.id, CreateChildRequest(children))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(Action {
                                    NetworkModule().api(context).ngo(ngo.id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(Consumer {
                                                if (childrenSection.childCount > 0) {
                                                    ((childrenSection.getChildAt(childrenSection.childCount - 1) as NgoChildView)).hideDelimiter()
                                                }

                                                var newChild = it.children[it.children.size - 1]
                                                var childView = NgoChildView(context, newChild, ngo.id)
                                                childView.visibility = View.GONE
                                                childView.hideDelimiter()
                                                childrenSection.addView(childView, childrenSection.childCount)
                                                Handler().postDelayed(Runnable {
                                                    childView.visibility = View.VISIBLE
                                                }, (number++ * 50).toLong())
                                            }, Consumer {
                                                Toast.makeText(context, "Could not create new child", Toast.LENGTH_SHORT).show()
                                            })
                                }, Consumer {
                                    Toast.makeText(context, "Could not create new child", Toast.LENGTH_SHORT).show()
                                })
                    }

                }
                dialog.show((context as AppCompatActivity).supportFragmentManager, "Tag")

            })
        } else {
            addChildButton.visibility = View.GONE
        }

        setDonations(ngo)
    }

    fun toggleButtonsVisibility(ngo: Ngo) {
        var session = Session(context!!)

        if (session.isAdmin) {
            adminButtons.visibility = View.VISIBLE

            if (ngo.state.equals("new")) {
                approve.visibility = View.VISIBLE
                reject.visibility = View.VISIBLE
            } else if (ngo.state.equals("approved")) {
                approve.visibility = View.GONE
                reject.visibility = View.VISIBLE
            } else if (ngo.state.equals("rejected")) {
                approve.visibility = View.VISIBLE
                reject.visibility = View.GONE
            }

            approve.setOnClickListener {
                NetworkModule().api(context).approveNgo(ngo.id, ApproveRequest(""))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Action {
                            NetworkModule().api(context).ngo(ngo.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(Consumer {
                                        toggleButtonsVisibility(it)
                                        Toast.makeText(context, "NGO approved", Toast.LENGTH_SHORT).show()

                                        if (context is NgosPreviewActivity) {
                                            var activity = context as NgosPreviewActivity
                                            activity.refetchNgos()
                                        }
                                    }, Consumer {
                                        Toast.makeText(context, "Could not approve NGO", Toast.LENGTH_SHORT).show()
                                    })

                        }, Consumer {
                            Toast.makeText(context, "Could not approve NGO", Toast.LENGTH_SHORT).show()
                        })
            }

            reject.setOnClickListener {

                NetworkModule().api(context).rejectNgo(ngo.id, RejectRequest(""))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Action {
                            NetworkModule().api(context).ngo(ngo.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(Consumer {
                                        toggleButtonsVisibility(it)

                                        if (context is NgosPreviewActivity) {
                                            var activity = context as NgosPreviewActivity
                                            activity.refetchNgos()
                                        }

                                        Toast.makeText(context, "NGO rejected", Toast.LENGTH_SHORT).show()
                                    }, Consumer {
                                        Toast.makeText(context, "Could not reject NGO", Toast.LENGTH_SHORT).show()
                                    })
                        }, Consumer {
                            Toast.makeText(context, "Could not reject NGO", Toast.LENGTH_SHORT).show()
                        })
            }

        } else {
            adminButtons.visibility = View.GONE
        }
    }

    fun setNgo(it: Ngo) {
        for (i in 0..childrenSection.childCount) {
            var view = childrenSection.getChildAt(i)

            if (view is NgoChildView) {
                var childView = view as NgoChildView

                for (child in it.children) {
                    if (childView.child.id.equals(child.id)) {
                        childView.setFunds(child.gatheredFunds)
                        childView.setNeededFunds(child.neededFunds)
                        childView.invalidateFunds()
                    }
                }
            }
        }

        setDonations(it)

        headerView.invalidateNgo(it)
    }

    fun setDonations(it: Ngo) {
        donationsSection.removeAllViews()

        for (donation in it.donations.reversed()) {
            donationsSection.addView(NgoDonationView(context, donation), donationsSection.childCount)
        }

    }

    fun showChildDonationTooltip() {
        Handler().postDelayed(Runnable {
            if (childrenSection.childCount > 0) {
                (childrenSection.getChildAt(0) as NgoChildView).showTooltip()
            }
        }, 1000)
    }

}