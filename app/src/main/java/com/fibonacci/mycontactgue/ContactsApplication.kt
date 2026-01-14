package com.fibonacci.mycontactgue

import android.app.Application
import com.fibonacci.mycontactgue.data.ContactDatabase
import com.fibonacci.mycontactgue.data.ContactRepository

class ContactsApplication : Application() {
    private val database by lazy { ContactDatabase.getDatabase(this) }
    val repository by lazy { ContactRepository(database.contactDao(), database.callLogDao(), database.profileDao()) }
}