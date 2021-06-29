package com.example.iqrabedlibrary.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val name: String,
    val rollNo: Int,
    val mobileNo: Long,
    val batchYear: String,
    val type: String,
    var priority: Int
)
