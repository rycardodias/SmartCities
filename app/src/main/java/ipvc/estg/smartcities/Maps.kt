package ipvc.estg.smartcities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.google.maps.android.SphericalUtil
import ipvc.estg.smartcities.api.EndPoints
import ipvc.estg.smartcities.api.MapIncidences
import ipvc.estg.smartcities.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class Maps : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapIncidences: List<MapIncidences>

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST = 1

    //added to implement location periodic updates
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    //guarda id dos marcadores
    val markerIdMapping: HashMap<Marker, Int> = HashMap()
    lateinit var marker: Marker

    private var createMarker = 1
    private var editMarker = 2
    var userDriving = 0
    var callPoints = false


    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sharedPreferences = getSharedPreferences(getString(R.string.LoginData), Context.MODE_PRIVATE)

//        /**
//         * localização martelada
//         */
//        var targetlocation = Location(LocationManager.GPS_PROVIDER)
//        targetlocation.latitude = 0.0
//        targetlocation.longitude = 0.0
//        lastLocation = targetlocation

        //iniciar biblioteca localizacao
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d("###LOG", "locationCallback")

                    //necessário para nao crashar se lastLocation==null
                    if (!callPoints) {
                        getPointsWS(userDriving)
                        callPoints = true
                    }

                    lastLocation = location
                    val loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f))
                }
            }
        }

//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(p0: LocationResult) {
//                super.onLocationResult(p0)
//                lastLocation = p0.lastLocation
//                val loc = LatLng(lastLocation.latitude, lastLocation.longitude)
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f))
//            }
//        }

        //pede a localização
        createLocationRequest()


        //fab
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@Maps, AddMarker::class.java)
            startActivityForResult(intent, createMarker)
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // responsavel por ir buscar a localização apos o onResume
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        Log.d("###LOG", "startLocationUpdates")
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // Adiciona a localização
    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //coloca a bolinha na localização
            mMap.isMyLocationEnabled = true

            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
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
        Log.d("###LOG", "ONMAP")
        mMap = googleMap
        getLastLocation()
