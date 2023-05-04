package com.faste.foodwastemanageapp.recipes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.model.RecipeModel
import com.faste.foodwastemanageapp.recipes.update.RecipeUpdateActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso



class RecipeViewRVAdapter (

    private val recipeList: ArrayList<RecipeModel>,


    ) : RecyclerView.Adapter<RecipeViewRVAdapter.RecipeViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recipe_view_rv_item,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return RecipeViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        val recipeName = recipeList.get(position).name
        val imgUrl = recipeList.get(position).img
        val id = recipeList.get(position).id
        val time = recipeList.get(position).time
        val shelf = recipeList.get(position).shelfLife
        val directions = recipeList.get(position).directions
        holder.courseNameTV.text = recipeName
        //holder.courseIV.setImageResource(recipeList.get(position).img)
        val url = recipeList.get(position).img
        Picasso.get().load(url).into(holder.courseIV);

        holder.updateBtn.setOnClickListener {
            val intent = Intent(holder.itemView.context, RecipeUpdateActivity::class.java)
            intent.putExtra("name", recipeName)
            intent.putExtra("img",imgUrl)
            intent.putExtra("id",id)
            intent.putExtra("time",time)
            intent.putExtra("shelfLife",shelf)
            intent.putExtra("directions",directions)
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteBtn.setOnClickListener {
            //Create a reference to the database location
            val database = FirebaseDatabase.getInstance("https://food-waste-manage-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Recipes")
            // Create a reference to the Firebase Storage where you want to store the image
            val storageReference = FirebaseStorage.getInstance().reference.child("images/")
            val objectRef = database.child(id)
            objectRef.removeValue()

        }
    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our size of our list
        return recipeList.size
    }

    class RecipeViewHolder(itemView: View , listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val courseNameTV: TextView = itemView.findViewById(R.id.recipeViewTV)
        val courseIV: ImageView = itemView.findViewById(R.id.recipeViewIV)
        val updateBtn:Button = itemView.findViewById(R.id.recipeUpdateBtn)
        val deleteBtn:Button = itemView.findViewById(R.id.recipeDeleteBtn)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }
}


