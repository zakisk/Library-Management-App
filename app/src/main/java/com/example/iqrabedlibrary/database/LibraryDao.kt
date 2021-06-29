package com.example.iqrabedlibrary.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    //Candidate Methods
    @Insert
    suspend fun addCandidate(user: User)

    @Delete
    suspend fun deleteCandidate(user: User)

    @Update
    suspend fun updateCandidate(user: User)

    @Query("SELECT * FROM User WHERE type = 'Teacher' ORDER BY priority DESC")
    fun getAllTeachers() : Flow<List<User>>

    @Query("SELECT * FROM User WHERE type = 'Student' ORDER BY priority DESC")
    fun getAllStudents() : Flow<List<User>>

    //History Methods

    @Insert
    suspend fun insertHistory(history: History)

    @Update
    suspend fun updateHistory(history: History)

    @Query("DELETE FROM History WHERE ownerId = :userId")
    suspend fun deleteAllHistory(userId: Int)

    @Query("DELETE FROM History WHERE ownerId = :userId AND status = 0")
    suspend fun deleteDoneHistory(userId: Int)

    @Query("SELECT * FROM History WHERE ownerId = :userId")
    fun getAllHistory(userId: Int) : Flow<List<History>>

    @Query("SELECT * FROM History WHERE ownerId = :userId")
    suspend fun getAllHistoryInList(userId: Int) : List<History>

    //Search Methods

    @Query("SELECT * FROM User WHERE type = 'Teacher' AND name LIKE :query ORDER BY priority DESC")
    fun searchTeachers(query: String): Flow<List<User>>

    @Query("SELECT * FROM User WHERE type = 'Student' AND name LIKE :query ORDER BY priority DESC")
    fun searchStudent(query: String): Flow<List<User>>

}