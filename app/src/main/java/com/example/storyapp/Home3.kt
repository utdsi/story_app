package com.example.storyapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        var modalList = ArrayList<Modal>()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home3)

//        val lay = findViewById<LinearLayout>(R.id.helloLayout)
        userProfile = findViewById(R.id.peopleIcon)

        userProfile.setOnClickListener {

            val intent = Intent(this@Home3,UserProfile::class.java)
            startActivity(intent)
        }


        val sh = getSharedPreferences("language", MODE_PRIVATE)
        val language_id = sh.getString("language_id", "")
        val sharedPreferences = getSharedPreferences("category", MODE_PRIVATE)
        category_id = sharedPreferences.getString("category_id","").toString()
         Log.d("language", language_id!!)

        val names = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5","Option 6","Option 7","Option 8","Option 9","Option 10","Option 1", "Option 2", "Option 3", "Option 4", "Option 5","Option 6","Option 7","Option 8","Option 9","Option 10")
        val colorList = listOf(
            R.color.black,
            R.color.blue_color,
            R.color.green,
            R.color.yellow,
            R.color.red,
            R.color.pink
            // Add more color resources as needed
        )

        val images = intArrayOf(R.drawable.image13,R.drawable.image14,R.drawable.image15,R.drawable.image16,R.drawable.image17,R.drawable.image18,R.drawable.image19,R.drawable.image20,R.drawable.image13,R.drawable.image14,R.drawable.image15,R.drawable.image16,R.drawable.image17,R.drawable.image18,R.drawable.image19,R.drawable.image20)


        for(i in images.indices){

            modalList.add(Modal(images[i]))
        }


        //val lay = findViewById<LinearLayout>(R.id.helloLayout)
        val inflater: LayoutInflater = LayoutInflater.from(this)

        val view1 = inflater.inflate(R.layout.one_layout,null)
        val view2 = inflater.inflate(R.layout.two_layout,null)











        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
        val chipGroup1 = findViewById<ChipGroup>(R.id.horizontalChipGroup1)


        val random = Random()



        // Create a variable to keep track of the current row of chips




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
//
//                            // Example: Log the message
//
//
//                            // Example: Log the response body
                Log.d("response", registrationResponse?.data?.toString().toString())

                withContext(Dispatchers.Main){
                    if(registrationResponse.status==1){


//                                // Toast.makeText(this@Login, "LOGIN SUCCESS \n User Successfully Logged In", Toast.LENGTH_SHORT).show()
                        Log.d("response",registrationResponse.data.toString())
                        for ((index, name) in registrationResponse?.data!!.withIndex()) {
                            val chip = Chip(this@Home3)
                            chip.text = registrationResponse.data!![index].name
                            chip.isCheckable = true
                            chip.isChecked = false

                            // Set the text color to white


                            // Set a random background color
                            val randomColorRes = colorList[random.nextInt(colorList.size)]
                            chip.setChipBackgroundColorResource(randomColorRes)

                            val textColorWhite = resources.getColor(android.R.color.white, null)
                            chip.setTextColor(textColorWhite)

                            // Set rounded corners (border radius)
                            chip.chipCornerRadius = resources.getDimension(R.dimen.chip_corner_radius)

                            // Add an OnCheckedChangeListener to handle chip selection
                            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                // Handle chip selection here
                                if (isChecked) {
                                    chip.setChipBackgroundColorResource(R.color.text)
                                    val textColorWhite = resources.getColor(android.R.color.black, null)
                                    chip.setTextColor(textColorWhite)
                                    //chip.setStyle(ChipDrawable.createFromAttributes(this@Home3, null, 0, R.style.Widget_MaterialComponents_Chip_Filter))
                                    // Chip is selected
                                    // You can perform actions based on the selected chip
                                    if (category_id.isEmpty()) {
                                        // If category_id is empty, set it to the selected category_id
                                        category_id = registrationResponse.data!![index].category_id.toString()
                                    } else {
                                        // If category_id is not empty, append the selected category_id
                                        val id = registrationResponse.data!![index].category_id.toString()
                                        category_id += ",$id"
                                    }
                                    //category_id = registrationResponse.data!![index].category_id.toString()
                                    GlobalScope.launch(Dispatchers.IO) {
                                        val client = OkHttpClient()
                                        val mediaType = "text/plain".toMediaType()
                                        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("language_id", language_id)
                                            .addFormDataPart("category_id", category_id)
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




                                                if (registrationResponse.status == 1) {

                                                    val recyclerView: RecyclerView = findViewById(R.id.khokho1)
                                                    recyclerView.layoutManager = GridLayoutManager(this@Home3,2)

                                                    // Create and set the adapter
                                                    val imageAdapter = ImageAdapter(this@Home3,
                                                        registrationResponse.data as MutableList<ImageRequest>
                                                    )
                                                    recyclerView.adapter = imageAdapter



                                                } else {


                                                }






                                            }


//                                // Close the response to release resources
                                            response.close()

                                        } catch (e: Exception) {
                                            // Handle network request failure
                                            e.printStackTrace()
                                        }
                                    }



                                } else if(!isChecked) {

                                    val randomColorRes = colorList[random.nextInt(colorList.size)]
                                    chip.setChipBackgroundColorResource(randomColorRes)
                                    val textColorWhite = resources.getColor(android.R.color.white, null)
                                    chip.setTextColor(textColorWhite)

                                    val id = registrationResponse.data!![index].category_id.toString()

                                    category_id = category_id.split(",").filter { it != id }.joinToString(",")

                                    // If only one chip is present, clear the category_id
                                    if (chipGroup1.childCount == 1) {
                                        category_id = ""
                                    }
                                    GlobalScope.launch(Dispatchers.IO) {
                                        val client = OkHttpClient()
                                        val mediaType = "text/plain".toMediaType()
                                        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("language_id", language_id)
                                            .addFormDataPart("category_id", category_id)
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




                                                if (registrationResponse.status == 1) {

                                                    val recyclerView: RecyclerView = findViewById(R.id.khokho1)
                                                    recyclerView.layoutManager = GridLayoutManager(this@Home3,2)

                                                    // Create and set the adapter
                                                    val imageAdapter = ImageAdapter(this@Home3,
                                                        registrationResponse.data as MutableList<ImageRequest>
                                                    )
                                                    recyclerView.adapter = imageAdapter



                                                } else {


                                                }






                                            }


//                                // Close the response to release resources
                                            response.close()

                                        } catch (e: Exception) {
                                            // Handle network request failure
                                            e.printStackTrace()
                                        }
                                    }


                                }
                            }

                            // Add the chip to the current ChipGroup
                            chipGroup1.addView(chip)

                            // Check if we need to switch to the second ChipGroup

                        }


                    }else{

                        //showDialog(this@UpdatePassword,"Some error occured while updating","UPDATE FAILED")


                    }
                }



                // Close the response to release resources
                response.close()

            } catch (e: Exception) {
                // Handle network request failure
                e.printStackTrace()
            }
        }


        // image call
        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val mediaType = "text/plain".toMediaType()
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("language_id", language_id)
               .addFormDataPart("category_id", category_id)
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




                        if (registrationResponse.status == 1) {

                            val recyclerView: RecyclerView = findViewById(R.id.khokho1)
                            recyclerView.layoutManager = GridLayoutManager(this@Home3,2)

                            // Create and set the adapter
                            val imageAdapter = ImageAdapter(this@Home3,
                                registrationResponse.data as MutableList<ImageRequest>
                            )
                            recyclerView.adapter = imageAdapter



                        } else {


                        }






                }


//                                // Close the response to release resources
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


}