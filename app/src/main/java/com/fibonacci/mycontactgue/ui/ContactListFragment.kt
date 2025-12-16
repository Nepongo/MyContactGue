package com.fibonacci.mycontactgue.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.R
import com.fibonacci.mycontactgue.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }
    
    private lateinit var contactAdapter: ContactAdapter

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
        
        // Initialize adapter with an empty list
        contactAdapter = ContactAdapter(emptyList())

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = contactAdapter
        }

        // THIS IS THE KEY: Observe changes from the database
        contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            // When data changes, update the adapter
            contacts?.let { 
                contactAdapter.updateList(it)
            }
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
                if (newText.isNullOrEmpty()) {
                    contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
                        contactAdapter.updateList(contacts)
                    }
                } else {
                    contactViewModel.searchContacts(newText)
                    contactViewModel.searchResults.observe(viewLifecycleOwner) { results ->
                        contactAdapter.updateList(results)
                    }
                }
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
