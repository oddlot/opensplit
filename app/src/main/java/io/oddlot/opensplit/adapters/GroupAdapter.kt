package io.oddlot.opensplit.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.oddlot.opensplit.R
import io.oddlot.opensplit.models.Group
import kotlinx.android.synthetic.main.group_card.view.*

class GroupAdapter(
    private val groups: List<Group>
) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]

        holder.itemView.groupName.text = group.name
    }

    override fun getItemCount(): Int = groups.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}