package com.example.iqrabedlibrary.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey(autoGenerate = true)
    var historyId: Int,
    var ownerId: Int,
    val issueDate: String,
    val bookTitle: String,
    val bookNo: Int,
    var returnDate: String?,
    var status: Int
)