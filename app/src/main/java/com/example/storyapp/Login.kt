package com.example.storyapp

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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

class Login : AppCompatActivity() {

    private lateinit var loginBtn: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signupBtn = findViewById<TextView>(R.id.signupRedirect)
        val forgotBtn = findViewById<TextView>(R.id.fPassword)

        loginBtn = findViewById(R.id.loginBtn)
        email = findViewById(R.id.logEmail)
        password  = findViewById(R.id.logPassword)

        val backBtn = findViewById<Button>(R.id.backBtn2)
        backBtn.setOnClickListener {

            val intent = Intent(this@Login,home2::class.java)
            startActivity(intent)
        }


        signupBtn.setOnClickListener {
            val intent  = Intent(this,Signup::class.java)
            startActivity(intent)
        }

        forgotBtn.setOnClickListener {

            val intent = Intent(this,ForgotPassword::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener{

            val emailValue = email.text.toString()
            val passwordValue = password.text.toString()





            if(checkForInternet(this)){

                if(emailValue.isEmpty() || passwordValue.isEmpty()){
                    Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show()
                }else{
                    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    val emailRegex = Regex(emailPattern)

                    if(emailRegex.matches(emailValue)){

                        GlobalScope.launch(Dispatchers.IO) {
                            val client = OkHttpClient()
                            val mediaType = "text/plain".toMediaType()
                            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("email", emailValue)
                                .addFormDataPart("password", passwordValue)
                                .build()

                            val request = Request.Builder()
                                .url("https://statusstory.digiauxilio.com/index.php/api/login")
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


                                if(registrationResponse.status==1){

                                   // Toast.makeText(this@Login, "LOGIN SUCCESS \n User Successfully Logged In", Toast.LENGTH_SHORT).show()

                                    (showDialogSuccess(this@Login,"User Successfully Logged In","LOGIN SUCCESS"))

                                    val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                                    val myEdit = sharedPreferences.edit()

                                    // write all the data entered by the user in SharedPreference and apply
                                    myEdit.putString("name", registrationResponse?.data!![0]?.name.toString())
                                    myEdit.putInt("user_id", registrationResponse?.data[0]?.user_id.toString().toInt())
                                    myEdit.putString("dob", registrationResponse?.data!![0]?.dob.toString())
                                    myEdit.putInt("gender", registrationResponse?.data!![0]?.gender.toString().toInt())
                                    myEdit.putString("image",registrationResponse?.data!![0]?.profile_pic.toString())
                                    myEdit.putString("email",registrationResponse?.data!![0]?.email.toString())
                                    myEdit.apply()





                                }else if(registrationResponse.status==2){

                                    showDialog(this@Login,"Incorrect email or password","LOGIN FAILED")
                                }


//                                // Close the response to release resources
                                response.close()

                            } catch (e: Exception) {
                                // Handle network request failure
                                e.printStackTrace()
                            }
                        }

                    }else{
                        Toast.makeText(this, "Please enter the correct format of email", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
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

//    //parsing the response
    private fun parseRegistrationResponse(responseBody: String?): ApiResponse {
        // Use Gson to parse the response body into a RegistrationResponse object
        return Gson().fromJson(responseBody, ApiResponse::class.java)
    }

//    // shows dialog box
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
                    val intent  = Intent(this@Login,Home3::class.java)
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