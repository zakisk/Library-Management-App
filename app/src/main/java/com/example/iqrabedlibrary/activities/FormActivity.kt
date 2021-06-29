package com.example.iqrabedlibrary.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.iqrabedlibrary.BaseApplication
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.database.Constants
import com.example.iqrabedlibrary.database.User
import com.example.iqrabedlibrary.viewModel.UserViewModel
import com.example.iqrabedlibrary.viewModel.UserViewModelFactory
import kotlinx.android.synthetic.main.activity_form.*
import kotlin.properties.Delegates

class FormActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as BaseApplication).repository)
        }

    private lateinit var type: String
    private var priority by Delegates.notNull<Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        window.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        batch_spinner.onItemSelectedListener = this

        val selected = (applicationContext as BaseApplication).selected

        when(intent.getIntExtra(Constants.ARGUMENT, -1)) {

            Constants.ADD_NEW_TEACHER -> {
                type = Constants.TEACHER
                textView.text = "Add Teacher"
                roll_layout.visibility = View.GONE
            }

            Constants.ADD_NEW_STUDENT -> {
                type = Constants.STUDENT
                textView.text = "Add Student"
                roll_layout.visibility = View.VISIBLE
            }

            Constants.UPDATE -> {
                type = selected!!.type
                textView.text = "Update ${selected.name}"
                roll_layout.visibility = if (selected.type == Constants.TEACHER) View.GONE else View.VISIBLE
            }

            else -> {
                type = Constants.TEACHER
                textView.text = "Add Teacher"
                roll_layout.visibility = View.GONE
            }
        }

        setSpinnerAdapter()

        if (selected == null) {
            add_button.text = getString(R.string.add)
            priority = 0
        } else {
            add_button.text = getString(R.string.update)
            fillEditText(selected)
        }

        add_button.setOnClickListener {

                if (validate()) {
                    val user = User(
                        selected?.id ?: 0,
                        editText_name!!.text.toString(),
                        if (type == Constants.STUDENT) editText_roll_number!!.text.toString()
                            .toInt() else 0,
                        editText_mobile!!.text.toString().toLong(),
                        batch_spinner.selectedItem.toString(),
                        type,
                        priority
                    )
                    if (selected == null) {
                        userViewModel.insertUser(user)
                        Toast.makeText(
                            this,
                            "${user.name} is Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        userViewModel.updateUser(user)
                        (applicationContext as BaseApplication).selected = user
                        Toast.makeText(
                            this,
                            "${user.name} is Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        (applicationContext as BaseApplication).selected = user
                    }
                    finish()
                }
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun setSpinnerAdapter() {
        ArrayAdapter.createFromResource(
            this,
            R.array.batch_year_list,
            android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            batch_spinner.adapter = it
        }
    }

    private fun fillEditText(user: User) {
        editText_name.setText(user.name)
        editText_mobile.setText(user.mobileNo.toString())

        if (type == Constants.STUDENT) {
            editText_roll_number.setText(user.rollNo.toString())
        }

        resources.getStringArray(R.array.batch_year_list).forEachIndexed { i, str ->
            if (str == user.batchYear) {
                batch_spinner.setSelection(i)
            }
        }
        priority = user.priority
    }

    private fun validate() : Boolean {
        var valid = true
        if (editText_name.text!!.isEmpty()) {
            editText_name.apply {
                error = "Please Enter Name"
                requestFocus()
            }
            valid = false
        }

        if (type == Constants.STUDENT) {
            if (editText_roll_number.text!!.isEmpty()) {
                editText_roll_number.apply {
                    error = "Please Enter Roll Number"
                    requestFocus()
                }
                valid = false
            }
        }


        if (editText_mobile.text!!.isEmpty()) {
            editText_mobile.apply {
                error = "Please Enter Mobile Number"
                requestFocus()
            }
            valid = false
        }


        return valid
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        (parent?.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.colorPrimary))
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { }
}