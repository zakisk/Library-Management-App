package com.example.iqrabedlibrary.viewModel

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.example.iqrabedlibrary.database.Constants
import com.example.iqrabedlibrary.database.History
import com.example.iqrabedlibrary.database.Repository
import com.example.iqrabedlibrary.database.User
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlin.properties.Delegates

class UserViewModel(private val repository: Repository) : ViewModel() {
    // Using LiveData and caching what allTeachers and allStudent returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    var allTeachers: LiveData<List<User>> = repository.allTeachers.asLiveData()

    var allStudents: LiveData<List<User>> = repository.allStudents.asLiveData()

    var isExporting = false

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }


    fun deleteUser(user: User) = viewModelScope.launch {
        repository.deleteUser(user)
    }


    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }


    fun getSearchedTeachers(query: String) : LiveData<List<User>> {
        return repository.searchTeachers(query).asLiveData()
    }


    fun getSearchedStudents(query: String) : LiveData<List<User>> {
        return repository.searchStudents(query).asLiveData()
    }


    //History Methods

    fun getAllHistory(userId: Int) : LiveData<List<History>> {
        return repository.getAllHistory(userId).asLiveData()
    }


    suspend fun getAllHistoryInList(userId: Int) : List<History> {
        return withContext(CoroutineScope(IO).coroutineContext) {
            repository.getAllHistoryInList(userId)
        }
    }


    fun deleteDoneHistory(userId: Int) = viewModelScope.launch {
        repository.deleteDoneHistory(userId)
    }

    fun insertHistory(user: User, history: History) = viewModelScope.launch {
        repository.insertHistory(history)
        user.priority++
        updateUser(user)
    }



    fun updateHistory(user: User, history: History) = viewModelScope.launch {
        repository.updateHistory(history)
        user.priority--
        updateUser(user)
    }
}