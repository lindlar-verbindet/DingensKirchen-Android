package de.lindlarverbindet.dingenskirchen.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import de.lindlarverbindet.dingenskirchen.R

class MapActivity: AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        Mapbox.getInstance(applicationContext, "pk.eyJ1IjoicGl4ZWxza3VsbCIsImEiOiJja3B4dGVjOG0wNjcxMnJvNmkxMjgwNW5uIn0.X4Y1kWyTsmm6Y2yE2a2TUQ")
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        Log.d("MAP", "onCreate called")

        mapView.getMapAsync { mapboxMap ->
            Log.d("MAP", "map Callback")
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                Log.d("MAP", "Map is loaded and should be shown")
            }
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
            Log.d("MAP", "onResume called")
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        Log.d("MAP", "onPause called")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}