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
import com.sama.android.TheApp
import com.sama.android.network.Api
import com.sama.android.network.AuthRequest
import com.sama.android.network.NetworkModule
import com.sama.android.network.SignUpRequest
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.view_login_progress.*

class LoginActivity : AppCompatActivity() {

    companion object {
        fun login(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    private val api = NetworkModule().api()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (TheApp.sessionToken != null) {
            MainAcitvity.login(this)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        loginButton.setOnClickListener { login() }
        signupButton.setOnClickListener { signUp() }
        retrievePasswordButton.setOnClickListener { }

    }

    fun login() {
        progressView.visibility = View.VISIBLE
        var authRequest = AuthRequest(email = emailInput.text.toString(), password = passwordInput.text.toString())
        api.login(authRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    TheApp.sessionToken = it.accessToken
                    onLoggedIn()
                }, Consumer {
                    onError()
                })

    }

    fun signUp() {
        progressView.visibility = View.VISIBLE
        var signupRequest = SignUpRequest(email = emailInput.text.toString(), password = passwordInput.text.toString())
        api.signUp(signupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    onSignUp()
                }, Consumer {
                    progressView.visibility = View.GONE
                    Toast.makeText(baseContext, "Could not sign up", Toast.LENGTH_SHORT).show()
                })

    }

    fun onLoggedIn() {
        progressView.visibility = View.GONE
        Toast.makeText(baseContext, "Logged in", Toast.LENGTH_SHORT).show()
        MainAcitvity.login(baseContext)
        finish()
    }

    fun onError() {
        progressView.visibility = View.GONE
        Toast.makeText(baseContext, "Could not log in", Toast.LENGTH_SHORT).show()
    }

    fun onSignUp() {
        progressView.visibility = View.GONE
        Toast.makeText(baseContext, "You are now registered", Toast.LENGTH_SHORT).show()
    }
}

