package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    companion object {
        fun show(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
            SamaAnimUtils.overrideEnterTransitionWithHorizontalSlide(context as Activity?)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    override fun onBackPressed() {
        SamaAnimUtils.overrideExitTransitionWithHorizontalSlide(this)
        super.onBackPressed()
    }
}