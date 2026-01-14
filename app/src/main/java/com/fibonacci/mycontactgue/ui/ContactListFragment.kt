package com.fibonacci.mycontactgue.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.R
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }
    
    private lateinit var contactAdapter: ContactAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            syncSystemContacts()
        } else {
            Toast.makeText(requireContext(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        
        contactAdapter = ContactAdapter(emptyList())

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactAdapter
        }

        contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            contacts?.let { 
                contactAdapter.updateList(it)
                if (it.isEmpty()) {
                    checkAndSyncContacts()
                }
            }
        }
    }

    private fun checkAndSyncContacts() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            syncSystemContacts()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun syncSystemContacts() {
        val systemContacts = mutableMapOf<String, Contact>()
        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "No Name"
                val rawNumber = it.getString(numberIndex) ?: ""
                
                // Normalize number to use as a unique key (removes spaces, dashes, etc.)
                val normalizedNumber = rawNumber.replace("[^0-9+]".toRegex(), "")
                
                if (normalizedNumber.isNotEmpty() && !systemContacts.containsKey(normalizedNumber)) {
                    val photoUri = it.getString(photoIndex)
                    systemContacts[normalizedNumber] = Contact(
                        name = name, 
                        phoneNumber = rawNumber, 
                        email = "", 
                        birthday = "", 
                        photoUri = photoUri
                    )
                }
            }
        }

        if (systemContacts.isNotEmpty()) {
            val uniqueContacts = systemContacts.values.toList()
            uniqueContacts.forEach { contactViewModel.insertContact(it) }
            Toast.makeText(context, "Synced ${uniqueContacts.size} unique contacts from phone", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_list_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}