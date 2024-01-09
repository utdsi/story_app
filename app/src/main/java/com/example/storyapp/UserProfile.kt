package com.digiauxilio.storyapp


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.View
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Wave
import com.squareup.picasso.Picasso
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class UserProfile : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var userName: TextView
    private lateinit var dateDisplay: TextView
    private lateinit var dateBtn: ImageView
    private lateinit var genderDisplay: TextView
    private lateinit var genderSet: RadioGroup
    private lateinit var deleteUser: TextView
    private lateinit var logOutUser: TextView
    private lateinit var maleSet: RadioButton
    private lateinit var femaleSet: RadioButton
    private lateinit var otherSet: RadioButton
    private lateinit var saveEditData: Button
    private lateinit var dob: String
    private lateinit var setImage: ImageView
    private lateinit var setImageBtn: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private var imageUri: Uri? = null
    private var pngFile: File? = null
    private val PERMISSIONS_REQUEST_CAMERA_STORAGE = 3
    private lateinit var changePassword: TextView
    private var userId: Int = 0

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var email: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        loadingDialog = LoadingDialog(this)


        userName = findViewById(R.id.userNameDisplay)
        dateDisplay = findViewById(R.id.bdayText)
        dateBtn = findViewById(R.id.setBdayText)
        genderDisplay = findViewById(R.id.genderText)
        genderSet = findViewById(R.id.setGenderText)
        deleteUser = findViewById(R.id.deleteProfile)
        logOutUser = findViewById(R.id.logOutProfile)
        maleSet = findViewById(R.id.radioButton1)
        femaleSet = findViewById(R.id.radioButton2)
        otherSet = findViewById(R.id.radioButton3)
        saveEditData = findViewById(R.id.saveEditData)
        setImage = findViewById(R.id.setUserImage)
        setImageBtn = findViewById(R.id.changeUserProfileImage)
        changePassword = findViewById(R.id.changePassword)

        val backBtn = findViewById<Button>(R.id.backBtn7)

        backBtn.setOnClickListener {

            val intent = Intent(this@UserProfile, Home3::class.java)
            startActivity(intent)
        }

        val userMyPost = findViewById<ImageView>(R.id.userMyPost)

        userMyPost.setOnClickListener {

            val intent = Intent(this@UserProfile,UserPost::class.java)
            startActivity(intent)
        }


        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val name = sh.getString("name", "")
        userId = sh.getInt("user_id", 0)
        dob = sh.getString("dob", "").toString()
        var gender = sh.getInt("gender", 1)
        var image = sh.getString("image", "")
        email = sh.getString("email", "").toString()

        userName.setText(name)
        dateDisplay.setText(dob)

        if (image != "") {
            Picasso.get().load(image).into(setImage)
        }

        changePassword.setOnClickListener {

            val intent = Intent(this, UpdatePassword::class.java)
            startActivity(intent)
        }
        deleteUser.setOnClickListener {

            showYesNoDialogDelete()


        }

        logOutUser.setOnClickListener {

            showYesNoDialog()


        }

        setImageBtn.setOnClickListener {
            showLoadingDialog()

            if (hasPermissions(android.Manifest.permission.CAMERA) || hasPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showActionSheet()
            } else {
                requestPermissions()
            }


        }



        if (gender == 1) {
            maleSet.isChecked = true
            femaleSet.isChecked = false
            otherSet.isChecked = false

        } else if (gender == 2) {
            femaleSet.isChecked = true
            maleSet.isChecked = false
            otherSet.isChecked = false

        } else {
            femaleSet.isChecked = false
            maleSet.isChecked = false
            otherSet.isChecked = true
        }

        genderSet.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.radioButton1 -> {
                    gender = 1
                    maleSet.isChecked = true
                }

                R.id.radioButton2 -> {
                    gender = 2
                    femaleSet.isChecked = true
                }

                R.id.radioButton3 -> {
                    gender = 3
                    otherSet.isChecked = true

                }
            }
        }

        dateBtn.setOnClickListener {
            showDatePickerDialog()
        }

        saveEditData.setOnClickListener {

            showLoadingDialog()
            if (checkForInternet(this)) {

                GlobalScope.launch(Dispatchers.IO) {

                    val client = OkHttpClient()
                    val mediaType = "text/plain".toMediaType()
                    val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("name", name.toString())
                        .addFormDataPart("user_id", userId.toString())
                        .addFormDataPart("gender", gender.toString())
                        .addFormDataPart("dob", dob.toString())
                        .build()

                    val request = Request.Builder()
                        .url("https://statusstory.digiauxilio.com/index.php/api/userEditProfile")
                        .post(requestBody)
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
                                this@UserProfile,
                                "User details updated successfully",
                                "UPDATE SUCCESS"
                            ))

                            val sharedPreferences =
                                getSharedPreferences("MySharedPref", MODE_PRIVATE)
                            val myEdit = sharedPreferences.edit()

                            // write all the data entered by the user in SharedPreference and apply
                            myEdit.putString(
                                "name",
                                registrationResponse?.data!![0]?.name.toString()
                            )
                            myEdit.putInt(
                                "user_id",
                                registrationResponse?.data[0]?.user_id.toString().toInt()
                            )
                            myEdit.putString("dob", registrationResponse?.data!![0]?.dob.toString())
                            myEdit.putInt(
                                "gender",
                                registrationResponse?.data!![0]?.gender.toString().toInt()
                            )
                            myEdit.apply()


                        } else if (registrationResponse.status == 2) {

                            showDialog(
                                this@UserProfile,
                                "Some error occured while updating",
                                "UPDATE FAILED"
                            )
                        }