//        getPointsWS(userDriving)

        googleMap.setOnInfoWindowLongClickListener {
            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.getMapPointsById(markerIdMapping.get(it)!!)

            call.enqueue(object : Callback<MapIncidences> {
                override fun onResponse(call: Call<MapIncidences>, response: Response<MapIncidences>) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        if (data?.users_id == sharedPreferences.getInt("id", 0)) {

                            val alertDialogBuilder = AlertDialog.Builder(this@Maps).setTitle(getString(R.string.do_you_want_to_modify_marker))
                            alertDialogBuilder.setNeutralButton(R.string.edit) { dialog, which ->
                                editarMarker(data.id, data.title, data.description, "", data.carTrafficProblem)
                            }
                            alertDialogBuilder.setNegativeButton("Delete") { dialog, which ->
                                deletePointWS(data.id)
                            }
                            alertDialogBuilder.show()
                        }
                    }
                }

                override fun onFailure(call: Call<MapIncidences>, t: Throwable) {
                    Toast.makeText(this@Maps, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    /**
     *  Vai buscar os pontos ao servidor
     */
    private fun getPointsWS(driving: Int) {
        //vai buscar o id para controlar a cor das marks por utilizador
        val id = sharedPreferences.getInt("id", 0)

        // adiciona pontos no mapa por webservices
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getMapPoints()
        var position: LatLng

        call.enqueue(object : Callback<List<MapIncidences>> {
            @SuppressLint("MissingPermission")
            override fun onResponse(call: Call<List<MapIncidences>>, response: Response<List<MapIncidences>>) {
                mMap.clear()
                mapIncidences = response.body()!!

                val actualLocation = LatLng(42.0269, -8.6422)

//                val actualLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                var distancia: Double

                mMap.addMarker(MarkerOptions().position(actualLocation).title("map.title").snippet("map.description")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))

                for (map in mapIncidences) {
                    position = LatLng(map.latCoordinates, map.longCoordinates)

                    distancia = SphericalUtil.computeDistanceBetween(position, actualLocation)

                    //DISTANCIA
                    if (distancia < 3500) {
                        //VISTA DRIVING
                        if (map.carTrafficProblem == driving) {
                            // verifica se são pins do utilizador logado
                            if (id == map.users_id) {
                                marker = mMap.addMarker(MarkerOptions().position(position).title(map.title).snippet(map.description)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                            } else {
                                marker = mMap.addMarker(MarkerOptions().position(position).title(map.title).snippet(map.description))
                            }
                            markerIdMapping.put(marker, map.id)
                        }
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
    fun addPointWS(user_id: Int, latCoordinates: Double, longCoordinates: Double, title: String, description: String, image: String, carTrafficProblem: Int) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.addPoint(user_id, latCoordinates, longCoordinates, title, description, image, carTrafficProblem, 0)

        call.enqueue(object : Callback<MapIncidences> {
            override fun onResponse(call: Call<MapIncidences>, response: Response<MapIncidences>) {
                Toast.makeText(this@Maps, getString(R.string.new_incidence), Toast.LENGTH_SHORT).show()
                getPointsWS(userDriving)
            }

            override fun onFailure(call: Call<MapIncidences>, t: Throwable) {
                Toast.makeText(this@Maps, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updatePointWS(id: Int, title: String, description: String, image: String, carTrafficProblem: Int) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.updatePoint(id, title, description, image, carTrafficProblem, 0)

        call.enqueue(object : Callback<MapIncidences> {
            override fun onResponse(call: Call<MapIncidences>, response: Response<MapIncidences>) {
                Toast.makeText(this@Maps, getString(R.string.mark_was_updated), Toast.LENGTH_SHORT).show()
                getPointsWS(userDriving)
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
                getPointsWS(userDriving)
            }

            override fun onFailure(call: Call<MapIncidences>, t: Throwable) {
                Toast.makeText(this@Maps, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * cria o intent com os valores para adicionar markers
     */
    private fun editarMarker(id: Int, title: String, description: String, image: String, carTrafficProblem: Int) {
        val intent = Intent(this@Maps, AddMarker::class.java)
        intent.putExtra("ID", id)
        intent.putExtra("TITLE", title)
        intent.putExtra("DESCRIPTION", description)
//      intent.putExtra("IMAGE", data.image)
        intent.putExtra("CARTRAFFICPROBLEM", carTrafficProblem)
        startActivityForResult(intent, editMarker)
    }

    /**
     * Recebe os parametros passados no AddMarker
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val id = data?.getIntExtra(AddMarker.ID, 0)
            val title = data?.getStringExtra(AddMarker.TITLE)
            val description = data?.getStringExtra(AddMarker.DESCRIPTION)
//            val image = data?.getIntExtra(AddMarker.IMAGE, 0)
            val carTrafficProblem = data?.getIntExtra(AddMarker.CARTRAFFICPROBLEM, 0)

            val user_id = sharedPreferences.getInt("id", 0)

            if (requestCode == createMarker) {
                addPointWS(user_id, lastLocation.latitude, lastLocation.longitude, title.toString(), description.toString(),
                        "", carTrafficProblem!!)
            } else if (requestCode == editMarker) {
                updatePointWS(id!!, title.toString(), description.toString(), "", carTrafficProblem!!)
            }

        } else {
            Toast.makeText(applicationContext, getString(R.string.any_field_was_changed), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     *MENU DE OPCOES
     **/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu!!.findItem(R.id.notesMenu).isVisible = true
        menu.findItem(R.id.logoutMenu).isVisible = true
        menu.findItem(R.id.drivingMenu).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notesMenu -> {
                val intent = Intent(this, Notes::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.logoutMenu -> {
                //limpa o ficheiro do SP
                sharedPreferences.edit().clear().apply()

                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.drivingMenu -> {
                if (item.isChecked()) {
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    userDriving = 0
//                    mBold = false;
                } else {
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    userDriving = 1
                }
                getPointsWS(userDriving)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        callPoints = false
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }


    /**
     * picasso
     */

    /**
     * Janelinha no mapa
     */
//    class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {
//        var mContext = context
//        var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_marker_window, null)
//
//        private fun rendowWindowText(marker: Marker, view: View) {
//
//            val tvTitle = view.findViewById<TextView>(R.id.cm_title)
//            val tvDescription = view.findViewById<TextView>(R.id.cm_description)
//            val bt_delete = view.findViewById<ImageButton>(R.id.cm_delete)
//            val im_foto = view.findViewById<ImageView>(R.id.im_foto)
//
//            tvTitle.text = marker.title.toString()
//            tvDescription.text = marker.snippet
//
//        }
//
//        override fun getInfoContents(marker: Marker): View {
//            rendowWindowText(marker, mWindow)
//            return mWindow
//        }
//
//        override fun getInfoWindow(marker: Marker): View? {
//            rendowWindowText(marker, mWindow)
//            return mWindow
//        }
//    }


}


