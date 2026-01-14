package com.fibonacci.mycontactgue.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(findNavController())
        
        binding.tvChatNumber.text = args.phoneNumber
        resolveAndDisplayName()

        loadConversation()

        binding.btnSendChat.setOnClickListener {
            sendSms()
        }
    }

    private fun resolveAndDisplayName() {
        contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            val normalizedInput = args.phoneNumber.replace("[^0-9]".toRegex(), "")
            
            // Fixed logic: Don't resolve if input is non-numeric or normalization yields empty string
            if (normalizedInput.isEmpty() || !args.phoneNumber.any { it.isDigit() }) {
                binding.tvChatName.text = args.phoneNumber
                return@observe
            }

            val contact = contacts.find {
                val normalizedContact = it.phoneNumber.replace("[^0-9]".toRegex(), "")
                normalizedContact.isNotEmpty() && (normalizedContact.endsWith(normalizedInput) || normalizedInput.endsWith(normalizedContact))
            }
            binding.tvChatName.text = contact?.name ?: args.phoneNumber
        }
    }

    private fun loadConversation() {
        val smsList = mutableListOf<SmsMessage>()
        val selection = "${Telephony.Sms.ADDRESS} = ?"
        val selectionArgs = arrayOf(args.phoneNumber)

        val cursor: Cursor? = requireContext().contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            Telephony.Sms.DATE + " ASC"
        )

        cursor?.use {
            val indexBody = it.getColumnIndex(Telephony.Sms.BODY)
            val indexDate = it.getColumnIndex(Telephony.Sms.DATE)
            val indexAddress = it.getColumnIndex(Telephony.Sms.ADDRESS)

            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            while (it.moveToNext()) {
                val body = it.getString(indexBody) ?: ""
                val dateMillis = it.getLong(indexDate)
                val address = it.getString(indexAddress) ?: "Unknown"
                val date = dateFormat.format(Date(dateMillis))

                smsList.add(SmsMessage(address, body, date))
            }
        }

        val adapter = SmsAdapter(smsList) { /* No-op */ }
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            this.adapter = adapter
        }
    }

    private fun sendSms() {
        val message = binding.etChatMessage.text.toString().trim()
        if (message.isEmpty()) return

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                val smsManager: SmsManager = requireContext().getSystemService(SmsManager::class.java)
                smsManager.sendTextMessage(args.phoneNumber, null, message, null, null)
                Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show()
                binding.etChatMessage.setText("")
                binding.root.postDelayed({ loadConversation() }, 1000)
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}