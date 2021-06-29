package com.example.iqrabedlibrary.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iqrabedlibrary.BaseApplication
import com.example.iqrabedlibrary.activities.ProfileActivity
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.database.History
import com.example.iqrabedlibrary.database.User
import com.example.iqrabedlibrary.adapters.UserAdapter
import com.example.iqrabedlibrary.viewModel.UserViewModel
import com.example.iqrabedlibrary.viewModel.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_students.*

class StudentsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val factory = UserViewModelFactory((context!!.applicationContext as BaseApplication).repository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view_students.visibility = View.GONE

        Handler().postDelayed({
            progress_student.visibility = View.GONE
            recycler_view_students.visibility = View.VISIBLE
        }, 800)

        recycler_view_students.layoutManager = LinearLayoutManager(context)

        adapter = UserAdapter(R.layout.student_layout) { user ->
            onClick(user)
        }
        recycler_view_students.adapter = adapter

        userViewModel.allStudents.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            if (list.isEmpty()) {
                progress_student.visibility = View.GONE
                nothing_layout.visibility = View.VISIBLE
            } else {
                nothing_layout.visibility = View.GONE
            }
        }

    }


    private fun onClick(user: User) {
        openProfile(user)
    }

    private fun openProfile(user: User) {
        (context!!.applicationContext as BaseApplication).selected = user
        startActivity(Intent(context, ProfileActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(this)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchDatabase(newText.orEmpty())
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun searchDatabase(query: String) {
        val searchedQuery = "%$query%"
        userViewModel.getSearchedStudents(searchedQuery).observe(viewLifecycleOwner, { list ->
            adapter.submitList(list)
            if (list.isEmpty()) {
                nothing_layout.visibility = View.VISIBLE
            } else {
                nothing_layout.visibility = View.GONE
            }
        })
    }
}