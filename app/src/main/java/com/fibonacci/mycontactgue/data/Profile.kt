package com.fibonacci.mycontactgue.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile(
    @PrimaryKey val id: Int = 1, // Only one profile allowed
    val name: String,
    val phoneNumber: String,
    val email: String,
    val birthday: String,
    val photoUri: String? = null
)
