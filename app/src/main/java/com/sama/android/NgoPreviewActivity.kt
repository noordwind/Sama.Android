package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.sama.android.domain.Ngo
import com.sama.android.network.Api
import com.sama.android.network.DonateRequest
import com.sama.android.network.NetworkModule
import com.sama.android.views.DonateDialog
import com.sama.android.views.NgoPreviewView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ngo_preview_single.*

class NgosPreviewActivity : AppCompatActivity(), DonateDialog.OnAccept {
    companion object {
        fun show(context: Context, ngo: Ngo) {
            val intent = Intent(context, NgosPreviewActivity::class.java)
            intent.putExtra("NGO", ngo)
            context.startActivity(intent)
            SamaAnimUtils.overrideEnterTransitionWithHorizontalSlide(context as Activity?)
        }
    }

    private lateinit var api: Api
    var disposable: Disposable? = null
    var donateDisposable: Disposable? = null
    var ngoPreviewView: NgoPreviewView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = NetworkModule().api(baseContext)
        setContentView(R.layout.activity_ngo_preview_single)
        var animation = AnimationUtils.loadLayoutAnimation(baseContext, R.anim.layout_falldown_animation);
        root.setLayoutAnimation(animation);

        fetchNgo()
    }

    fun fetchNgo() {
        findViewById<View>(R.id.progress).visibility = View.VISIBLE

        var ngo = intent.getSerializableExtra("NGO") as Ngo

        disposable = api.ngo(ngo.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    findViewById<View>(R.id.progress).visibility = View.GONE
                    showNgo(it)
                }, Consumer {
                    findViewById<View>(R.id.progress).visibility = View.GONE
                    Toast.makeText(baseContext, "Could not fetch NGO", Toast.LENGTH_SHORT).show()
                })
    }

    fun showNgo(ngo: Ngo) {
        ngoPreviewView = NgoPreviewView(this, ngo = ngo, headerClickable = false)
        title = ngo.name
        root.removeAllViews()
        root.addView(ngoPreviewView)
        root.visibility = View.VISIBLE
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

    private fun donate() {
        var donateDialog = DonateDialog()
        donateDialog.onAccept = this
        donateDialog.show(supportFragmentManager, "Donate")
    }

    override fun onAccept(donation: Int) {
        var ngo = intent.getSerializableExtra("NGO") as Ngo

        findViewById<View>(R.id.progress).visibility = View.VISIBLE
        donateDisposable = api.donate(ngo.id, DonateRequest(donation))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {

                    disposable = api.ngo(ngo.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Consumer {
                                var ngo = it
                                ngoPreviewView?.let {
                                    it.setNgo(ngo)
                                }

                                findViewById<View>(R.id.progress).visibility = View.GONE
                            }, Consumer {
                                findViewById<View>(R.id.progress).visibility = View.GONE
                                Toast.makeText(baseContext, "Could not fetch NGO", Toast.LENGTH_SHORT).show()
                            })
                }, Consumer {
                    findViewById<View>(R.id.progress).visibility = View.GONE
                    Toast.makeText(baseContext, "Insufficient funds", Toast.LENGTH_SHORT).show()
                })
    }

}

