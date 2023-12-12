package com.example.storyapp

import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

//class bdhb {
//    if (registrationResponse.status == 1) {
//        var index = 0
//        while (index < registrationResponse.data?.size!!) {
//            // Inflate the first_layout.xml for every two images
//
//
//            val view1 = inflater.inflate(R.layout.one_layout, null)
//            val imageView1 = view1.findViewById<ImageView>(R.id.oneDisplay)
//            val imageView2 = view1.findViewById<ImageView>(R.id.twoDisplay)
//
//
//            Picasso.get().load(registrationResponse.data!![index].image).into(imageView1)
//            imageView1.id = index
//            imageView1.setOnClickListener {
//
//                val sharedPreferences = getSharedPreferences("shareImage",
//                    AppCompatActivity.MODE_PRIVATE
//                )
//                val myEdit = sharedPreferences.edit()
//
//                // write all the data entered by the user in SharedPreference and apply
//                myEdit.putString("image", registrationResponse.data!![index].image)
//                myEdit.apply()
//                val intent = Intent(this@Home3,ShareImageActivity::class.java)
//                startActivity(intent)
//            }
//
//            index++
//
//            if (index < registrationResponse.data?.size!!) {
//                Picasso.get().load(registrationResponse.data!![index].image).into(imageView2)
//                imageView2.id = index
//                imageView2.setOnClickListener {
//
//                    val sharedPreferences = getSharedPreferences("shareImage",
//                        AppCompatActivity.MODE_PRIVATE
//                    )
//                    val myEdit = sharedPreferences.edit()
//
//                    // write all the data entered by the user in SharedPreference and apply
//                    myEdit.putString("image", registrationResponse.data!![index].image)
//                    myEdit.apply()
//                    val intent = Intent(this@Home3,ShareImageActivity::class.java)
//                    startActivity(intent)
//                }
//                index++
//            }
//
//            lay.addView(view1)
//
//            // Inflate the second_layout.xml for every three images
//            if (index < registrationResponse.data?.size!!) {
//                val view2 = inflater.inflate(R.layout.two_layout, null)
//                val imageView3 = view2.findViewById<ImageView>(R.id.threeDisplay)
//                val imageView4 = view2.findViewById<ImageView>(R.id.fourDisplay)
//                val imageView5 = view2.findViewById<ImageView>(R.id.fiveDisplay)
//
//
//                if (index < registrationResponse.data?.size!!) {
//                    Picasso.get().load(registrationResponse.data!![index].image).into(imageView3)
//                    imageView3.id = index
//                    imageView3.setOnClickListener {
//
//                        val sharedPreferences = getSharedPreferences("shareImage",
//                            AppCompatActivity.MODE_PRIVATE
//                        )
//                        val myEdit = sharedPreferences.edit()
//
//                        // write all the data entered by the user in SharedPreference and apply
//                        myEdit.putString("image", registrationResponse.data!![index].image)
//                        myEdit.apply()
//                        val intent = Intent(this@Home3,ShareImageActivity::class.java)
//                        startActivity(intent)
//                    }
//                    index++
//                }
//
//                if (index < registrationResponse.data?.size!!) {
//                    Picasso.get().load(registrationResponse.data!![index].image).into(imageView4)
//                    imageView4.id = index
//                    imageView4.setOnClickListener {
//
//                        val sharedPreferences = getSharedPreferences("shareImage",
//                            AppCompatActivity.MODE_PRIVATE
//                        )
//                        val myEdit = sharedPreferences.edit()
//
//                        // write all the data entered by the user in SharedPreference and apply
//                        myEdit.putString("image", registrationResponse.data!![index].image)
//                        myEdit.apply()
//                        val intent = Intent(this@Home3,ShareImageActivity::class.java)
//                        startActivity(intent)
//                    }
//                    index++
//                }
//
//                imageView5.id = index
//                if (index < registrationResponse.data?.size!!) {
//                    Picasso.get().load(registrationResponse.data!![index].image).into(imageView5)
//                    imageView5.setOnClickListener {
//
//                        val sharedPreferences = getSharedPreferences("shareImage",
//                            AppCompatActivity.MODE_PRIVATE
//                        )
//                        val myEdit = sharedPreferences.edit()
//
//                        // write all the data entered by the user in SharedPreference and apply
//                        myEdit.putString("image", registrationResponse.data!![index].image)
//                        myEdit.apply()
//                        val intent = Intent(this@Home3,ShareImageActivity::class.java)
//                        startActivity(intent)
//                    }
//                    index++
//
//                }
//
//                lay.addView(view2)
//            }
//
//        }
//
//        Log.d("hello",registrationResponse.data.toString())
//
//
//
//    } else {
//
//
//    }
//}

//
//var index = 0
//while (index < registrationResponse.data?.size!!) {
//    // Inflate the first_layout.xml for every two images
//
//
//    val view1 = inflater.inflate(R.layout.one_layout, null)
//    val imageView1 = view1.findViewById<ImageView>(R.id.oneDisplay)
//    val imageView2 = view1.findViewById<ImageView>(R.id.twoDisplay)
//
//
//
//    Picasso.get().load(registrationResponse.data!![index].image).into(imageView1)
//    var image1 = ImageShare(index,registrationResponse.data!![index].image)
//
//
//
//    index++
//
//    if (index < registrationResponse.data?.size!!) {
//        Picasso.get().load(registrationResponse.data!![index].image).into(imageView2)
//
//        index++
//    }
//
//    lay.addView(view1)
//
//    // Inflate the second_layout.xml for every three images
//    if (index < registrationResponse.data?.size!!) {
//        val view2 = inflater.inflate(R.layout.two_layout, null)
//        val imageView3 = view2.findViewById<ImageView>(R.id.threeDisplay)
//        val imageView4 = view2.findViewById<ImageView>(R.id.fourDisplay)
//        val imageView5 = view2.findViewById<ImageView>(R.id.fiveDisplay)
//
//
//        if (index < registrationResponse.data?.size!!) {
//            Picasso.get().load(registrationResponse.data!![index].image).into(imageView3)
//
//            index++
//        }
//
//        if (index < registrationResponse.data?.size!!) {
//            Picasso.get().load(registrationResponse.data!![index].image).into(imageView4)
//
//            index++
//        }
//
//        imageView5.id = index
//        if (index < registrationResponse.data?.size!!) {
//            Picasso.get().load(registrationResponse.data!![index].image).into(imageView5)
//
//            index++
//
//        }
//
//        lay.addView(view2)
//    }
//
//}
//
//Log.d("hello",registrationResponse.data.toString())