package com.digiauxilio.storyapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import com.digiauxilio.storyapp.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.ArrayList
import java.util.Random
import java.util.zip.Inflater


class Home3 : AppCompatActivity() {

    private var category_id: String = ""
    private lateinit var userProfile: ImageView
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var constraint: ConstraintLayout
    private lateinit var homeIcon: ImageView
    private var language_id: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage: Int = 0
    private lateinit var progressBar: ProgressBar
    private var SHOW_ADMOB: Boolean = true
    private lateinit var linearRecycle:LinearLayout


    @SuppressLint("MissingInflatedId", "WrongViewCast", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {




        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home3)


//        if(SHOW_ADMOB){
//            loadInterAd()
//            SHOW_ADMOB = false
//        }






        // image call
        recyclerView = findViewById(R.id.khokho1)
        linearRecycle = findViewById(R.id.recycleLinear)


        layoutManager = GridLayoutManager(this, 2)

        setupRecyclerView()

        fetchData(page)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                //Log.d("MainActivity", "onScrollChange: ")
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total = adapter.itemCount
                if (page <= totalPage) {
                    if (visibleItemCount + pastVisibleItem >= total) {

                        page++
                        fetchData(page)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })




        constraint = findViewById(R.id.constraint)
        constraint.visibility = View.GONE
        val plusBtn = findViewById<ImageView>(R.id.smallerCircle)
        plusBtn.setOnClickListener {

            Toast.makeText(this@Home3, "In Progress", Toast.LENGTH_SHORT).show()
        }

        homeIcon = findViewById(R.id.homeIcon)


//        val lay = findViewById<LinearLayout>(R.id.helloLayout)
        userProfile = findViewById(R.id.peopleIcon)

        userProfile.setOnClickListener {

            val intent = Intent(this@Home3, UserProfile::class.java)
            startActivity(intent)
        }


        val sh = getSharedPreferences("language", MODE_PRIVATE)
        language_id = sh.getString("language_id", "").toString()
        val sharedPreferences = getSharedPreferences("category", MODE_PRIVATE)
        category_id = sharedPreferences.getString("category_id", "").toString()
        Log.d("language", language_id!!)

        val colorList = listOf(
            R.color.black,
            R.color.blue_color,
            R.color.green,
            R.color.yellow,
            R.color.red,
            R.color.pink
            // Add more color resources as needed
        )


        val inflater: LayoutInflater = LayoutInflater.from(this)

        val view1 = inflater.inflate(R.layout.one_layout, null)
        val view2 = inflater.inflate(R.layout.two_layout, null)


        val chipGroup1 = findViewById<ChipGroup>(R.id.horizontalChipGroup1)


        homeIcon.setOnClickListener {

            for (i in 0 until chipGroup1.childCount) {
                val chip = chipGroup1.getChildAt(i) as Chip
                chip.isChecked = false
            }

            category_id = ""


        }


        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.horizontalScrollView)


        val random = Random()





