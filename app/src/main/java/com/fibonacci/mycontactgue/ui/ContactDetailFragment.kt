package com.fibonacci.mycontactgue.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.R
import com.fibonacci.mycontactgue.data.CallLog
import com.fibonacci.mycontactgue.databinding.FragmentContactDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ContactDetailFragment : Fragment() {

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ContactDetailFragmentArgs by navArgs()

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initiateCall()
        } else {
            Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(findNavController())

        val contact = args.contact
        binding.toolbar.title = ""
        binding.tvDetailName.text = contact.name
        binding.tvDetailPhone.text = contact.phoneNumber
        binding.tvDetailEmail.text = contact.email

        try {
            contact.photoUri?.let {
                binding.ivDetailPhoto.setImageURI(it.toUri())
            } ?: binding.ivDetailPhoto.setImageResource(R.drawable.ic_default_person)
        } catch (e: SecurityException) {
            binding.ivDetailPhoto.setImageResource(R.drawable.ic_default_person)
        }

        binding.btnCall.setOnClickListener { checkCallPermission() }
        binding.btnMessage.setOnClickListener { navigateToChat() }
        binding.btnEmail.setOnClickListener { initiateEmail() }
    }

    private fun checkCallPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                initiateCall()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    private fun initiateCall() {
        val phoneNumber = args.contact.phoneNumber
        if (phoneNumber.isNotBlank()) {
            val callLog = CallLog(
                contactName = args.contact.name,
                phoneNumber = phoneNumber,
                callType = "OUTGOING",
                timestamp = System.currentTimeMillis(),
                duration = 0
            )
            contactViewModel.addCallLog(callLog)

            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_no_phone), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToChat() {
        val phoneNumber = args.contact.phoneNumber
        if (phoneNumber.isNotBlank()) {
            val action = ContactDetailFragmentDirections.actionContactDetailFragmentToChatFragment(phoneNumber)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_no_phone), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun initiateEmail() {
        val email = args.contact.email
        if (email.isNotBlank()) {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Email is not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val action = ContactDetailFragmentDirections.actionContactDetailFragmentToCreateContactFragment(args.contact)
                findNavController().navigate(action)
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_contact_title))
            .setMessage(getString(R.string.delete_contact_msg, args.contact.name))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                contactViewModel.deleteContact(args.contact)
                Toast.makeText(context, getString(R.string.contact_deleted_toast, args.contact.name), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}