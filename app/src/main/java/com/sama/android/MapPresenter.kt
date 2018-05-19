package com.sama.android

import android.util.Log
import android.view.View
import android.widget.Toast
import com.sama.android.domain.Ngo
import com.sama.android.network.Api
import com.sama.android.network.NetworkModule
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_login_progress.*
import java.util.*
import java.util.concurrent.TimeUnit


class MapPresenter(val view: MainAcitvity) {

    val api: Api = NetworkModule().api(view)
    val ngos = LinkedList<Ngo>()

    fun fetchNgos() {
        var disposable = api.ngos().repeatWhen { objectObservable: Observable<Any> -> objectObservable.delay(30, TimeUnit.SECONDS) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            ngos.clear()
                            ngos.addAll(it)
                            view.showNgos(it)
                        }, Consumer {
                            Toast.makeText(view, "Could not fetch NGOs", Toast.LENGTH_SHORT).show()
                        })

        view.addDisposable(disposable)
    }

    fun getNgo(ngoID: String): Ngo? {
        return ngos.firstOrNull { it.id == ngoID }
    }

    fun getNgos(): List<Ngo>  = ngos
}

