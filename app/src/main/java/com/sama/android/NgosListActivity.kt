package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.sama.android.domain.Ngo
import com.sama.android.views.NgoHeaderView
import kotlinx.android.synthetic.main.view_ngo_preview.*
import java.io.Serializable

class NgosListActivity : AppCompatActivity() {

    companion object {
        fun show(context: Context, ngos: List<Ngo>) {
            val intent = Intent(context, NgosListActivity::class.java)
            intent.putExtra("NGO", NgosList(ngos))
            context.startActivity(intent)
            SamaAnimUtils.overrideEnterTransitionWithHorizontalSlide(context as Activity?)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngo_preview)
        var animation = AnimationUtils.loadLayoutAnimation(baseContext, R.anim.layout_falldown_animation);
        root.setLayoutAnimation(animation);

        var ngosList = intent.getSerializableExtra("NGO") as NgosList
        var ngos = ngosList.ngos

        var number = 1
        for (ngo in ngos) {
            var headerView = NgoHeaderView(this, ngo)
            headerView.visibility = View.GONE
            root.addView(headerView, root.childCount)

            Handler().postDelayed(Runnable {
                headerView.visibility = View.VISIBLE
            }, (number++ * 50).toLong())
        }
    }

    override fun onBackPressed() {
        SamaAnimUtils.overrideExitTransitionWithHorizontalSlide(this)
        super.onBackPressed()
    }
}

class NgosList(val ngos: List<Ngo>) : Serializable