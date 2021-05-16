package hu.bme.aut.android.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.shoppinglist.models.ShoppingList
import hu.bme.aut.android.shoppinglist.databinding.MyListsItemBinding

class MyListsAdapter (private val clickListener: MyListsClickListener, private val longClickListener: MyListsLongClickListener) : ListAdapter<ShoppingList, MyListsAdapter.ViewHolder>(MyListsDiffCallback()) {

    class ViewHolder private constructor(val binding: MyListsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(list: ShoppingList, clickListener: MyListsClickListener, longClickListener: MyListsLongClickListener){
            binding.list = list
            binding.clickListener = clickListener
            binding.executePendingBindings()

            itemView.setOnClickListener {
                clickListener.onClick(list)
            }

            itemView.setOnLongClickListener {
                longClickListener.onLongClick(list)
                return@setOnLongClickListener true
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MyListsItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, clickListener, longClickListener)
    }

    fun getItemAt(position: Int) : ShoppingList {
        return currentList[position]
    }

}

class MyListsDiffCallback : DiffUtil.ItemCallback<ShoppingList>() {
    override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
        return oldItem == newItem
    }
}

class MyListsClickListener(val clickListener: (list: ShoppingList) -> Unit) {
    fun onClick(list: ShoppingList) = clickListener(list)
}

class MyListsLongClickListener(val longClickListener: (list: ShoppingList) -> Unit) {
    fun onLongClick(list: ShoppingList) = longClickListener(list)
}


