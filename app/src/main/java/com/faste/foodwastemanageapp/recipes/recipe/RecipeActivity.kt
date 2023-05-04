package com.faste.foodwastemanageapp.recipes.recipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.faste.foodwastemanageapp.R
import com.squareup.picasso.Picasso

class RecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val heading = findViewById<TextView>(R.id.recipeHeadingTxt)
        val img =findViewById<ImageView>(R.id.recipeImg)
        val timeTxt = findViewById<TextView>(R.id.recipeTimeTxt)
        val bundle :Bundle?=intent.extras

        val id = bundle!!.getString("key")
        val headingValue = bundle!!.getString("name")
        val imgUrl = bundle!!.getString("image")
        val time =  bundle!!.getString("time")
        val ingredients =  bundle!!.getString("ingredients")

        println(headingValue)
        Picasso.get().load(imgUrl).into(img)
        heading.text = headingValue
        timeTxt.text = time
    }
}