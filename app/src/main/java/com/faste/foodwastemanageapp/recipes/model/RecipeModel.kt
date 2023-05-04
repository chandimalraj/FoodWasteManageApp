package com.faste.foodwastemanageapp.recipes.model

data class RecipeModel (
    val id:String,
    val name:String,
    val time:String,
    val shelfLife:String,
    val ingredients:String,
    val directions:String,
    val img:String
)