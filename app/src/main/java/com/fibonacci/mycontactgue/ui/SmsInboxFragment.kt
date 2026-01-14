package com.fibonacci.mycontactgue.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.databinding.FragmentSmsInboxBinding
import java.text.SimpleDateFormat
import java.util.*

class SmsInboxFragment : Fragment() {

    private var _binding: FragmentSmsInboxBinding? = null
    private val binding get() = _binding!!

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }

    private var contactsList: List<Contact> = emptyList()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadSmsMessages()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmsInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        
        contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            contactsList = contacts
            checkPermissionAndLoadSms()
        }
    }

    private fun checkPermissionAndLoadSms() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            loadSmsMessages()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
        }
    }

    private fun loadSmsMessages() {
        val smsList = mutableListOf<SmsMessage>()
        val cursor: Cursor? = requireContext().contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            null,
            null,
            Telephony.Sms.DATE + " DESC"
        )

        cursor?.use {
            val indexAddress = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val indexBody = it.getColumnIndex(Telephony.Sms.BODY)
            val indexDate = it.getColumnIndex(Telephony.Sms.DATE)

            val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

            val seenNumbers = mutableSetOf<String>()
            while (it.moveToNext() && seenNumbers.size < 50) {
                val address = it.getString(indexAddress) ?: "Unknown"
                if (address !in seenNumbers) {
                    val body = it.getString(indexBody) ?: ""
                    val dateMillis = it.getLong(indexDate)
                    val date = dateFormat.format(Date(dateMillis))
                    
                    // Fixed logic: Resolve contact name only for numeric addresses
                    val isNumeric = address.any { char -> char.isDigit() }
                    val contactName = if (isNumeric) resolveContactName(address) else address

                    smsList.add(SmsMessage(contactName, body, date, address))
                    seenNumbers.add(address)
                }
            }
        }

        val smsAdapter = SmsAdapter(smsList) { message ->
            val action = SmsInboxFragmentDirections.actionSmsInboxFragmentToChatFragment(message.phoneNumber ?: message.sender)
            findNavController().navigate(action)
        }
        
        binding.rvSms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = smsAdapter
        }
    }

    private fun resolveContactName(phoneNumber: String): String {
        val normalizedInput = phoneNumber.replace("[^0-9]".toRegex(), "")
        if (normalizedInput.isEmpty()) return phoneNumber

        val contact = contactsList.find { 
            val normalizedContact = it.phoneNumber.replace("[^0-9]".toRegex(), "")
            normalizedContact.isNotEmpty() && (normalizedContact.endsWith(normalizedInput) || normalizedInput.endsWith(normalizedContact))
        }
        
        return contact?.name ?: phoneNumber
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}