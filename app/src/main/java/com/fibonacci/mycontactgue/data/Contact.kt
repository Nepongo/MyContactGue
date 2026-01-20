package com.fibonacci.mycontactgue.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "contact_table",
    indices = [Index(value = ["phoneNumber"], unique = true)] // Mencegah duplikat nomor telepon
)
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val birthday: String,
    val photoUri: String? = null
) : Parcelable
