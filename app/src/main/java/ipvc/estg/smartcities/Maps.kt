package ipvc.estg.smartcities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.media.Image
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ipvc.estg.smartcities.api.EndPoints
import ipvc.estg.smartcities.api.MapIncidences
import ipvc.estg.smartcities.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Maps : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapIncidences: List<MapIncidences>

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST = 1

    //added to implement location periodic updates
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //iniciar biblioteca localizacao
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                val loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 1.0f))
            }
        }
        //pede a localização
        createLocationRequest()
        getPointsWS()
    }



    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 100000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // responsavel por ir buscar a localização apos o onResume
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }


    // Adiciona a localização
    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //coloca a bolinha na localização
            mMap.isMyLocationEnabled = true

            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            return
        }
    }

    //controla ações perante a resposta ao pedido de permissões
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                mMap.isMyLocationEnabled = true
            }
        } else {
            Toast.makeText(this, getString(R.string.gps_not_granted), Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocation()
        mMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))

        mMap.setOnMapLongClickListener() {
            val user_id = getSharedPreferences(getString(R.string.LoginData), Context.MODE_PRIVATE).getInt("id", 0)
            addPointWS(user_id,it.latitude, it.longitude, "teste", "description", "", 1, 0)
        }
    }

    class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

        var mContext = context
        var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_marker_window, null)

        private fun rendowWindowText(marker: Marker, view: View){

            val tvTitle = view.findViewById<TextView>(R.id.cm_title)
            val tvDescription = view.findViewById<TextView>(R.id.cm_description)
            val bt_delete = view.findViewById<ImageButton>(R.id.cm_delete)

            bt_delete.setOnClickListener {
                Log.d("###LOGG", marker.id.toString())
            }

            tvTitle.text = marker.title
            tvDescription.text = marker.snippet

        }

        override fun getInfoContents(marker: Marker): View {
            rendowWindowText(marker, mWindow)
            return mWindow
        }

        override fun getInfoWindow(marker: Marker): View? {
            rendowWindowText(marker, mWindow)
            return mWindow
        }
    }

    /**
     *  Vai buscar os pontos ao servidor
     */
    private fun getPointsWS() {
        //vai buscar o id para controlar a cor das marks por utilizador
        val id = getSharedPreferences(getString(R.string.LoginData), Context.MODE_PRIVATE).getInt("id", 0)

        // adiciona pontos no mapa por webservices
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getMapPoints()
        var position: LatLng

        call.enqueue(object : Callback<List<MapIncidences>> {
            override fun onResponse(
                call: Call<List<MapIncidences>>,
                response: Response<List<MapIncidences>>
            ) {
                mapIncidences = response.body()!!
                for (map in mapIncidences) {
                    position = LatLng(map.latCoordinates, map.longCoordinates)

                    // verifica se são pins do utilizador logado
                    if (id == map.users_id) {
                        mMap.addMarker(MarkerOptions().position(position).title(map.title).snippet(map.description)
                            .icon(BitmapDescriptorFactory.
                                //fromBitmap()))
                            defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    } else {
                        mMap.addMarker(MarkerOptions().position(position).title(map.title).snippet(map.description))
                    }
                }
            }
            override fun onFailure(call: Call<List<MapIncidences>>, t: Throwable) {
                Toast.makeText(this@Maps, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * adiciona mark no servidor
     */
    fun addPointWS(user_id: Int, latCoordinates: Double, longCoordinates: Double,
                 title: String, description: String, image: String, carTrafficProblem: Int, solved: Int) {

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.addPoint(user_id, latCoordinates, longCoordinates, title, description, image, carTrafficProblem, solved)

        call.enqueue(object : Callback<MapIncidences> {
            override fun onResponse(call: Call<MapIncidences>, response: Response<MapIncidences>) {
                Toast.makeText(this@Maps, getString(R.string.new_incidence), Toast.LENGTH_SHORT).show()
                getPointsWS()
            }

            override fun onFailure(call: Call<MapIncidences>, t: Throwable) {
                Toast.makeText(this@Maps, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     *  eliminar pontos por id
     */
    fun deletePointWS(id: Int) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.deletePoint(id)

        call.enqueue(object : Callback<MapIncidences> {
            override fun onResponse(call: Call<MapIncidences>, response: Response<MapIncidences>) {
                Toast.makeText(this@Maps, getString(R.string.mark_was_deleted), Toast.LENGTH_SHORT).show()
                getPointsWS()
            }

            override fun onFailure(call: Call<MapIncidences>, t: Throwable) {
                Toast.makeText(this@Maps, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
        *MENU DE OPCOES
    **/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu!!.findItem(R.id.notesMenu).setVisible(true)
        menu.findItem(R.id.logoutMenu).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notesMenu -> {
                val intent = Intent(this, Notes::class.java)
                startActivity(intent)
                true
            }
            R.id.logoutMenu -> {
                //limpa o ficheiro do SP
                val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.LoginData), Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }


}


