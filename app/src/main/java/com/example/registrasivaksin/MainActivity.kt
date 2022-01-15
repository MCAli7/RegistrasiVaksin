package com.example.registrasivaksin.ui.main;

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.registrasivaksin.R
import com.example.registrasivaksin.networking.ApiClient
import com.example.registrasivaksin.ui.profile.ProfileActivity
import im.delight.android.location.SimpleLocation
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.*


class MainActivity : AppCompatActivity() {
    var REQ_PERMISSION = 100
    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0
    var strCurrentLatLong: String? = null
    var strImage: String? = null
    var simpleLocation: SimpleLocation? = null
    var pbLoading: ProgressBar? = null
    var mainAdapter: MainAdapter? = null
    var rvListHospital: RecyclerView? = null
    var tvCurrentLocation: TextView? = null
    var imageProfile: ImageView? = null
    var modelMainList: MutableList<ModelMain> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageProfile = findViewById(R.id.ImageProfile)
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation)
        pbLoading = findViewById(R.id.pbLoading)
        rvListHospital = findViewById(R.id.rvListHospital)
        setPermission()
        setStatusBar()
        setLocation()
        setInitLayout()

        //get data rumah sakit
        rumahSakit

        //get nama daerah
        currentLocation
    }

    private fun setLocation() {
        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        //get location
        strCurrentLatitude = simpleLocation.getLatitude()
        strCurrentLongitude = simpleLocation.getLongitude()

        //set location lat long
        strCurrentLatLong = "$strCurrentLatitude,$strCurrentLongitude"
    }

    private fun setInitLayout() {
        mainAdapter = MainAdapter(this@MainActivity, modelMainList)
        rvListHospital!!.setHasFixedSize(true)
        rvListHospital!!.layoutManager = LinearLayoutManager(this)
        rvListHospital!!.adapter = mainAdapter
        imageProfile!!.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private val currentLocation: Unit
        private get() {
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val addressList =
                    geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val strCurrentLocation = addressList[0].locality
                    tvCurrentLocation!!.text = strCurrentLocation
                    tvCurrentLocation!!.isSelected = true
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    private fun setPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQ_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_PERMISSION && resultCode == RESULT_OK) {

            //load data rumah sakit
            rumahSakit
        }
    }

    //get lat long
    private val rumahSakit:

    //handle photo result
            Unit
        private get() {
            pbLoading!!.visibility = View.VISIBLE
            AndroidNetworking.get(ApiClient.BASE_URL.toString() + strCurrentLatLong + ApiClient.TYPE + ApiClient.API_KEY)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            pbLoading!!.visibility = View.GONE
                            val jsonArrayResult = response.getJSONArray("results")
                            for (i in 0 until jsonArrayResult.length()) {
                                val jsonObjectResult = jsonArrayResult.getJSONObject(i)
                                val modelMain = ModelMain()
                                modelMain.setStrName(jsonObjectResult.getString("name"))
                                modelMain.setStrVicinity(jsonObjectResult.getString("vicinity"))

                                //get lat long
                                val jsonObjectGeo = jsonObjectResult.getJSONObject("geometry")
                                val jsonObjectLoc = jsonObjectGeo.getJSONObject("location")
                                modelMain.setLatLoc(jsonObjectLoc.getDouble("lat"))
                                modelMain.setLongLoc(jsonObjectLoc.getDouble("lng"))

                                //handle photo result
                                try {
                                    val jsonArrayImage = jsonObjectResult.getJSONArray("photos")
                                    for (x in 0 until jsonArrayImage.length()) {
                                        val jsonObjectData = jsonArrayImage.getJSONObject(x)
                                        strImage = jsonObjectData.getString("photo_reference")
                                        modelMain.setStrPhoto(strImage)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    modelMain.setStrPhoto(null)
                                }
                                modelMainList.add(modelMain)
                            }
                            mainAdapter!!.notifyDataSetChanged()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@MainActivity,
                                "Gagal menampilkan data!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        pbLoading!!.visibility = View.GONE
                        Toast.makeText(
                            this@MainActivity,
                            "Tidak ada jaringan internet!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }
}