//                                // Close the response to release resources
                        response.close()


                    } catch (e: Exception) {
                        // Handle network request failure
                        e.printStackTrace()
                    }
                }
            } else {
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
            }


        }


    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        // Set the minimum age allowed (12 years)
        val minimumAge = 12
        val minDateCalendar = Calendar.getInstance()
        minDateCalendar.add(Calendar.YEAR, -minimumAge)

        // Set the maximum date allowed (12 years ago from the current date)
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.YEAR, -minimumAge)

        // Create a DatePickerDialog with the current date as the default
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum and maximum date for the DatePicker

        datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis

        // Show the date picker dialog
        datePickerDialog.show()
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        // Format the selected date as "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        // Display the selected date
        dateDisplay.setText(formattedDate)

        dob = formattedDate
    }
//    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//        val selectedDate = Calendar.getInstance()
//        selectedDate.set(year, month, dayOfMonth)
//
//        // Calculate the minimum allowed date (12 years ago)
//        val minAllowedDate = Calendar.getInstance()
//        minAllowedDate.add(Calendar.YEAR, -12)
//
//        // Check if the selected date is at least 12 years ago
//
//            // Format the selected date as "dd-MM-yyyy"
//            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//            val formattedDate = dateFormat.format(selectedDate.time)
//
//            // Display the selected date
//            dateDisplay.setText(formattedDate)
//
//            dob = formattedDate
//
//    }

    private fun parseRegistrationResponse(responseBody: String?): ApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, ApiResponse::class.java)
    }

    private fun showDialog(context: Context, message: String, alert: String) {
        (context as? Activity)?.runOnUiThread {
            val builder = AlertDialog.Builder(context)

            // Set the dialog title and message
            builder.setTitle(alert)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    // Dismiss the dialog when the "OK" button is clicked
                    dialog.dismiss()
                }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()
        }
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
//                    val intent  = Intent(this@UserProfile,UserProfile::class.java)
//                    startActivity(intent)
                }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun showDialogSuccessDelete(context: Context, message: String, alert: String) {
        (context as? Activity)?.runOnUiThread {
            val builder = AlertDialog.Builder(context)

            // Set the dialog title and message
            builder.setTitle(alert)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    // Dismiss the dialog when the "OK" button is clicked
                    dialog.dismiss()
                    val intent = Intent(this@UserProfile, Signup::class.java)
                    startActivity(intent)
                }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    //checking internet
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
                    dismissLoadingDialog()
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

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    setImage.setImageURI(selectedImageUri)

                    selectedImageUri?.let { displayImage(it) }

                    imageUri = null // Reset the image URI
                }

                REQUEST_IMAGE_CAPTURE -> {
                    setImage.setImageURI(imageUri)
                    imageUri?.let { displayImage(it) }
                    imageUri = null // Reset the image URI
                }
            }
        }
    }


    private fun displayImage(imageData: Any) {
        if (checkForInternet(this)) {
            GlobalScope.launch(Dispatchers.IO) {


                val client = OkHttpClient()
                val mediaType = "text/plain".toMediaType()

                val requestBody = when (imageData) {
                    is Uri -> {
                        // If the image data is a Uri (selected from the gallery)
                        contentResolver.openInputStream(imageData)?.use {
                            it.readBytes().toRequestBody(mediaType)
                        } ?: throw IOException("Error reading image data from Uri")
                    }

                    else -> throw IllegalArgumentException("Unsupported image data type")
                }

                val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", userId.toString())
                    .addFormDataPart("profile_pic", "image.png", requestBody)
                    .build()

                val request = Request.Builder()
                    .url("https://statusstory.digiauxilio.com/index.php/api/userProfileImageUpdate")
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
                            this@UserProfile,
                            "User profile image updated successfully",
                            "UPDATE SUCCESS"
                        ))

                        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()

                        // write all the data entered by the user in SharedPreference and apply
                        myEdit.putString("name", registrationResponse?.data!![0]?.name.toString())
                        myEdit.putInt(
                            "user_id",
                            registrationResponse?.data[0]?.user_id.toString().toInt()
                        )
                        myEdit.putString("dob", registrationResponse?.data!![0]?.dob.toString())
                        myEdit.putInt(
                            "gender",
                            registrationResponse?.data!![0]?.gender.toString().toInt()
                        )
                        myEdit.putString(
                            "image",
                            registrationResponse?.data!![0]?.profile_pic.toString()
                        )
                        myEdit.apply()


                    } else if (registrationResponse.status == 2) {


                        showDialog(
                            this@UserProfile,
                            "Some error occured while upload",
                            "UPDATE FAILED"
                        )
                    }


