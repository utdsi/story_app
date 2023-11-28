package com.example.storyapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.ArrayList
import java.util.Random
import java.util.zip.Inflater


class Home3 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        var modalList = ArrayList<Modal>()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home3)

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {

            val intent  = Intent(this,Login::class.java)
            startActivity(intent)
        }


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

//        var customAdapter = customAdapter(modalList,this)
//
//        val gridView = findViewById<GridView>(R.id.gridView)
//
//        gridView.adapter = customAdapter
        val lay = findViewById<LinearLayout>(R.id.helloLayout)
        val inflater: LayoutInflater = LayoutInflater.from(this)

//        val view1 = inflater.inflate(R.layout.one_layout,null)
//        val view2 = inflater.inflate(R.layout.two_layout,null)

        var index = 0
        while (index < images.size) {
            // Inflate the first_layout.xml for every two images
            val view1 = inflater.inflate(R.layout.one_layout, null)
            val imageView1 = view1.findViewById<ImageView>(R.id.oneDisplay)
            val imageView2 = view1.findViewById<ImageView>(R.id.twoDisplay)

            imageView1.setImageResource(images[index])
            index++

            if (index < images.size) {
                imageView2.setImageResource(images[index])
                index++
            }

            lay.addView(view1)

            // Inflate the second_layout.xml for every three images
            if (index < images.size) {
                val view2 = inflater.inflate(R.layout.two_layout, null)
                val imageView3 = view2.findViewById<ImageView>(R.id.threeDisplay)
                val imageView4 = view2.findViewById<ImageView>(R.id.fourDisplay)
                val imageView5 = view2.findViewById<ImageView>(R.id.fiveDisplay)

                if (index < images.size) {
                    imageView3.setImageResource(images[index])
                    index++
                }

                if (index < images.size) {
                    imageView4.setImageResource(images[index])
                    index++
                }

                if (index < images.size) {
                    imageView5.setImageResource(images[index])
                    index++
                }

                lay.addView(view2)
            }

        }









        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
        val chipGroup1 = findViewById<ChipGroup>(R.id.horizontalChipGroup1)
        val chipGroup2 = findViewById<ChipGroup>(R.id.horizontalChipGroup2)

        val random = Random()



        // Create a variable to keep track of the current row of chips
        var currentChipGroup = chipGroup1

        for ((index, name) in names.withIndex()) {
            val chip = Chip(this)
            chip.text = name
            chip.isCheckable = true
            chip.isChecked = false

            // Set the text color to white
            chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))

            // Set a random background color
            val randomColorRes = colorList[random.nextInt(colorList.size)]
            chip.setChipBackgroundColorResource(randomColorRes)

            // Set rounded corners (border radius)
            chip.chipCornerRadius = resources.getDimension(R.dimen.chip_corner_radius)

            // Add an OnCheckedChangeListener to handle chip selection
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                // Handle chip selection here
                if (isChecked) {
                    // Chip is selected
                    // You can perform actions based on the selected chip
                } else {
                    // Chip is deselected
                    // You can handle deselection if needed
                }
            }

            // Add the chip to the current ChipGroup
            currentChipGroup.addView(chip)

            // Check if we need to switch to the second ChipGroup
            if (index + 1 == names.size / 2) {
                currentChipGroup = chipGroup2
            }
        }












    }


    class customAdapter(
        var itemModel: ArrayList<Modal>,
        var context: Context


    ): BaseAdapter(){

        var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @SuppressLint("SuspiciousIndentation")
        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {

         var view = view

            if(view==null){
                view = layoutInflater.inflate(R.layout.grid_item,viewGroup,false)
            }

            var tvImage = view?.findViewById<ImageView>(R.id.gridImage)

            tvImage?.setImageResource(itemModel[position].image!!)


            return view!!


        }



        override fun getItem(p0: Int): Any {

            return itemModel[p0]

        }

        override fun getItemId(p0: Int): Long {

            return p0.toLong()

        }

        override fun getCount(): Int {

            return itemModel.size
        }

    }
}