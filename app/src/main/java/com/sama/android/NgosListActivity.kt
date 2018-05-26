package com.sama.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import com.sama.android.domain.Ngo
import com.sama.android.network.NetworkModule
import com.sama.android.views.NgoHeaderView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ngo_preview.*
import java.io.Serializable
import java.util.*

class NgosListActivity : AppCompatActivity(), OnItemSelectedListener {

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                root.removeAllViews()
                showNgos(ngos)
            }
            1 -> {
                root.removeAllViews()
                var fileredNgos = ngos.filter { ngo -> ngo.state.equals("new", true) }
                if (fileredNgos.size == 1) {
                    Toast.makeText(this, "Found ${fileredNgos.size} ngo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Found ${fileredNgos.size} ngos", Toast.LENGTH_SHORT).show()
                }
                showNgos(fileredNgos)
            }
            2 -> {
                root.removeAllViews()
                var fileredNgos = ngos.filter { ngo -> ngo.state.equals("approved", true) }
                if (fileredNgos.size == 1) {
                    Toast.makeText(this, "Found ${fileredNgos.size} ngo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Found ${fileredNgos.size} ngos", Toast.LENGTH_SHORT).show()
                }
                showNgos(fileredNgos)
            }
            3 -> {
                root.removeAllViews()
                var fileredNgos = ngos.filter { ngo -> ngo.state.equals("rejected", true) }
                if (fileredNgos.size == 1) {
                    Toast.makeText(this, "Found ${fileredNgos.size} ngo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Found ${fileredNgos.size} ngos", Toast.LENGTH_SHORT).show()
                }
                showNgos(fileredNgos)
            }
        }
    }

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
    var ngos: List<Ngo> = LinkedList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngo_preview)
        var animation = AnimationUtils.loadLayoutAnimation(baseContext, R.anim.layout_falldown_animation);
        root.setLayoutAnimation(animation);

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
        }


        if (Session(baseContext).isAdmin || Session(baseContext).isNgo) {
            var adapter = ArrayAdapter.createFromResource(this, R.array.ngo_types, R.layout.support_simple_spinner_dropdown_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ngoTypes.setClickable(false);
            ngoTypes.setAdapter(adapter);
            ngoTypes.visibility = View.GONE
            ngoTypes.onItemSelectedListener = this
            ngoTypes.visibility = View.VISIBLE
        }

        disposable = NetworkModule().api(this)
                .ngos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    ngos = it
                    ngoTypes.visibility = View.VISIBLE
                    showNgos(ngos)
                }, Consumer {
                    Toast.makeText(baseContext, "Could not fetch NGOs", Toast.LENGTH_SHORT).show()
                })
    }

    fun showNgos(ngos: List<Ngo>) {
        root.removeAllViews()
        var number = 1
        for (ngo in ngos) {
            var headerView = NgoHeaderView(this, ngo, headerClickable = true)
            headerView.visibility = View.GONE
            headerViews.add(headerView)
            root.addView(headerView, root.childCount)

            Handler().postDelayed(Runnable {
                headerView.visibility = View.VISIBLE
            }, (number++ * 50).toLong())
        }
    }

    override fun onStart() {
        super.onStart()

        if (refetchNgos) {
            refetchNgos = false

            disposable = NetworkModule().api(this)
                    .ngos()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer {
                        ngos = it
                        ngoTypes.visibility = View.VISIBLE

                        when (ngoTypes.selectedItemPosition) {
                            0 -> {
                                root.removeAllViews()
                                showNgos(ngos)
                            }
                            1 -> {
                                root.removeAllViews()
                                var fileredNgos = ngos.filter { ngo -> ngo.state.equals("new", true) }
                                showNgos(fileredNgos)
                            }
                            2 -> {
                                root.removeAllViews()
                                var fileredNgos = ngos.filter { ngo -> ngo.state.equals("approved", true) }
                                showNgos(fileredNgos)
                            }
                            3 -> {
                                root.removeAllViews()
                                var fileredNgos = ngos.filter { ngo -> ngo.state.equals("rejected", true) }
                                showNgos(fileredNgos)
                            }
                        }
                    }, Consumer {
                        Toast.makeText(baseContext, "Could not fetch NGOs", Toast.LENGTH_SHORT).show()
                    })
        } else {
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
                        Toast.makeText(baseContext, "Could not fetch NGOs", Toast.LENGTH_SHORT).show()
                    })
        }
    }

    override fun onBackPressed() {
        SamaAnimUtils.overrideExitTransitionWithHorizontalSlide(this)
        super.onBackPressed()
    }

    var refetchNgos = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            refetchNgos = true
        }
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