        // category call
        GlobalScope.launch(Dispatchers.IO) {

            val client = OkHttpClient()
            val mediaType = "text/plain".toMediaType()
            val request = Request.Builder()
                .url("https://statusstory.digiauxilio.com/index.php/api/categoryList")
                .build()



            try {
                val response = client.newCall(request).execute()

                val responseBody = response.body?.string()

//                            // Use the response as needed (e.g., update UI, handle success/failure)
                val registrationResponse = parseRegistrationResponse(responseBody)

                Log.d("response", registrationResponse?.data?.toString().toString())

                withContext(Dispatchers.Main) {
                    if (registrationResponse.status == 1) {

//                                // Toast.makeText(this@Login, "LOGIN SUCCESS \n User Successfully Logged In", Toast.LENGTH_SHORT).show()
                        Log.d("response", registrationResponse.data.toString())
                        for ((index, name) in registrationResponse?.data!!.withIndex()) {
                            val chip = Chip(this@Home3)
                            chip.text = registrationResponse.data!![index].name
                            chip.isCheckable = true
                            chip.isChecked = false

                            // Set the text color to white


                            // Set a random background color
                            val randomColorRes = colorList[random.nextInt(colorList.size)]
                            chip.setChipBackgroundColorResource(R.color.text)

                            val textColorBlack = resources.getColor(android.R.color.black, null)
                            chip.setTextColor(textColorBlack)

                            // Set rounded corners (border radius)
                            chip.chipCornerRadius =
                                resources.getDimension(R.dimen.chip_corner_radius)

                            // Add an OnCheckedChangeListener to handle chip selection
                            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                // Handle chip selection here
                                if (isChecked) {


                                    chip.setChipBackgroundColorResource(randomColorRes)
                                    val textColorWhite =
                                        resources.getColor(android.R.color.white, null)
                                    chip.setTextColor(textColorWhite)
                                    //chip.setStyle(ChipDrawable.createFromAttributes(this@Home3, null, 0, R.style.Widget_MaterialComponents_Chip_Filter))
                                    // Chip is selected
                                    // You can perform actions based on the selected chip
                                    if (category_id.isEmpty()) {
                                        // If category_id is empty, set it to the selected category_id
                                        category_id =
                                            registrationResponse.data!![index].category_id.toString()
                                    } else {
                                        // If category_id is not empty, append the selected category_id
                                        val id =
                                            registrationResponse.data!![index].category_id.toString()
                                        category_id += ",$id"
                                    }
                                    adapter.clear()
                                    //category_id = registrationResponse.data!![index].category_id.toString()
                                    page = 1
                                    fetchData(page)


                                } else if (!isChecked) {


                                    chip.setChipBackgroundColorResource(R.color.text)

                                    chip.setTextColor(textColorBlack)

                                    val id =
                                        registrationResponse.data!![index].category_id.toString()

                                    category_id =
                                        category_id.split(",").filter { it != id }.joinToString(",")

                                    // If only one chip is present, clear the category_id
                                    if (chipGroup1.childCount == 1) {
                                        category_id = ""
                                    }
                                    adapter.clear()
                                    page = 1
                                    fetchData(page)


                                }
                            }

                            // Add the chip to the current ChipGroup
                            chipGroup1.addView(chip)

                            // Check if we need to switch to the second ChipGroup

                        }
                    } else {

                    }
                }


                // Close the response to release resources
                response.close()

            } catch (e: Exception) {
                // Handle network request failure
                e.printStackTrace()
            }
        }


    }


    private fun parseRegistrationResponse(responseBody: String?): CategoryApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, CategoryApiResponse::class.java)
    }

    private fun parseRegistrationResponseImage(responseBody: String?): ImageApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, ImageApiResponse::class.java)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Do nothing to restrict the back button
        // You can add your custom logic here if needed
    }


    private fun fetchData(page: Int) {


        GlobalScope.launch(Dispatchers.IO) {


            val client = OkHttpClient()
            val mediaType = "text/plain".toMediaType()
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("language_id", language_id)
                .addFormDataPart("category_id", category_id)
                .addFormDataPart("page", "$page")
                .build()

            val request = Request.Builder()
                .url("https://statusstory.digiauxilio.com/index.php/api/get_post_image_all")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()

                val responseBody = response.body?.string()

                // Use the response as needed (e.g., update UI, handle success/failure)
                val registrationResponse = parseRegistrationResponseImage(responseBody)

                // Example: Log the message


                // Example: Log the response body
                Log.d("response", registrationResponse.toString())

                withContext(Dispatchers.Main) {

                    constraint.visibility = View.VISIBLE


                    if (registrationResponse.status == 1) {

                        totalPage = registrationResponse.total_page

//                        if(registrationResponse?.data!!.isEmpty()){
//
//                            Toast.makeText(this@Home3,"NO RESULT FOUND!",Toast.LENGTH_SHORT).show()
//
//                        }else{
                            adapter.addList(registrationResponse?.data!! as ArrayList<ImageRequest>)
                       // }


                    } else {

                    }


                }

                //progressBar.visibility = View.GONE
                // Close the response to release resources
                response.close()


            } catch (e: Exception) {
                // Handle network request failure
                e.printStackTrace()
            }
        }
    }


    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        adapter = ImageAdapter(this@Home3)
        recyclerView.adapter = adapter
    }


    private fun loadInterAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-6657092270609774/8440910410", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {

                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
                if (mInterstitialAd != null) {
                    mInterstitialAd?.fullScreenContentCallback  = object : FullScreenContentCallback(){
                        override fun onAdClicked() {
                            super.onAdClicked()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                        }

                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                        }

                    }

                    mInterstitialAd?.show(this@Home3)
                } else {

                }
            }
        })

    }





}
