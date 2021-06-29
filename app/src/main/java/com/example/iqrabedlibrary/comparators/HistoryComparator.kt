package com.example.iqrabedlibrary.comparators

import androidx.recyclerview.widget.DiffUtil
import com.example.iqrabedlibrary.database.History

class HistoryComparator : DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem.historyId == newItem.historyId ||
               oldItem.ownerId == newItem.ownerId ||
               oldItem.issueDate == newItem.issueDate ||
               oldItem.bookTitle == newItem.bookTitle ||
               oldItem.bookNo == newItem.bookNo ||
               oldItem.returnDate == newItem.returnDate ||
               oldItem.status == newItem.status
    }
}