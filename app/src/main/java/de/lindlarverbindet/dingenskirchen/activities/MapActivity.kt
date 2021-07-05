package de.lindlarverbindet.dingenskirchen.activities

import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.mapbox.geojson.Feature
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import de.lindlarverbindet.dingenskirchen.R
import org.json.JSONArray
import java.io.StringReader


class MapActivity: AppCompatActivity(), MapboxMap.OnMapClickListener {

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        Log.d("MAP", "onCreate called")

        mapView.getMapAsync { mapboxMap ->
            Log.d("MAP", "map Callback")
            mapboxMap.setStyle(Style.Builder().fromUri(getString(R.string.mapbox_custommap_uri))) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                // enable compass
                val uiSettings = mapboxMap.uiSettings
                uiSettings.isCompassEnabled = true
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

    override fun onMapClick(point: LatLng): Boolean {

        val stop = queryStopData(point).firstOrNull()
        val lines = queryLineData(point)
        if (stop != null) {
            val busIds = getLineNumbers(lines)
            Toast.makeText(this, "${stop.properties()?.get("name")}, line: ${escapeString(busIds.toString())}", Toast.LENGTH_LONG).show()
        }
        return false
    }

    /**
     * Queries Mapbox features on the selected point
     *
     * @param point LatLng value of the selected point
     *
     * @return List of all found features (stops) 
     */
    private fun queryStopData(point:LatLng): List<Feature> {
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
    private fun queryLineData(point:LatLng): List<Feature> {
        val screenPoint: PointF = mapboxMap.projection.toScreenLocation(point)
        val relationArea = RectF(screenPoint.x + 50, screenPoint.y + 50, screenPoint.x - 50, screenPoint.y - 50)
        return mapboxMap.queryRenderedFeatures(relationArea, getString(R.string.mapbox_bus_line_layer))
    }

    /**
     * Parses the Line Identifiers found in [{reltags -> ref}]
     *
     * @param lines List of the mapbox features containing the relation data
     *
     * @return List of the Line Identifiers
     */
    private fun getLineNumbers(lines:List<Feature>):List<String> {
        val resultList = arrayListOf<String>()
        for (line in lines) {
            val properties = line.properties()
            val jsonElementString = properties?.get("@relations")?.asString
            val jsonElement = Gson().fromJson(jsonElementString, JsonArray::class.java)
            val busId = jsonElement?.get(0)?.asJsonObject?.get("reltags")?.asJsonObject?.get("ref")
            if (busId != null) {
                resultList.add(busId.toString())
            }
        }
        return resultList.distinct().toList()
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