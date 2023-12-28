package com.digiauxilio.storyapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.digiauxilio.storyapp.R
import com.github.ybq.android.spinkit.SpinKitView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

class home2 : AppCompatActivity() {

    private lateinit var spinKit: SpinKitView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)

        spinKit = findViewById(R.id.spin_kit)
        val nextBtn = findViewById<Button>(R.id.nextBtn)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        //val list = mutableListOf<LanguageRequest>(LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"),LanguageRequest(1, "string"))
        //Log.d("list",list.toString())
        var list: List<LanguageRequest> = mutableListOf()

















        // Set the initial value of the TextView


        nextBtn.setOnClickListener {

            val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)

            val email = sharedPreferences.getString("email","")

            val sh = getSharedPreferences("language", AppCompatActivity.MODE_PRIVATE)
            val language = sh.getString("language_id","")

            if(language==""){
                Toast.makeText(this@home2,"Please Select Language to contine",Toast.LENGTH_SHORT).show()
            }else{
                if(email==""){
                    val intent = Intent(this,Login  ::class.java)
                    startActivity(intent)
                }else{
                    val intent = Intent(this,Home3  ::class.java)
                    startActivity(intent)
                }
            }







        }

        GlobalScope.launch(Dispatchers.IO) {

            spinKit.visibility = View.VISIBLE

            val client = OkHttpClient()
            val mediaType = "text/plain".toMediaType()
            val request = Request.Builder()
                .url("https://statusstory.digiauxilio.com/index.php/api/languageList")
                .build()



            try {
                val response = client.newCall(request).execute()

                val responseBody = response.body?.string()

//                            // Use the response as needed (e.g., update UI, handle success/failure)
                val registrationResponse = parseRegistrationResponse(responseBody)
//
//                            // Example: Log the message
//
//
//                            // Example: Log the response body
                Log.d("response", registrationResponse?.data?.toString().toString())

                withContext(Dispatchers.Main){
                    if(registrationResponse.status==1){


//                                // Toast.makeText(this@Login, "LOGIN SUCCESS \n User Successfully Logged In", Toast.LENGTH_SHORT).show()
                        list = registrationResponse?.data!! // Adjust this line based on your data structure
                        recyclerView.adapter = LanguageAdapter(this@home2,list as MutableList<LanguageRequest>)
                        //recyclerView.layoutManager = LinearLayoutManager(this@home2)

                        val layoutManager = LinearLayoutManager(this@home2, LinearLayoutManager.VERTICAL, false)
                        recyclerView.layoutManager = layoutManager
//
                        val snapHelper = LinearSnapHelper()
                        snapHelper.attachToRecyclerView(recyclerView)
//
//                        // Set up your adapter
                        val adapter = LanguageAdapter(this@home2,list as MutableList<LanguageRequest>)
                        recyclerView.adapter = adapter
//
//




                    }else if(registrationResponse.status==2){

                        //showDialog(this@UpdatePassword,"Some error occured while updating","UPDATE FAILED")


                    }
                }



                // Close the response to release resources
                response.close()

                spinKit.visibility = View.GONE


            } catch (e: Exception) {
                // Handle network request failure
                e.printStackTrace()
            }
        }


    }


    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    private fun parseRegistrationResponse(responseBody: String?): LanguageApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, LanguageApiResponse::class.java)
    }
    override fun onBackPressed() {
        // Do nothing to restrict the back button
        // You can add your custom logic here if needed
    }


}