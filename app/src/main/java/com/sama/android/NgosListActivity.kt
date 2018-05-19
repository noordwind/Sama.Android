package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.sama.android.domain.Ngo
import com.sama.android.network.NetworkModule
import com.sama.android.views.NgoHeaderView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_ngo_preview.*
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

class NgosListActivity : AppCompatActivity() {

    companion object {
        fun show(context: Context, ngos: List<Ngo>) {
            val intent = Intent(context, NgosListActivity::class.java)
            intent.putExtra("NGO", NgosList(ngos))
            context.startActivity(intent)
            SamaAnimUtils.overrideEnterTransitionWithHorizontalSlide(context as Activity?)
        }
    }

    var disposable: Disposable? = null
    var headerViews: LinkedList<NgoHeaderView> = LinkedList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngo_preview)
        var animation = AnimationUtils.loadLayoutAnimation(baseContext, R.anim.layout_falldown_animation);
        root.setLayoutAnimation(animation);

        disposable = NetworkModule().api(this)
                .ngos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    var number = 1
                    for (ngo in it) {
                        var headerView = NgoHeaderView(this, ngo, headerClickable = true)
                        headerView.visibility = View.GONE
                        headerViews.add(headerView)
                        root.addView(headerView, root.childCount)

                        Handler().postDelayed(Runnable {
                            headerView.visibility = View.VISIBLE
                        }, (number++ * 50).toLong())
                    }
                }, Consumer {
                    Toast.makeText(baseContext, "Could not fetch ngos", Toast.LENGTH_SHORT).show()
                })
    }

    override fun onStart() {
        super.onStart()

        disposable = NetworkModule().api(this)
                .ngos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    for (ngo in it) {
                        for (headerView in headerViews) {
                            if (headerView.ngo.id.equals(ngo.id)) {
                                headerView.invalidateNgo(ngo)
                            }
                        }
                    }
                }, Consumer {
                    Toast.makeText(baseContext, "Could not fetch ngos", Toast.LENGTH_SHORT).show()
                })
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

class NgosList(val ngos: List<Ngo>) : Serializable