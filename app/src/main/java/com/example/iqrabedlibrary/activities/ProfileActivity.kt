package com.example.iqrabedlibrary.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iqrabedlibrary.BaseApplication
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.adapters.HistoryAdapter
import com.example.iqrabedlibrary.database.Constants
import com.example.iqrabedlibrary.database.History
import com.example.iqrabedlibrary.database.User
import com.example.iqrabedlibrary.fragments.IssueBookDialog
import com.example.iqrabedlibrary.viewModel.UserViewModel
import com.example.iqrabedlibrary.viewModel.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class ProfileActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((applicationContext as BaseApplication).repository)
    }

    private lateinit var selected: User

    private lateinit var adapter: HistoryAdapter

    private lateinit var selectedHistory: History

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(profile_toolbar)
        window.statusBarColor = resources.getColor(R.color.colorPrimary, null)


        selected = (applicationContext as BaseApplication).selected!!

        selected_name.text = selected.name

        selected_mobile_no.text = "${selected.mobileNo}  "

        Handler().postDelayed({ profile_progress.visibility = View.GONE }, 500)

        recycler_view_history.layoutManager = LinearLayoutManager(this)

        adapter = HistoryAdapter { history ->
            onReturn(history)
        }

        recycler_view_history.adapter = adapter

        userViewModel.getAllHistory(selected.id).observe(this, { list ->

            adapter.submitList(list)

            if (list.isEmpty()) {
                profile_progress.visibility = View.GONE
                no_history.visibility = View.VISIBLE

            } else {
                no_history.visibility = View.GONE
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        if (no_history.isVisible) {
            menu?.findItem(R.id.clear_all_history)?.isVisible = false
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        Log.d("MainActivity", "onOptionsItemSelected")
        when(item.itemId) {

            R.id.edit_user -> {

                startActivity(Intent(this, FormActivity::class.java).apply {
                putExtra(Constants.ARGUMENT, Constants.UPDATE)
            })

            }

            R.id.delete_user -> showDialog("Do you want to delete ${selected.name} ?", 0)

            R.id.issue_book -> showIssueDialog(selected)

            R.id.clear_all_history -> showDialog("Do you want to clear history ?\n" +
                    "Note: Only returned books entries will be cleared.", 1)

            else -> return super.onOptionsItemSelected(item)

        }
        return true
    }





    private fun showIssueDialog(user: User) {
        val dialog = IssueBookDialog(layoutInflater, user.name, user.id) {
            onIssue(user, it)
        }

        dialog.show(supportFragmentManager, null)
    }




    private fun onIssue(user: User, history: History) {

        userViewModel.insertHistory(user ,history)
        adapter.notifyDataSetChanged()

        val message = """BOOK ISSUE ALERT
   Dear ${user.name} You are Issued book
${history.bookTitle}(${history.bookNo}) on ${history.issueDate}
           Thank You.
        """.trimIndent()

        sendSms(user.mobileNo.toString(), message)
        Toast.makeText(this, "Book Issued Successfully", Toast.LENGTH_SHORT).show()
    }







    private fun showDialog(msg: String, action: Int) {

        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme).apply {
            setTitle("Confirm")
            setMessage(msg)

            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.dialog_bg, null)
            background = drawable

            setNegativeButton("NO", null)
            setPositiveButton("YES") { _, _ ->
                when(action){
                    0 -> {
                        userViewModel.deleteUser(selected)
                        finish()
                    }

                    1 -> userViewModel.deleteDoneHistory(selected.id)

                    2 -> returnBook(selectedHistory)
                }

            }

            show()
        }
    }



    private fun onReturn(history: History) {
        selectedHistory = history
        showDialog("Did ${selected.name} return book : ${history.bookTitle} ?", 2)
    }




    private fun returnBook(history: History) {
        history.status = Constants.RETURNED

        history.returnDate = getDate()

        userViewModel.updateHistory(selected, history)

        adapter.notifyDataSetChanged()

        val message = """BOOK RETURN ALERT
   Dear ${selected.name} 
you have returned book ${history.bookTitle}(${history.bookNo})
on ${history.returnDate}.
            Thank you.
        """.trimMargin()

        sendSms(selected.mobileNo.toString(), message)

        Toast.makeText(this, "Book Returned Successfully", Toast.LENGTH_SHORT).show()
    }



    private fun getDate(): String {

        val date = Date()

        return DateFormat.getInstance().format(date)
    }



    private fun sendSms(phoneNumber: String, message: String) {

        val manager: SmsManager = SmsManager.getDefault()

        manager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    override fun onRestart() {

        selected = (applicationContext as BaseApplication).selected!!

        selected_name.text = selected.name

        selected_mobile_no.text = "${selected.mobileNo}  "

        super.onRestart()
    }

}