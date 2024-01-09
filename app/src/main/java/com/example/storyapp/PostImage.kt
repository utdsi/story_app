package com.digiauxilio.storyapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.digiauxilio.storyapp.CategoryRequest
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.digiauxilio.storyapp.ApiResponse
import com.digiauxilio.storyapp.Home3
import com.digiauxilio.storyapp.ImageApiResponse
import com.digiauxilio.storyapp.LanguageRequest
import com.digiauxilio.storyapp.LoadingDialog
import com.digiauxilio.storyapp.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Wave
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class PostImage : AppCompatActivity() {

    private lateinit var category:String
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private var imageUri: Uri? = null
    private var image: Uri? = null
    private val PERMISSIONS_REQUEST_CAMERA_STORAGE = 3
    var category_id:Int = 0
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var language_id: String
    private lateinit var postDesc:EditText
    private lateinit var postTitle:EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_image)

        loadingDialog = LoadingDialog(this)
        val spinner: Spinner = findViewById(R.id.spinner)
        val spinner1: Spinner = findViewById(R.id.spinner1)
         postTitle = findViewById(R.id.postImageTitle)
         postDesc = findViewById(R.id.postImageDesc)
        val postImage = findViewById<ImageView>(R.id.postImage)
        val postBtn = findViewById<Button>(R.id.sendImageBtn)
        val backBtn = findViewById<Button>(R.id.backBtn10)
        backBtn.setOnClickListener {

            val intent = Intent(this,Home3::class.java)
            startActivity(intent)
        }

        postImage.setOnClickListener {


            if (hasPermissions(android.Manifest.permission.CAMERA) || hasPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showActionSheet()
            } else {
                requestPermissions()
            }
        }
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        val userId = sh.getInt("user_id", 0)

        Log.d("user",userId.toString())




        val list = getCategoryList()
        val lang_list = getLanguageList()

        val namesList = list.map { it.name }
        val langlist = lang_list.map{it.name}
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, namesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Handle the selected item
                val selectedItem = parent.getItemAtPosition(position).toString()
                // You can do something with the selected item here
                category = selectedItem

                //getCategoryId(category)
                category_id = list[position].category_id

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing here
            }
        }

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, langlist)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner1.adapter = adapter1

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Handle the selected item
                val selectedItem = parent.getItemAtPosition(position).toString()
                // You can do something with the selected item here


                //getCategoryId(category)
                language_id = lang_list[position].languages_id.toString()


                Log.d("list2",language_id.toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing here
            }
        }

        postBtn.setOnClickListener {
        showLoadingDialog()
            val titleValue = postTitle.text.toString()
             val descValue = postDesc.text.toString()

            if(image != null && titleValue != "" && descValue != ""){
                if (checkForInternet(this)) {
                    GlobalScope.launch(Dispatchers.IO) {


                        val client = OkHttpClient()
                        val mediaType = "text/plain".toMediaType()

                        val requestBody = when (image) {
                            is Uri -> {
                                // If the image data is a Uri (selected from the gallery)
                                contentResolver.openInputStream(image!!)?.use {
                                    it.readBytes().toRequestBody(mediaType)
                                } ?: throw IOException("Error reading image data from Uri")
                            }

                            else -> throw IllegalArgumentException("Unsupported image data type")
                        }

                        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("title", titleValue)
                            .addFormDataPart("description", descValue)
                            .addFormDataPart("category_id", category_id.toString())
                            .addFormDataPart("language_id", language_id.toString())
                            .addFormDataPart("user_id",userId.toString())
                            .addFormDataPart("image", "image.png", requestBody)
                            .build()

                        val request = Request.Builder()
                            .url("https://statusstory.digiauxilio.com/index.php/api/upload_post_image")
                            .post(body)
                            .build()

                        try {
                            val response = client.newCall(request).execute()

                            val responseBody = response.body?.string()

                            // Use the response as needed (e.g., update UI, handle success/failure)
                            val registrationResponse = parseRegistrationResponse(responseBody)

                            // Example: Log the message


                            // Example: Log the response body
                            Log.d("response", registrationResponse.toString())

                            dismissLoadingDialog()
                            if (registrationResponse.status == 1) {


                                // Toast.makeText(this@Login, "LOGIN SUCCESS \n User Successfully Logged In", Toast.LENGTH_SHORT).show()

                                (showDialogSuccess(
                                    this@PostImage,
                                    "Image uploaded successfully",
                                    "UPLOAD SUCCESS"
                                ))




                            } else if (registrationResponse.status == 2) {


//                            showDialog(
//                                this@UserProfile,
//                                "Some error occured while upload",
//                                "UPDATE FAILED"
//                            )
                            }


//                                // Close the response to release resources
                            response.close()

                        } catch (e: Exception) {
                            // Handle network request failure
                            e.printStackTrace()
                        }
                    }
                } else {
                    dismissLoadingDialog()
                    Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                }
            }else{
                dismissLoadingDialog()
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
            }


        }

    }




    fun getCategoryList(): List<CategoryRequest> {
        val prefs = getSharedPreferences("Category_List", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("CATEGORY_LIST", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<CategoryRequest>>() {}.type)
        } else {
            emptyList()
        }
    }
    fun getLanguageList(): List<LanguageRequest> {
        val prefs = getSharedPreferences("Language_List", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("LANGUAGE_LIST", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<LanguageRequest>>() {}.type)
        } else {
            emptyList()
        }
    }
    private fun showActionSheet() {
        val options = arrayOf("Photo Library", "Camera", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Image Source")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // Handle Photo Library selection
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, REQUEST_IMAGE_PICK)
                }

                1 -> {
                    // Handle Camera selection
                    val cameraPermission = Manifest.permission.CAMERA
                    if (ContextCompat.checkSelfPermission(
                            this,
                            cameraPermission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(cameraPermission),
                            REQUEST_IMAGE_CAPTURE
                        )
                    }
                }

                2 -> {

                }
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "ImageCapture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can proceed with the action
                    showActionSheet()
                } else {
                    // Permission denied

                }
            }
        }
    }

    private fun hasPermissions(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        val cameraPermission = Manifest.permission.CAMERA
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(
                this,
                cameraPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(cameraPermission)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                storagePermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(storagePermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSIONS_REQUEST_CAMERA_STORAGE
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val postImage = findViewById<ImageView>(R.id.postImage)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    postImage.setImageURI(selectedImageUri)
image=selectedImageUri
//                    selectedImageUri?.let { displayImage(it) }
//
//                    imageUri = null // Reset the image URI
                }

                REQUEST_IMAGE_CAPTURE -> {
                    postImage.setImageURI(imageUri)
image = imageUri
//                    imageUri?.let { displayImage(it) }
//                    imageUri = null // Reset the image URI
                }
            }
        }
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
        val spinKitView = loadingDialog.findViewById<SpinKitView>(R.id.spin_kit)
        spinKitView.setIndeterminateDrawable(Wave())
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

    private fun parseRegistrationResponse(responseBody: String?): ImageApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, ImageApiResponse::class.java)
    }

    private fun showDialogSuccess(context: Context, message: String, alert: String) {
        (context as? Activity)?.runOnUiThread {
            val builder = AlertDialog.Builder(context)

            // Set the dialog title and message
            builder.setTitle(alert)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    // Dismiss the dialog when the "OK" button is clicked
                    dialog.dismiss()
                    val intent  = Intent(this@PostImage,Home3::class.java)
                    startActivity(intent)
                }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()
        }
    }
    override fun onBackPressed() {
        // Do nothing to restrict the back button
        // You can add your custom logic here if needed
    }
}