package com.example.iqrabedlibrary.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.database.User
import com.example.iqrabedlibrary.comparators.UserComparator
import kotlinx.android.synthetic.main.student_layout.view.*

class UserAdapter(private val resId: Int, private val onClick: (User) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<User, UserAdapter.ViewHolder>(UserComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), resId)
    }

    class ViewHolder(itemView: View, private val onClick: (User) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private var user: User? = null

        init {
            itemView.setOnClickListener {
                onClick(user!!)
            }
        }

            fun bind(user: User, type: Int) {
                this.user = user

                itemView.name.text = user.name
                itemView.batch_year.text = user.batchYear
                if (type == R.layout.teacher_layout) {
                    itemView.book_no_text.text = user.mobileNo.toString()
                } else {
                    itemView.book_no_text.text = user.rollNo.toString()
                }
            }
    }

}