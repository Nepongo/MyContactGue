package com.fibonacci.mycontactgue.data

import androidx.lifecycle.LiveData

class ContactRepository(
    private val contactDao: ContactDao,
    private val callLogDao: CallLogDao,
    private val profileDao: ProfileDao
) {

    // Contact Operations
    val allContacts: LiveData<List<Contact>> = contactDao.getAllContacts()

    suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }
    
    fun searchDatabase(searchQuery: String): LiveData<List<Contact>> {
        return contactDao.searchDatabase(searchQuery)
    }

    // Call Log Operations
    val allCallLogs: LiveData<List<CallLog>> = callLogDao.getAllCallLogs()

    suspend fun insertCallLog(callLog: CallLog) {
        callLogDao.insertCallLog(callLog)
    }

    // Profile Operations
    val profile: LiveData<Profile?> = profileDao.getProfile()

    suspend fun insertProfile(profile: Profile) {
        profileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile)
    }
}