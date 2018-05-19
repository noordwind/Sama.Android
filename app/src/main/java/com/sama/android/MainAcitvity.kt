package com.sama.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.LocationManager
import android.location.LocationListener
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import com.flipboard.bottomsheet.BottomSheetLayout
import com.google.android.gms.maps.model.Marker
import com.sama.android.domain.Ngo
import com.sama.android.login.LoginActivity
import com.sama.android.views.NgoPreviewView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*


class MainAcitvity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    companion object {
        fun login(context: Context) {
            val intent = Intent(context, MainAcitvity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    private lateinit var mMap: GoogleMap
    private val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var locationManager: LocationManager
    private lateinit var presenter: MapPresenter
    private lateinit var compositeDisposable: CompositeDisposable
    private var markers = LinkedList<Marker>()
    private lateinit var bottomSheet: BottomSheetLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_acitvity)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        presenter = MapPresenter(this)

        bottomSheet = findViewById(R.id.bottomsheet)
    }

    override fun onStart() {
        super.onStart()
        compositeDisposable = CompositeDisposable()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap = googleMap
        mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap?.setOnMarkerClickListener(this)
        mMap?.setMinZoomPreference(10f)

        if (checkLocationPermission()) {
            mMap.isMyLocationEnabled = true;
            mMap.getUiSettings().isMyLocationButtonEnabled = true;
            listenForLocation()
        }

        if (mMap != null) {
            presenter.fetchNgos()
        }
    }

    @SuppressLint("MissingPermission")
    fun listenForLocation() {
        // Acquire a reference to the system Location Manager
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0.toLong(), 0.toFloat(), this)
    }


    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Permission was granted.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true;
                    mMap.getUiSettings().isMyLocationButtonEnabled = true;
                }

            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
        // other 'case' lines to check for other permissions this app might request.
        //You can add here other case statements according to your requirement.
    }

    override fun onLocationChanged(location: Location?) {
        mMap?.let {
            location?.let {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f))
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.let {
            var ngoID = marker.snippet

            ngoID?.let {
                var ngo = presenter.getNgo(ngoID)


                ngo?.let {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(ngo.latitude, ngo.longitude)))

                    var previewContainer = LayoutInflater.from(baseContext).inflate(R.layout.view_ngo_preview_container, bottomSheet, false) as FrameLayout
                    var ngoPreview = NgoPreviewView(this, ngo = ngo)
                    previewContainer.addView(ngoPreview)

                    bottomSheet.showWithSheetView(previewContainer)
                }
            }
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        if (locationManager != null) {
            locationManager.removeUpdates(this)
        }

        if (compositeDisposable != null && !compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    fun showNgos(ngos: List<Ngo>) {
        for (marker in markers) {
            marker.remove()
        }

        for (ngo in ngos) {
            mMap.addMarker(MarkerOptions()
                    .snippet(ngo.id)
                    .position(LatLng(ngo.latitude, ngo.longitude))
                    .title(ngo.name))
            mMap.setOnMarkerClickListener(this)
        }
    }

    fun addDisposable(disposable: Disposable?) {
        compositeDisposable.add(disposable)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ngos -> showNgosList()
            R.id.profile -> showProfile()
            R.id.logout -> logout()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showProfile() {
        ProfileActivity.show(this)
    }


    private fun logout() {
        TheApp.sessionToken = null
        LoginActivity.login(baseContext)
        finish()
    }

    private fun showNgosList() {
        NgosListActivity.show(this, presenter.getNgos())
    }
}
