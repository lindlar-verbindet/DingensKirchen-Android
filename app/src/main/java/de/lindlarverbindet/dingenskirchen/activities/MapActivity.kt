package de.lindlarverbindet.dingenskirchen.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.attribution.Attribution
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.databinding.ActivityMapBinding


class MapActivity: AppCompatActivity(), MapboxMap.OnMapClickListener, PermissionsListener {

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetTitle: TextView
    private lateinit var bottomSheetDescription: TextView

    private lateinit var binding: ActivityMapBinding
    private var permissionsManager: PermissionsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.supportActionBar?.title = "Mobil"

        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.mapContextBottomSheet.mapBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING

        bottomSheetTitle = binding.mapContextBottomSheet.bottomSheetTitle
        bottomSheetDescription = binding.mapContextBottomSheet.bottomSheetDescription
        linkPhoneNumber()

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        Log.d("MAP", "onCreate called")

        mapView.getMapAsync { mapboxMap ->
            Log.d("MAP", "map Callback")
            mapboxMap.setStyle(Style.Builder().fromUri(getString(R.string.mapbox_custommap_uri))) { style ->
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                // enable compass
                val uiSettings = mapboxMap.uiSettings
                uiSettings.isCompassEnabled = true
                uiSettings.logoGravity = Gravity.AXIS_PULL_BEFORE
                uiSettings.attributionGravity = Gravity.AXIS_CLIP

                enableLocationComponent(style)
            }
            mapboxMap.addOnMapClickListener(this)
            this.mapboxMap = mapboxMap
        }
    }

    override fun onStart() {
        super.onStart()
        if (this::mapView.isInitialized) {
            mapView.onStart()
            Log.d("MAP", "onStart called")
        }
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        Log.d("MAP", "onStop called")
    }

    override fun onResume() {
        super.onResume()
        if (this::mapView.isInitialized) {
            mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get an instance of the component
            val locationComponent = mapboxMap.locationComponent

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build())

            // Enable to make component visible
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            locationComponent.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING

            // Set the component's render mode
            locationComponent.renderMode = RenderMode.COMPASS
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
            finish()
        }
    }


    override fun onMapClick(point: LatLng): Boolean {

        val stop = queryStopData(point).firstOrNull()
        val lines = queryLineData(point)
        if (stop != null) {
            val busIds = getLineInfo(lines)
            runOnUiThread {
                bottomSheetTitle.text = stop.properties()?.get("name")?.asString
                var busLines = ""
                busIds.forEach {
                    busLines += "${it} <br>"
                }
                if (busLines != "") {
                    bottomSheetDescription.text = HtmlCompat.fromHtml(busLines, HtmlCompat.FROM_HTML_MODE_LEGACY)
                } else {
                    setDefaultDescription()
                }
                bottomSheetDescription.movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            runOnUiThread {
                bottomSheetTitle.text = getString(R.string.bottom_sheet_title)
                setDefaultDescription()
            }
        }
        return false
    }

    private fun setDefaultDescription() {
        bottomSheetDescription.text = getString(R.string.bottom_sheet_desc_placeholder)
        linkPhoneNumber()
    }

    private fun linkPhoneNumber() {
        Linkify.addLinks(bottomSheetDescription, Linkify.PHONE_NUMBERS)
        bottomSheetDescription.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Queries Mapbox features on the selected point
     *
     * @param point LatLng value of the selected point
     *
     * @return List of all found features (stops)
     */
    private fun queryStopData(point: LatLng): List<Feature> {
        val screenPoint: PointF = mapboxMap.projection.toScreenLocation(point)
        return mapboxMap.queryRenderedFeatures(screenPoint, getString(R.string.mapbox_bus_stop_layer))
    }

    /**
     * Queries Mapbox data in an Area of 50px around the selected Point
     *
     * @param point LatLng Position on the mapbox map
     *
     * @return list of all found features
     */
    private fun queryLineData(point: LatLng): List<Feature> {
        val screenPoint: PointF = mapboxMap.projection.toScreenLocation(point)
        val relationArea = RectF(screenPoint.x + 50, screenPoint.y + 50, screenPoint.x - 50, screenPoint.y - 50)
        return mapboxMap.queryRenderedFeatures(relationArea, getString(R.string.mapbox_bus_line_layer))
    }

    /**
     * Parses the Line Information found in [{reltags}] like Bus Number, from and to
     *
     * @param lines List of the mapbox features containing the relation data
     *
     * @return List of the Line Identifiers
     */
    private fun getLineInfo(lines: List<Feature>):List<String> {
        val resultList = arrayListOf<String>()
        for (line in lines) {
            val properties = line.properties()
            val jsonElementString = properties?.get("@relations")?.asString
            val jsonElement = Gson().fromJson(jsonElementString, JsonArray::class.java)
            val busId = jsonElement?.get(0)?.asJsonObject?.get("reltags")?.asJsonObject?.get("ref")
            val from = jsonElement?.get(0)?.asJsonObject?.get("reltags")?.asJsonObject?.get("from")
            val to = jsonElement?.get(0)?.asJsonObject?.get("reltags")?.asJsonObject?.get("to")
            Log.d("RELTAGS", jsonElement?.get(0)?.asJsonObject?.get("reltags")?.toString()
                    ?: "empty")
            if (busId != null) {
                val linkTarget = getDeparturePlan(busId.asString)
                resultList.add("${linkTarget}: $from -> $to")
            }
        }
        return resultList.distinct().toList()
    }

    private fun getDeparturePlan(busId: String): String {
        return "<a href=\"https://docs.google.com/viewer?url=https://www.vrs.de/his/minifahrplan/de:vrs:$busId\">$busId</a>"
    }

    /**
     * Removes backslash from string
     *
     * @param target String to remove the escape character ("\") from
     *
     * @return target String without "\"
     */
    private fun escapeString(target: String): String {
        return target.replace("\\", "", true)
    }
}