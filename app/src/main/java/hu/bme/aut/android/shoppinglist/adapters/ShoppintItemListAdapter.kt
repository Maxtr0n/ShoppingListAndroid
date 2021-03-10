package hu.bme.aut.android.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.shoppinglist.models.ShoppingItem
import hu.bme.aut.android.shoppinglist.databinding.ShoppingListItemBinding


class ShoppingItemListAdapter(private val clickListener: ShoppingListItemClickListener, private val longClickListener: ShoppingListItemLongClickListener) : ListAdapter<ShoppingItem, ShoppingItemListAdapter.ViewHolder>(ShoppingListDiffCallback()) {


    class ViewHolder private constructor(val binding: ShoppingListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingItem, clickListener: ShoppingListItemClickListener, longClickListener: ShoppingListItemLongClickListener){
            binding.item = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

            itemView.setOnLongClickListener {
                longClickListener.onLongClick(item)
                return@setOnLongClickListener true
            }

            itemView.setOnClickListener {
                clickListener.onClick(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ShoppingListItemBinding.inflate(layoutInflater, parent, false)

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

    fun getItemAt(position: Int) : ShoppingItem{
        return currentList[position]
    }

}

class ShoppingListDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
    override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem == newItem
    }
}

class ShoppingListItemClickListener(val clickListener: (item: ShoppingItem) -> Unit) {
    fun onClick(item: ShoppingItem) = clickListener(item)
}

class ShoppingListItemLongClickListener(val longClickListener: (item: ShoppingItem) -> Unit) {
    fun onLongClick(item: ShoppingItem) = longClickListener(item)
}

