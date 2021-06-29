package com.example.iqrabedlibrary.database

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(private val libraryDao: LibraryDao) {

    val allTeachers: Flow<List<User>> = libraryDao.getAllTeachers()

    val allStudents: Flow<List<User>> = libraryDao.getAllStudents()


    @WorkerThread
    suspend fun insertUser(user: User) {
        libraryDao.addCandidate(user)
    }


    @WorkerThread
    suspend fun updateUser(user: User) {
        libraryDao.updateCandidate(user)
    }


    @WorkerThread
    suspend fun deleteUser(user: User) {
        libraryDao.deleteCandidate(user)
        libraryDao.deleteAllHistory(user.id)
    }


    @WorkerThread
    fun searchTeachers(query: String)  : Flow<List<User>>{
        return libraryDao.searchTeachers(query)
    }


    @WorkerThread
    fun searchStudents(query: String)  : Flow<List<User>>{
        return libraryDao.searchStudent(query)
    }



    //History Methods



    @WorkerThread
    suspend fun insertHistory(history: History) {
        libraryDao.insertHistory(history)
    }



    @WorkerThread
    suspend fun updateHistory(history: History) {
        libraryDao.updateHistory(history)
    }



    @WorkerThread
    fun getAllHistory(userId: Int) : Flow<List<History>> {
        return libraryDao.getAllHistory(userId)
    }



    @WorkerThread
    suspend fun getAllHistoryInList(userId: Int) : List<History> {
        return libraryDao.getAllHistoryInList(userId)
    }


    @WorkerThread
    suspend fun deleteDoneHistory(userId: Int) {
        libraryDao.deleteDoneHistory(userId)
    }
}
