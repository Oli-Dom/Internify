package com.example.internify.model

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "internships")
data class Internship(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val location: String,
    val status: String
)
