package com.faste.foodwastemanageapp.recipes.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.model.RecipeModel
import com.squareup.picasso.Picasso

class RecipeRVAdapter (

    private var recipeList: ArrayList<RecipeModel>,

    ) : RecyclerView.Adapter<RecipeRVAdapter.RecipeViewHolder>() {

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
            R.layout.recipe_rv_item,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return RecipeViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        holder.courseNameTV.text = recipeList.get(position).name
        //holder.courseIV.setImageResource(recipeList.get(position).img)
        val url = recipeList.get(position).img
        Picasso.get().load(url).into(holder.courseIV);
    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our size of our list
        return recipeList.size
    }

    class RecipeViewHolder(itemView: View , listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val courseNameTV: TextView = itemView.findViewById(R.id.idTVCourse)
        val courseIV: ImageView = itemView.findViewById(R.id.IVRecipe)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun setFilteredList(mList: ArrayList<RecipeModel>){
        this.recipeList = mList
        notifyDataSetChanged()
    }
}


