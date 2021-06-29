package com.example.iqrabedlibrary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.database.Constants
import com.example.iqrabedlibrary.database.History
import com.example.iqrabedlibrary.comparators.HistoryComparator
import kotlinx.android.synthetic.main.history_item_layout.view.*

class HistoryAdapter(private val onReturn: (History) -> Unit) :
    ListAdapter<History, HistoryAdapter.HistoryViewHolder>(HistoryComparator()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item_layout, parent, false)
        return HistoryViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.return_button.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                onReturn(current)
            }
        }
    }

    class HistoryViewHolder( itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {



        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(history: History) {

            if (history.status == Constants.RETURNED) {
                itemView.status_image.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_done, null))
                itemView.return_date.text = history.returnDate
                itemView.return_button.visibility = View.GONE
            } else {
                itemView.status_image.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_pending, null))
                itemView.return_date.text = Constants.NOT_RETURNED
                itemView.return_button.visibility = View.VISIBLE
            }

            itemView.issue_date.text = history.issueDate
            itemView.book_title.text = history.bookTitle
            itemView.book_no.text = history.bookNo.toString()
        }
    }
}