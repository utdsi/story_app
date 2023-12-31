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
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class Signup : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var loginRedirect: TextView
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var check: ImageView
    private lateinit var unCheck: ImageView
    private lateinit var emailValue: String
    private lateinit var passwordValue: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        loadingDialog = LoadingDialog(this)

        email = findViewById(R.id.registerEmail)
        name = findViewById(R.id.registerName)
        password = findViewById(R.id.registerPassword)
        loginRedirect = findViewById(R.id.sLRedirect)
        check = findViewById(R.id.checkBox1)
        unCheck = findViewById(R.id.unCheckBox1)

        check.setOnClickListener {

            unCheck.visibility = View.VISIBLE

            check.visibility = View.GONE
        }

        unCheck.setOnClickListener {

            check.visibility = View.VISIBLE

            unCheck.visibility = View.GONE
        }

        val backBtn = findViewById<Button>(R.id.backBtn3)
        backBtn.setOnClickListener {

            val intent = Intent(this@Signup, Login::class.java)
            startActivity(intent)
        }

        loginRedirect.setOnClickListener {

            val intent = Intent(this@Signup, Login::class.java)
            startActivity(intent)
        }


        val registerBtn = findViewById<Button>(R.id.registerBtn)


        registerBtn.setOnClickListener {
            showLoadingDialog()
            emailValue = email.text.toString()
            val nameValue = name.text.toString()
            passwordValue = password.text.toString()


            if (checkForInternet(this)) {

                if (emailValue.isEmpty() || nameValue.isEmpty() || passwordValue.isEmpty()) {
                    dismissLoadingDialog()
                    Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show()
                } else {
                    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    val emailRegex = Regex(emailPattern)

                    if (emailRegex.matches(emailValue)) {

                        val passwordRegex = Pattern.compile("^.{6,}$")

                        if (passwordRegex.matcher(passwordValue).matches()) {
                            // Use Kotlin Coroutine to perform the network operation asynchronously
                            GlobalScope.launch(Dispatchers.IO) {
                                val client = OkHttpClient()
                                val mediaType = "text/plain".toMediaType()
                                val requestBody =
                                    MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("name", nameValue)
                                        .addFormDataPart("email", emailValue)
                                        .addFormDataPart("password", passwordValue)
                                        .addFormDataPart("dob", "01-02-1990")
                                        .addFormDataPart("gender", "2")
                                        .build()

                                val request = Request.Builder()
                                    .url("https://statusstory.digiauxilio.com/index.php/api/registration")
                                    .post(requestBody)
                                    .build()

                                try {
                                    val response = client.newCall(request).execute()

                                    val responseBody = response.body?.string()

                                    // Use the response as needed (e.g., update UI, handle success/failure)
                                    val registrationResponse =
                                        parseRegistrationResponse(responseBody)

                                    // Example: Log the message


                                    // Example: Log the response body
                                    Log.d("response", registrationResponse.toString())
                                    dismissLoadingDialog()

//
                                    if (registrationResponse.status == 1) {

                                        showDialogSuccess(
                                            this@Signup,
                                            "User Successfully registered",
                                            "REGISTRATION SUCCESS"
                                        )


                                    } else if (registrationResponse.status == 2) {

                                        showDialog(
                                            this@Signup,
                                            "User already Exist, Please login",
                                            "REGISTRATION FAILED"
                                        )
                                    }


                                    // Close the response to release resources
                                    response.close()

                                } catch (e: Exception) {
                                    // Handle network request failure
                                    e.printStackTrace()
                                }
                            }
                        } else {

                            dismissLoadingDialog()

                            Toast.makeText(
                                this,
                                "Password should be minimum of 6 characters ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } else {
                        dismissLoadingDialog()
                        Toast.makeText(
                            this,
                            "Please enter the correct format of email ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            } else {
                dismissLoadingDialog()
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
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

    //parsing the response
    private fun parseRegistrationResponse(responseBody: String?): ApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, ApiResponse::class.java)
    }


    // shows dialog box
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


                    if (check.visibility == View.VISIBLE) {
                        val sharedPreferences =
                            getSharedPreferences("showDetails", Context.MODE_PRIVATE)

                        val editor = sharedPreferences.edit()


                        editor.putString("showEmail", emailValue)


                        editor.putString("showPassword", passwordValue)

                        editor.apply()
                    }
                    val intent = Intent(this@Signup, Login::class.java)

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

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
        val spinKitView = loadingDialog.findViewById<SpinKitView>(R.id.spin_kit)
        spinKitView.setIndeterminateDrawable(Wave())
    }
}