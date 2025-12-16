package com.fibonacci.mycontactgue.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.data.ContactRepository
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    val allContacts: LiveData<List<Contact>> = repository.allContacts
    
    private val _searchResults = MutableLiveData<List<Contact>>()
    val searchResults: LiveData<List<Contact>> = _searchResults

    fun insertContact(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        repository.update(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)
    }
    
    fun searchContacts(query: String) {
        val searchQuery = "%$query%"
        repository.searchDatabase(searchQuery).observeForever { contacts ->
            _searchResults.value = contacts
        }
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
