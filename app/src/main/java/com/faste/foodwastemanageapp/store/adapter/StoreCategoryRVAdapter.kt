package com.faste.foodwastemanageapp.store.adapter




import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.store.model.StoreCategoryModel

class StoreCategoryRVAdapter (

    private val categoryList: ArrayList<StoreCategoryModel>,


    ) : RecyclerView.Adapter<StoreCategoryRVAdapter.CategoryViewHolder>() {

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
    ): CategoryViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.store_category_rv_item,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return CategoryViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.categoryName.text = categoryList.get(position).name
        holder.categoryImageView.setImageResource(categoryList.get(position).img)
//        val url = categoryList.get(position).img
//        Picasso.get().load(url).into(holder.categoryImageView);
    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our size of our list
        return categoryList.size
    }

    class CategoryViewHolder(itemView: View , listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val categoryName: TextView = itemView.findViewById(R.id.storeCategoryName)
        val categoryImageView: ImageView = itemView.findViewById(R.id.storeCategoryImg)


        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }
}


