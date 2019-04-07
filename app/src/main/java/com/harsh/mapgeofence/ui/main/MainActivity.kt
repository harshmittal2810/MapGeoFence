package com.harsh.mapgeofence.ui.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.harsh.mapgeofence.R
import com.harsh.mapgeofence.data.model.HubData
import com.harsh.mapgeofence.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class MainActivity : BaseActivity(), MainMvpView, OnMapReadyCallback {

    @Inject
    lateinit var mMainPresenter: MainPresenter<MainMvpView>

    @BindView(R.id.textViewData)
    lateinit var textViewData: TextView

    @BindView(R.id.lat)
    lateinit var etLat: AppCompatEditText
    @BindView(R.id.lng)
    lateinit var etLng: AppCompatEditText
    @BindView(R.id.accuracy)
    lateinit var etAccuracy: AppCompatEditText

    var mMap: GoogleMap? = null
    private val ALPHA_ADJUSTMENT = 0x77000000
    private var areaList = mutableListOf<LatLng>()

    override fun onMapReady(map: GoogleMap?) {
        if (mMap != null) {
            return
        }
        mMap = map
        startMapping()
    }

    private fun startMapping() {
        val context = this
        val inputStream = context.resources.openRawResource(R.raw.hub)
        val jsonString = Scanner(inputStream).useDelimiter("\\A").next()

        Log.e("Json", jsonString)

        val gson = Gson()
        val hubData = gson.fromJson(jsonString, HubData::class.java)

        areaList = ArrayList()
        if (hubData.results.geometry.isNotEmpty()) {
            var geoList = hubData.results.geometry[0]
            geoList.forEach {
                areaList.add(LatLng(it.lat!!, it.lon!!))
            }
        }

        var tolerance = 5.0 // meters
        val simplifiedTriangle = PolyUtil.simplify(areaList, tolerance)
        mMap?.addPolygon(
            PolygonOptions()
                .addAll(simplifiedTriangle)
                .fillColor(ContextCompat.getColor(this, R.color.map_fill) - ALPHA_ADJUSTMENT)
                .strokeColor(ContextCompat.getColor(this, R.color.map_stroke))
                .strokeWidth(5f)
        )

        val builder = LatLngBounds.Builder()
        builder.include(LatLng(hubData.results.bounds.maxlat, hubData.results.bounds.maxlon))
        builder.include(LatLng(hubData.results.bounds.minlat, hubData.results.bounds.minlon))

        /**initialize the padding for map boundary*/
        val padding = 50
        /**create the bounds from latlngBuilder to set into map camera*/
        val bounds = builder.build()
        /**create the camera with bounds and padding to set into map*/
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        /**call the map call back to know map is loaded or not*/
        mMap?.setOnMapLoadedCallback {
            /**set animated zoom camera into map */
            /**set animated zoom camera into map */
            mMap?.animateCamera(cu)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent()?.inject(this)
        setContentView(R.layout.activity_main)
        setUpMap()

        ButterKnife.bind(this)
        mMainPresenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        setUpMap()
    }


    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        mMainPresenter.detachView()
    }

    override fun showData(data: String) {
        textViewData.text = data
    }

    override fun showError(error: String) {
        textViewData.text = error
    }

    @OnClick(R.id.btnGo)
    internal fun btnGo() {
        val latti = etLat.text.toString()
        val longi = etLng.text.toString()
        val accuracy = etAccuracy.text.toString()

        if (latti.isNotBlank() && longi.isNotBlank() && accuracy.isNotBlank()) {

            val lat = latti.toDouble()
            val lng = longi.toDouble()
            val allowedDistance = accuracy.toInt()

            if (isLocationWithInArea(LatLng(lat, lng), areaList, allowedDistance)) {
                Snackbar.make(btnGo, "IN Between", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(btnGo, "Not IN Between", Snackbar.LENGTH_LONG).show()
            }
        } else {
            Snackbar.make(btnGo, "Field can't be empty", Snackbar.LENGTH_LONG).show()
        }
    }


    /**
     * @param tap point to check
     * @param vertices polygon points
     * @param accuracy allowed distance
     *
     * checking tap point is in between vertices by counting intersect point
     * if intersect point is even means point is outside
     * so checking distance of that point is less or equal to allowed distance
     */

    private fun isLocationWithInArea(tap: LatLng, vertices: MutableList<LatLng>, accuracy: Int): Boolean {
        var intersectCount = 0
        var isInside = false
        for (j in 0 until vertices.size - 1) {
            if (rayCastIntersect(tap, vertices[j], vertices[j + 1])) {
                intersectCount++
            }
        }

        if (intersectCount % 2 == 1) {
            isInside = true
        } else {
            for (j in 0 until vertices.size - 1) {
                val nearestPoint = findNearestPoint(tap, vertices)
                val distance = SphericalUtil.computeDistanceBetween(tap, nearestPoint)
                isInside = distance <= accuracy
            }
        }
        return isInside
    }


    /**
     * @param tap point to check
     * @param vertA polygon point 1
     * @param vertB polygon point 2
     * In this function checking point is intersect that line which is draw by two point
     * or not
     */
    private fun rayCastIntersect(tap: LatLng, vertA: LatLng, vertB: LatLng): Boolean {

        val aY = vertA.latitude
        val bY = vertB.latitude
        val aX = vertA.longitude
        val bX = vertB.longitude

        val pY = tap.latitude
        val pX = tap.longitude

        if (aY > pY && bY > pY ||
            aY < pY && bY < pY ||
            aX < pX && bX < pX
        ) {
            return false // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        val m = (aY - bY) / (aX - bX) // Rise over run
        val bee = -aX * m + aY // y = mx + b
        val x1 = (pY - bee) / m // algebra is neat!

        return x1 > pX
    }


    /**
     * finding the nearest point from polygon
     */
    private fun findNearestPoint(tap: LatLng?, target: List<LatLng>?): LatLng? {
        var distance = -1.0
        var minimumDistancePoint = tap

        if (tap == null || target == null) {
            return minimumDistancePoint
        }

        for (i in target.indices) {
            val point = target[i]

            var segmentPoint = i + 1
            if (segmentPoint >= target.size) {
                segmentPoint = 0
            }

            val currentDistance = PolyUtil.distanceToLine(tap, point, target[segmentPoint])
            if (distance == -1.0 || currentDistance < distance) {
                distance = currentDistance
                minimumDistancePoint = findNearestPoint(tap, point, target[segmentPoint])
            }
        }

        return minimumDistancePoint
    }

    /**
     * finding the nearest point from polygon
     */
    private fun findNearestPoint(p: LatLng, start: LatLng, end: LatLng): LatLng {
        if (start == end) {
            return start
        }

        val s0lat = Math.toRadians(p.latitude)
        val s0lng = Math.toRadians(p.longitude)
        val s1lat = Math.toRadians(start.latitude)
        val s1lng = Math.toRadians(start.longitude)
        val s2lat = Math.toRadians(end.latitude)
        val s2lng = Math.toRadians(end.longitude)

        val s2s1lat = s2lat - s1lat
        val s2s1lng = s2lng - s1lng
        val u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng) / (s2s1lat * s2s1lat + s2s1lng * s2s1lng)
        if (u <= 0) {
            return start
        }
        return if (u >= 1) {
            end
        } else LatLng(
            start.latitude + u * (end.latitude - start.latitude),
            start.longitude + u * (end.longitude - start.longitude)
        )
    }
}
