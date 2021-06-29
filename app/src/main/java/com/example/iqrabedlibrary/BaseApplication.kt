package com.example.iqrabedlibrary

import android.app.Application
import com.example.iqrabedlibrary.database.User
import com.example.iqrabedlibrary.database.UserDatabase
import com.example.iqrabedlibrary.database.Repository

class BaseApplication : Application() {

    val database by lazy { UserDatabase.getDatabase(this) }
    val repository by lazy { Repository(database.getDao()) }
    var selected: User? = null
}