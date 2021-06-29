package com.example.iqrabedlibrary.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.database.Constants
import com.example.iqrabedlibrary.database.History
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DateFormat
import java.util.*


class IssueBookDialog(inflater: LayoutInflater, private val name: String
    ,private val userId: Int, val onIssue: (History) -> Unit)
                                : DialogFragment() {

    private lateinit var bookTitle: String
    private lateinit var bookNo: String
    private lateinit var ctx: Context

    @SuppressLint("InflateParams")
    val layout: View = inflater.inflate(R.layout.issue_book_dialog_layout, null)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("AlertDialog", "onCreateDialog() Method")

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            builder.apply {
                setView(layout)
                setTitle("Issue to $name")
                setNegativeButton("Cancel", null)
                setPositiveButton("Issue") { _, _ ->
                    val history = history()
                    if (bookTitle.isEmpty() || bookNo.isEmpty()) {
                        showErrorDialog()
                    } else {
                        onIssue(history)
                    }

                }
            }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        ctx = context
        super.onAttach(context)
    }

    private fun history() : History {
        val date = getDate()
        bookTitle = layout.findViewById<EditText>(R.id.editText_book_title).text.toString().trim()
        bookNo = layout.findViewById<EditText>(R.id.editText_book_no).text.toString()

            return History(
                0,
                userId,
                date,
                bookTitle,
                if (bookNo.isNotEmpty()) bookNo.toInt() else 0,
                null,
                Constants.ISSUED
            )
    }

    private fun getDate() : String {
        val date = Date()
        return DateFormat.getInstance().format(date)
    }

    private fun showErrorDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).apply {
            setTitle("Wrong Input")
            val background = ResourcesCompat.getDrawable(ctx.resources, R.drawable.dialog_bg, null)
            setBackground(background)
            setMessage("The Book isn't Issued because Book Title or Book Number is Empty")
            setPositiveButton("OK", null)
            show()
        }
    }
}