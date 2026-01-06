package com.fibonacci.mycontactgue.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CallLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLog)

    @Query("SELECT * FROM call_log_table ORDER BY timestamp DESC")
    fun getAllCallLogs(): LiveData<List<CallLog>>

}
