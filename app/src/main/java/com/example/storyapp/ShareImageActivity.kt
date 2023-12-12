package com.example.storyapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ShareImageActivity : AppCompatActivity() {
    private lateinit var shareBtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_image)

        val setImage = findViewById<ImageView>(R.id.shareImage1)

        val image: String? = intent.getStringExtra("image_url")
        Log.d("image", image.toString())

        shareBtn = findViewById(R.id.shareBtn)
        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {

            val intent = Intent(this@ShareImageActivity, Home3::class.java)
            startActivity(intent)
        }

        if (image != "") {
            Picasso.get().load(image).into(setImage)
        }

        shareBtn.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {
                downloadAndShareImage(image!!)
            }

        }
    }

    private suspend fun downloadAndShareImage(imageUrl: String) {
        try {
            val file = File(cacheDir, "shared_image.jpg")
            val fileOutputStream = FileOutputStream(file)
            val bitmap: Bitmap = Picasso.get().load(imageUrl).get()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            // Share the image using Intent with FileProvider
            val uri = FileProvider.getUriForFile(
                this@ShareImageActivity,
                "${packageName}.provider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/jpeg"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share image"))
        } catch (e: IOException) {
            e.printStackTrace()
            GlobalScope.launch(Dispatchers.Main) {
                Toast.makeText(this@ShareImageActivity, "Failed to share image", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }
}

