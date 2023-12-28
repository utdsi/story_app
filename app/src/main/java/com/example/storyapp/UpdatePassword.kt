package com.digiauxilio.storyapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Wave
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

class UpdatePassword : AppCompatActivity() {
    private lateinit var displayEmail: EditText
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var submitBtn: Button
    private var email: String? = null
    private lateinit var loadingDialog: LoadingDialog

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        loadingDialog = LoadingDialog(this)


        displayEmail = findViewById(R.id.updatePasswordEmail)
        oldPassword = findViewById(R.id.oldPassword)
        newPassword = findViewById(R.id.newPassword)
        submitBtn = findViewById(R.id.submitUpdatePasswordBtn)
        val backBtn = findViewById<Button>(R.id.backBtn6)

        backBtn.setOnClickListener {

            val intent = Intent(this@UpdatePassword, UserProfile::class.java)
            startActivity(intent)
        }

        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        email = sh.getString("email", "")


        displayEmail.setText(email)

        submitBtn.setOnClickListener {


            val newPasswordValue = newPassword.text.toString()
            val oldPasswordValue = oldPassword.text.toString()

            if (newPasswordValue.isEmpty() || oldPasswordValue.isEmpty()) {
                Toast.makeText(this, "Please fill all the rows", Toast.LENGTH_SHORT).show()
            } else {
                showSimpleDialog()
            }


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

    private fun showDialog(context: Context, message: String, alert: String) {
        (context as? Activity)?.runOnUiThread {
            val builder = AlertDialog.Builder(context)

            // Set the dialog title and message
            builder.setTitle(alert)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    // Dismiss the dialog when the "OK" button is clicked
                    dialog.dismiss()

                    val intent = Intent(this@UpdatePassword, UserProfile::class.java)
                    startActivity(intent)
                }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()
        }

    }

    private fun parseRegistrationResponse(responseBody: String?): ApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, ApiResponse::class.java)
    }

    private fun showSimpleDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Update Password")
        builder.setMessage("Do you want to proceed?")

        builder.setPositiveButton("Yes") { dialog, which ->
            // Handle Yes button click

            updatePassword()
            // ...
            dialog.dismiss()


        }

        builder.setNegativeButton("No") { dialog, which ->
            // Handle No button click
            // ...
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun updatePassword() {

        showLoadingDialog()
        if (checkForInternet(this)) {
            val newPasswordValue = newPassword.text.toString()
            val oldPasswordValue = oldPassword.text.toString()

            if (newPasswordValue.isEmpty() || oldPasswordValue.isEmpty()) {
                Toast.makeText(this, "Please fill all the rows", Toast.LENGTH_SHORT).show()
            } else {


                GlobalScope.launch(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val mediaType = "text/plain".toMediaType()
                    val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("email", email.toString())
                        .addFormDataPart("old_password", oldPasswordValue.toString())
                        .addFormDataPart("new_password", newPasswordValue.toString())
                        .build()

                    val request = Request.Builder()
                        .url("https://statusstory.digiauxilio.com/index.php/api/updatePassword")
                        .post(requestBody)
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
                        Log.d("response", registrationResponse.toString())

                        dismissLoadingDialog()
                        if (registrationResponse.status == 1) {

                            showDialogSuccessUpdate(
                                this@UpdatePassword,
                                "Password updated successfully",
                                "UPDATE SUCCESS"
                            )

//                                // Toast.makeText(this@Login, "LOGIN SUCCESS \n User Successfully Logged In", Toast.LENGTH_SHORT).show()


                        } else if (registrationResponse.status == 2) {

                            showDialog(
                                this@UpdatePassword,
                                "Some error occured while updating",
                                "UPDATE FAILED"
                            )
                        }


////                                // Close the response to release resources
                        response.close()

                    } catch (e: Exception) {
                        // Handle network request failure
                        e.printStackTrace()
                    }
                }
            }


        } else {

            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showDialogSuccessUpdate(context: Context, message: String, alert: String) {
        (context as? Activity)?.runOnUiThread {
            val builder = AlertDialog.Builder(context)

            // Set the dialog title and message
            builder.setTitle(alert)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    // Dismiss the dialog when the "OK" button is clicked
                    dialog.dismiss()
                    val intent = Intent(this@UpdatePassword, Login::class.java)
                    startActivity(intent)
                }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()
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
}