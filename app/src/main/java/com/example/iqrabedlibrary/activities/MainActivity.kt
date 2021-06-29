package com.example.iqrabedlibrary.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.example.iqrabedlibrary.BaseApplication
import com.example.iqrabedlibrary.R
import com.example.iqrabedlibrary.adapters.SectionsPagerAdapter
import com.example.iqrabedlibrary.database.Constants
import com.example.iqrabedlibrary.database.ExportToExcel
import com.example.iqrabedlibrary.database.User
import com.example.iqrabedlibrary.viewModel.UserViewModel
import com.example.iqrabedlibrary.viewModel.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

        private var PAGE_NO = 0

        private val ALL_PERMISSIONS = 1

        private lateinit var teachersList: List<User>

        private lateinit var studentsList: List<User>

        private lateinit var exportToExcel: ExportToExcel


    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((applicationContext as BaseApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        window.statusBarColor = resources.getColor(R.color.colorPrimary, null)


        view_pager.addOnPageChangeListener(this)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)



        userViewModel.allTeachers.observe(this) {
            teachersList = it
        }

        userViewModel.allStudents.observe(this) {
            studentsList = it
        }

        fab.setOnClickListener {
            (applicationContext as BaseApplication).selected = null
            startActivity(Intent(this, FormActivity::class.java).apply {
                putExtra(Constants.ARGUMENT, PAGE_NO)
            })
        }

        

        if (!checkPermissions(Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            askPermission()
        }


    }



    private fun checkPermissions(vararg permissions: String) : Boolean = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun checkWritePermission() : Boolean {
       return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                                    PackageManager.PERMISSION_GRANTED
    }



    // --------------------ViewPager.OnPageChangeListener Methods-----------------------------------

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

    override fun onPageSelected(position: Int) { PAGE_NO = position }

    override fun onPageScrollStateChanged(state: Int) { }

    // ---------------------------------------------------------------------------------------------




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.export) {
                write()
        }
        return true
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode) {

            requestCode -> {

                for (i in grantResults.indices) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "${permissions[i]} is Granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }



    private fun write() {

        if (checkWritePermission()) {
            if (!userViewModel.isExporting) {

                userViewModel.isExporting = true
                exportToExcel = ExportToExcel(
                    this,
                    userViewModel,
                    teachersList,
                    studentsList,
                )

                exportToExcel.export()
            } else {
                showAlertDialog("Exporting is Already Ongoing Please Wait Until it's Done.", 1)
            }

        } else {

            showAlertDialog("Please Grant Write Storage Permission to get Data in Excel", 0)

        }
    }


    override fun onDestroy() {
        exportToExcel.cancel()
        exportToExcel.updateNotification(0, 0)
        super.onDestroy()
    }



    private fun askPermission() {
        val permissions = arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)

            requestPermissions(permissions, ALL_PERMISSIONS)
    }

    private fun showAlertDialog(msg: String, action: Int) {
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme).apply {
            setTitle("Alert")

            setMessage(msg)

            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.dialog_bg, null)
            background = drawable

            if (action == 0) {
                setNegativeButton("No", null)
                setPositiveButton("Grant") {_, _ ->
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), ALL_PERMISSIONS)
                }
            } else {
                setPositiveButton("OK", null)
            }
            show()
        }
    }
}