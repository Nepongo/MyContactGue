package com.fibonacci.mycontactgue.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_log_table")
data class CallLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val contactName: String?,
    val phoneNumber: String,
    val callType: String, // e.g., "OUTGOING", "INCOMING", "MISSED"
    val timestamp: Long,    // The date and time of the call
    val duration: Long      // Call duration in seconds
)
