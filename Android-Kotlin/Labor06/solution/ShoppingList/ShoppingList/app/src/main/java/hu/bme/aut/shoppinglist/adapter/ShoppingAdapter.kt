package hu.bme.aut.shoppinglist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.shoppinglist.R
import hu.bme.aut.shoppinglist.data.ShoppingItem
import hu.bme.aut.shoppinglist.fragments.NewShoppingItemDialogFragment

class ShoppingAdapter(private val listener: ShoppingItemClickListener) :
    RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>() {

    private val items = mutableListOf<ShoppingItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description
        holder.categoryTextView.text = item.category.name
        holder.priceTextView.text = item.estimatedPrice.toString() + " Ft"
        holder.iconImageView.setImageResource(getImageResource(item.category))
        holder.isBoughtCheckBox.isChecked = item.isBought

        holder.item = item
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ShoppingItemClickListener {
        fun onItemChanged(item: ShoppingItem)
        fun onItemRemoved(item: ShoppingItem)
        fun onItemEdited(item: ShoppingItem)
    }

    @DrawableRes
    private fun getImageResource(category: ShoppingItem.Category) = when (category) {
        ShoppingItem.Category.BOOK -> R.drawable.open_book
        ShoppingItem.Category.ELECTRONIC -> R.drawable.lightning
        ShoppingItem.Category.FOOD -> R.drawable.groceries
    }

    fun addItem(item: ShoppingItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun editItem(item: ShoppingItem) {
        var position = items.indexOf(items.find { it -> it.id == item.id })
        items.removeAt(position)
        items.add(position, item)
        notifyItemChanged(position)
    }

    fun removeAll() {
        items.clear()
        notifyDataSetChanged()
    }

    fun update(shoppingItems: List<ShoppingItem>) {
        items.clear()
        items.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    inner class ShoppingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView
        val nameTextView: TextView
        val descriptionTextView: TextView
        val categoryTextView: TextView
        val priceTextView: TextView
        val isBoughtCheckBox: CheckBox
        val removeButton: ImageButton
        val editButton: ImageButton

        var item: ShoppingItem? = null

        init {
            iconImageView = itemView.findViewById(R.id.ShoppingItemIconImageView)
            nameTextView = itemView.findViewById(R.id.ShoppingItemNameTextView)
            descriptionTextView = itemView.findViewById(R.id.ShoppingItemDescriptionTextView)
            categoryTextView = itemView.findViewById(R.id.ShoppingItemCategoryTextView)
            priceTextView = itemView.findViewById(R.id.ShoppingItemPriceTextView)
            isBoughtCheckBox = itemView.findViewById(R.id.ShoppingItemIsBoughtCheckBox)
            editButton = itemView.findViewById(R.id.ShoppingItemEditButton)
            removeButton = itemView.findViewById(R.id.ShoppingItemRemoveButton)
            isBoughtCheckBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                item?.let {
                    val newItem = it.copy(
                        isBought = isChecked
                    )
                    item = newItem
                    listener.onItemChanged(newItem)
                }
            })
            editButton.setOnClickListener(View.OnClickListener { view ->
                item?.let {
                    val position = items.indexOf(it)
                    listener.onItemEdited(it)
                }
            })
            removeButton.setOnClickListener(View.OnClickListener { view ->
                item?.let {
                    val position = items.indexOf(it)
                    items.remove(it)
                    listener.onItemRemoved(it)
                    notifyItemRemoved(position)
                    Snackbar.make(
                        view, R.string.item_removed_message, BaseTransientBottomBar.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}