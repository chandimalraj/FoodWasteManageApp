package com.faste.foodwastemanageapp.recipes.recipes


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.adapter.RecipeRVAdapter
import com.faste.foodwastemanageapp.recipes.add.RecipeAddActivity
import com.faste.foodwastemanageapp.recipes.model.RecipeModel
import com.faste.foodwastemanageapp.recipes.recipe.RecipeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RecipesActivity : AppCompatActivity() {

    // on below line we are creating variables
    // for our swipe to refresh layout,
    // recycler view, adapter and list.
    lateinit var recipeRV: RecyclerView
    lateinit var recipeRVAdapter: RecipeRVAdapter
    lateinit var recipeList: ArrayList<RecipeModel>
    private lateinit var  database: DatabaseReference
    private lateinit var progressBar: ProgressBar
    private  val TAG = "MyActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        val addRecipeBtn = findViewById<Button>(R.id.recipeAddBtn)
        val viewRecipeBtn = findViewById<Button>(R.id.recipeViewBtn)
        val searchView = findViewById<SearchView>(R.id.recipesSearchView)

        val bundle :Bundle? = intent.extras
        val uid = bundle!!.getString("uid")

        progressBar = findViewById<ProgressBar>(R.id.recipesProgress)
        progressBar.visibility = View.VISIBLE

        //Create an instance of the FirebaseAuth class:
        val mAuth = FirebaseAuth.getInstance()
        //Create a reference to the database location

        // on below line we are initializing our list
        recipeList = ArrayList()
        // our views with their ids.
        recipeRV = findViewById(R.id.idRVCourses)

        // on below line we are creating a variable
        // for our grid layout manager and specifying
        // column count as 2
        val layoutManager = GridLayoutManager(this, 2)

        recipeRV.layoutManager = layoutManager
        getRecipeData()

        // on below line we are initializing our adapter
        recipeRVAdapter = RecipeRVAdapter(recipeList)

        // on below line we are setting
        // adapter to our recycler view.
        recipeRV.adapter = recipeRVAdapter



        viewRecipeBtn.setOnClickListener {
            val intent = Intent( this, RecipesViewActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)
        }

        addRecipeBtn.setOnClickListener {
            val intent = Intent( this, RecipeAddActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })



    }



    private fun filterList(query: String?) {

        if (query != null) {
            val filteredList = ArrayList<RecipeModel>()
            for (i in recipeList) {
                if (i.name.lowercase(Locale.ROOT).startsWith(query.lowercase(Locale.ROOT))) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                recipeRVAdapter.setFilteredList(filteredList)
            }
        }
    }




    private fun getRecipeData(){

        database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Recipes")

        database.addValueEventListener(
            object : ValueEventListener {


                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle data change
                    val values = dataSnapshot.value as HashMap<*, *>?
                   recipeList.clear()
                    dataSnapshot.children.forEach { child ->
                           val key = child.key // get the key of the current child
                            val value = child.value as HashMap<*, *>
                            val name = value["name"] as? String
                            val img = value["imageUrl"] as? String
                            val ingredients = value["ingredients"] as? String
                            val time = value["time"] as? String
                            val shelfLife = value["shelfLife"] as? String
                            val directions = value["directions"] as? String

                                recipeList.add(
                                    RecipeModel(
                                        key.toString(),
                                        name.toString(), time.toString(),
                                        shelfLife.toString(),
                                        ingredients.toString(),
                                        directions.toString(),
                                        img.toString()
                                    )
                                )

                    }

                    recipeRVAdapter.notifyDataSetChanged()

                    recipeRVAdapter.setOnItemClickListener(object : RecipeRVAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                           //Toast.makeText(this@RecipesActivity,"you clicked on $position" , Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RecipesActivity, RecipeActivity::class.java)

                            intent.putExtra("id",recipeList[position].id)
                            intent.putExtra("name",recipeList[position].name)
                            intent.putExtra("time",recipeList[position].time)
                            intent.putExtra("shelfLife",recipeList[position].shelfLife)
                            intent.putExtra("ingredients",recipeList[position].ingredients)
                            intent.putExtra("directions",recipeList[position].directions)
                            intent.putExtra("image",recipeList[position].img)
                            startActivity(intent)
                        }

                    })

                    // Hide ProgressBar when data is loaded
                    progressBar.visibility = View.GONE

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancelled event
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            }
        )

//        recipeRVAdapter.setOnItemClickListener(object : RecipeRVAdapter.onItemClickListener {
//                        override fun onItemClick(position: Int) {
//                           //Toast.makeText(this@RecipesActivity,"you clicked on $position" , Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this@RecipesActivity, RecipeActivity::class.java)
//
//                            intent.putExtra("id",recipeList[position].id)
//                            intent.putExtra("name",recipeList[position].name)
//                            intent.putExtra("time",recipeList[position].time)
//                            intent.putExtra("ingredients",recipeList[position].ingredients)
//                            intent.putExtra("image",recipeList[position].img)
//                            startActivity(intent)
//                        }
//
//                    })



    }
}