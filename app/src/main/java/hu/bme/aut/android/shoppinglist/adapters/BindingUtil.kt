package hu.bme.aut.android.shoppinglist.adapters

import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import hu.bme.aut.android.shoppinglist.database.ShoppingItem
import hu.bme.aut.android.shoppinglist.database.ShoppingList


@BindingAdapter("ItemName")
fun TextView.setItemName(item: ShoppingItem?){
    item?.let{
        text = item.name
    }
}

@BindingAdapter("ItemAcquired")
fun CheckBox.setItemAcquired(item: ShoppingItem?){
    item?.let {
        isChecked = item.acquired
    }
}

@BindingAdapter("ItemName")
fun TextView.setItemName(list: ShoppingList?){
    list?.let{
        text = list.name
    }
}