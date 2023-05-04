package com.faste.foodwastemanageapp.recipes.recipes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.adapter.RecipeViewRVAdapter
import com.faste.foodwastemanageapp.recipes.model.RecipeModel
import com.faste.foodwastemanageapp.recipes.recipe.RecipeActivity
import com.google.firebase.database.*

class RecipesViewActivity : AppCompatActivity() {

    lateinit var recipeRV: RecyclerView
    lateinit var recipeRVAdapter: RecipeViewRVAdapter
    lateinit var recipeList: ArrayList<RecipeModel>
    private lateinit var  database: DatabaseReference
    private  val TAG = "MyActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_view)

        val bundle :Bundle?=intent.extras
        val uid = bundle!!.getString("uid")

        if (uid != null) {
            getRecipeData(uid)
        }
    }

    private fun getRecipeData(userId:String){

        database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Recipes")

        // on below line we are initializing our list
        recipeList = ArrayList()

        // our views with their ids.
        recipeRV = findViewById(R.id.recipeViewRecycler)
        val update = findViewById<Button>(R.id.recipeUpdateBtn)
        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 1)

        recipeRV.layoutManager = layoutManager

        database.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle data change
                    val values = dataSnapshot.value as HashMap<*, *>?
                    Log.d(TAG, "Value is: $values")

//                    values?.forEach { (_, value) ->
//                        if (value is HashMap<*, *>) {
//                            val uid = value["uid"] as? String
//                            val name = value["name"] as? String
//                            val img = value["imageUrl"] as? String
//                            val ingredients = value["ingredients"] as? String
//                            val time = value["time"] as? String
//                            if (name != null && img != null && uid==userId) {
//
//                                recipeList.add(RecipeRVModal(name, time.toString(),
//                                    ingredients.toString(),img))
//
//                            }
//                        }
//                    }
                    recipeList.clear()
                    dataSnapshot.children.forEach { child ->
                        val key = child.key // get the key of the current child
                        if (child.value is HashMap<*, *>) {
                            val value = child.value as HashMap<*, *>
                            val uid = value["uid"] as? String
                            val name = value["name"] as? String
                            val img = value["imageUrl"] as? String
                            val ingredients = value["ingredients"] as? String
                            val time = value["time"] as? String
                            val shelfLife = value["shelfLife"] as? String
                            val directions = value["directions"] as? String
                            if (name != null && img != null&& uid==userId) {
                                recipeList.add(
                                    RecipeModel(
                                        key.toString(),
                                        name, time.toString(),shelfLife.toString(),
                                        ingredients.toString(),directions.toString(), img
                                    )
                                )
                            }
                        }
                    }
                    // on below line we are initializing our adapter
                    recipeRVAdapter = RecipeViewRVAdapter(recipeList)

                    // on below line we are setting
                    // adapter to our recycler view.
                    recipeRV.adapter = recipeRVAdapter

                    recipeRVAdapter.setOnItemClickListener(object : RecipeViewRVAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            //Toast.makeText(this@RecipesActivity,"you clicked on $position" , Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RecipesViewActivity, RecipeActivity::class.java)
                            intent.putExtra("name",recipeList[position].name)
                            intent.putExtra("image",recipeList[position].img)
                            startActivity(intent)
                        }
                    })

                    recipeRVAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancelled event
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            }
        )

    }
}