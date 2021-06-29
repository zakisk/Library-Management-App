package com.example.iqrabedlibrary.comparators

import androidx.recyclerview.widget.DiffUtil
import com.example.iqrabedlibrary.database.User

class UserComparator : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id ||
                oldItem.name == newItem.name ||
                oldItem.rollNo == newItem.rollNo ||
                oldItem.batchYear == newItem.batchYear ||
                oldItem.mobileNo == newItem.mobileNo
    }
}