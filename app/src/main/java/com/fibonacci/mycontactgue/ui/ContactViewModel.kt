package com.fibonacci.mycontactgue.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.fibonacci.mycontactgue.data.CallLog
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.data.ContactRepository
import com.fibonacci.mycontactgue.data.Profile
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    // Contact Logic
    private val _searchQuery = MutableLiveData<String>("")

    val allContacts: LiveData<List<Contact>> = _searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            repository.allContacts
        } else {
            repository.searchDatabase("%$query%")
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertContact(contact: Contact) = viewModelScope.launch {
        repository.insertContact(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        repository.updateContact(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        repository.deleteContact(contact)
    }

    // Call Log Logic
    val allCallLogs: LiveData<List<CallLog>> = repository.allCallLogs

    fun addCallLog(callLog: CallLog) = viewModelScope.launch {
        repository.insertCallLog(callLog)
    }

    // Profile Logic
    val profile: LiveData<Profile?> = repository.profile

    fun saveProfile(profile: Profile) = viewModelScope.launch {
        repository.insertProfile(profile)
    }
}

class ContactViewModelFactory(private val repository: ContactRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}