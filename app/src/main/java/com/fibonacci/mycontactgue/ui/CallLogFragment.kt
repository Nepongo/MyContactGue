package com.fibonacci.mycontactgue.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.databinding.FragmentCallLogBinding

class CallLogFragment : Fragment() {

    private var _binding: FragmentCallLogBinding? = null
    private val binding get() = _binding!!

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }

    private lateinit var callLogAdapter: CallLogAdapter
    private var currentContacts: List<Contact> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        callLogAdapter = CallLogAdapter(emptyList())
        binding.rvCallLogs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = callLogAdapter
        }

        // Observe contacts first to have the latest mapping
        contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            currentContacts = contacts ?: emptyList()
            // Re-trigger update if we already have call logs
            contactViewModel.allCallLogs.value?.let {
                callLogAdapter.updateList(it, currentContacts)
            }
        }

        // Observe call logs and sync with current contacts
        contactViewModel.allCallLogs.observe(viewLifecycleOwner) { callLogs ->
            callLogs?.let {
                callLogAdapter.updateList(it, currentContacts)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}