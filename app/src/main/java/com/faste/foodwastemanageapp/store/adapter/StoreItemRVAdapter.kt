package com.faste.foodwastemanageapp.store.adapter




import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.faste.foodwastemanageapp.R
import com.faste.foodwastemanageapp.recipes.model.RecipeModel
import com.faste.foodwastemanageapp.store.model.StoreItemModel
import com.squareup.picasso.Picasso

class StoreItemRVAdapter (

    private var itemList: ArrayList<StoreItemModel>,


    ) : RecyclerView.Adapter<StoreItemRVAdapter.ItemViewHolder>() {

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
    ): ItemViewHolder {
        // this method is use to inflate the layout file
        // which we have created for our recycler view.
        // on below line we are inflating our layout file.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.store_rv_item,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return ItemViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // on below line we are setting data to our text view and our image view.
        val price = "LKR " + itemList.get(position).price + ".00"
        val location = "Location: " + itemList.get(position).location
        holder.itemName.text = itemList.get(position).name
        holder.itemPrice.text = price
        holder.itemLocation.text = location
        val url = itemList.get(position).img
        Picasso.get().load(url).into(holder.itemImageView);
    }

    override fun getItemCount(): Int {
        // on below line we are
        // returning our size of our list
        return itemList.size
    }

    class ItemViewHolder(itemView: View , listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val itemName: TextView = itemView.findViewById(R.id.storeItemName)
        val itemImageView: ImageView = itemView.findViewById(R.id.storeCategoryImg)
        val itemPrice:TextView = itemView.findViewById(R.id.storeCategoryName)
        val itemLocation:TextView = itemView.findViewById(R.id.storeItemLocation)

        init {

            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun setFilteredList(mList: ArrayList<StoreItemModel>){
        this.itemList = mList
        notifyDataSetChanged()
    }
}