//                                // Close the response to release resources
                    response.close()

                } catch (e: Exception) {
                    // Handle network request failure
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Do nothing to restrict the back button
        // You can add your custom logic here if needed
    }

    private fun showYesNoDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        // Set the dialog title and message
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Do you want to logout?")

        // Set positive button and its click listener
        alertDialogBuilder.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
            // Do something when "Yes" is clicked
            dialog.dismiss() // Close the dialog
            // Add your logic here for the "Yes" option
            if (checkForInternet(this)) {
                val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val editor = sh.edit()
                editor.clear()
                editor.apply()
                val sharedPreferences = getSharedPreferences("showDetails", Context.MODE_PRIVATE)

                val edit = sharedPreferences.edit()

                edit.clear()
                edit.apply()
                Toast.makeText(this, "User logged out successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
            }

        }

        // Set negative button and its click listener
        alertDialogBuilder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            // Do something when "No" is clicked
            dialog.dismiss() // Close the dialog
            // Add your logic here for the "No" option
        }

        // Create and show the alert dialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
        val spinKitView = loadingDialog.findViewById<SpinKitView>(R.id.spin_kit)
        spinKitView.setIndeterminateDrawable(Wave())
    }

    private fun showYesNoDialogDelete() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        // Set the dialog title and message
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure want to delete your profile?")

        // Set positive button and its click listener
        alertDialogBuilder.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
            // Do something when "Yes" is clicked
            dialog.dismiss() // Close the dialog
            // Add your logic here for the "Yes" option
            deleteProfile()

        }

        // Set negative button and its click listener
        alertDialogBuilder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            // Do something when "No" is clicked
            dialog.dismiss() // Close the dialog
            // Add your logic here for the "No" option
        }

        // Create and show the alert dialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteProfile() {

        showLoadingDialog()

        if (checkForInternet(this)) {

            GlobalScope.launch(Dispatchers.IO) {

                val client = OkHttpClient()
                val mediaType = "text/plain".toMediaType()
                val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("email", email.toString())
                    .build()

                val request = Request.Builder()
                    .url("https://statusstory.digiauxilio.com/index.php/api/deleteUser")
                    .post(requestBody)
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

                        (showDialogSuccessDelete(
                            this@UserProfile,
                            "User deleted successfully",
                            "DELETED SUCCESS"
                        ))

                        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()
                        myEdit.clear()
                        // write all the data entered by the user in SharedPreference and apply

                        myEdit.apply()


                    } else if (registrationResponse.status == 2) {


                        showDialog(
                            this@UserProfile,
                            "Some error occured while deleting",
                            "DELETE FAILED"
                        )
                    }


//                                // Close the response to release resources
                    response.close()


                } catch (e: Exception) {
                    // Handle network request failure
                    e.printStackTrace()
                }
            }

        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }
}