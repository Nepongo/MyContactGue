package com.fibonacci.mycontactgue.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class, CallLog::class], version = 2, exportSchema = false) // ADDED CallLog & incremented version
abstract class ContactDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun callLogDao(): CallLogDao // ADDED this line

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null

        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_database"
                )
                .fallbackToDestructiveMigration() // ADDED for version increment
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
