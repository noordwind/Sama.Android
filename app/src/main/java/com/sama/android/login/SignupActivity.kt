package com.sama.android.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.sama.android.MainAcitvity
import com.sama.android.R
import com.sama.android.Session
import com.sama.android.network.Api
import com.sama.android.network.AuthRequest
import com.sama.android.network.NetworkModule
import com.sama.android.network.SignUpRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.view_login_progress.*

class SignupActivity : AppCompatActivity() {

    companion object {
        fun signup(context: Context) {
            val intent = Intent(context, SignupActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup);

        api = NetworkModule().api(baseContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        signupButton.setOnClickListener { signUp() }

    }

    fun signUp() {
        progressView.visibility = View.VISIBLE
        var signupRequest = SignUpRequest(email = emailInput.text.toString(),
                password = passwordInput.text.toString(),
                username = usernameInput.text.toString())
        api.signUp(signupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Action {
                    onSignUp()
                }, Consumer {
                    progressView.visibility = View.GONE
                    Toast.makeText(baseContext, "Could not sign up", Toast.LENGTH_SHORT).show()
                })

    }

    fun onSignUp() {
        progressView.visibility = View.GONE
        Toast.makeText(baseContext, "Sign up success", Toast.LENGTH_SHORT).show()
    }
}