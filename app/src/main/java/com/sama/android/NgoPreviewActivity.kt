package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import com.sama.android.domain.Ngo
import com.sama.android.views.DonateDialog
import com.sama.android.views.NgoHeaderView
import com.sama.android.views.NgoPreviewView
import kotlinx.android.synthetic.main.activity_ngo_preview_single.*
import java.io.Serializable

class NgosPreviewActivity : AppCompatActivity() {

    companion object {
        fun show(context: Context, ngo: Ngo) {
            val intent = Intent(context, NgosPreviewActivity::class.java)
            intent.putExtra("NGO", ngo)
            context.startActivity(intent)
            SamaAnimUtils.overrideEnterTransitionWithHorizontalSlide(context as Activity?)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngo_preview_single)
        var animation = AnimationUtils.loadLayoutAnimation(baseContext, R.anim.layout_falldown_animation);
        root.setLayoutAnimation(animation);

        var ngo = intent.getSerializableExtra("NGO") as Ngo
        var ngoPreviewView = NgoPreviewView(baseContext, ngo = ngo)
        title = ngo.name
        root.addView(ngoPreviewView)
    }

    override fun onBackPressed() {
        SamaAnimUtils.overrideExitTransitionWithHorizontalSlide(this)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getMenuInflater();
        inflater.inflate(R.menu.ngo_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.donate -> donate()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun donate() {
        DonateDialog().show(supportFragmentManager, "Donate")
    }

}

