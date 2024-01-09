package com.digiauxilio.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request


class UserPost : AppCompatActivity() {

    private var category_id: String = ""

    private var language_id: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage: Int = 0
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)

        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)

         userId = sh.getInt("user_id", 0)
        val sh1 = getSharedPreferences("language", MODE_PRIVATE)
        language_id = sh1.getString("language_id", "").toString()


        recyclerView = findViewById(R.id.khokho2)


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


        val backBtn = findViewById<Button>(R.id.backBtn11)


        backBtn.setOnClickListener {

            val intent = Intent(this@UserPost,UserProfile::class.java)
            startActivity(intent)
        }



    }
    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        adapter = ImageAdapter(this@UserPost)
        recyclerView.adapter = adapter
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
                .addFormDataPart("user_id","$userId")
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
}