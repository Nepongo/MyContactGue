package com.fibonacci.mycontactgue.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.R
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.databinding.FragmentCreateContactBinding

class CreateContactFragment : Fragment() {

    private var _binding: FragmentCreateContactBinding? = null
    private val binding get() = _binding!!

    private val args: CreateContactFragmentArgs by navArgs()
    private var contactToEdit: Contact? = null
    private var currentPhotoUri: Uri? = null

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }

    // Activity Result Launcher for picking an image
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it
            binding.ivNewContactPhoto.setImageURI(it)
            binding.tvAddPhotoLabel.visibility = View.GONE // Hide label after selecting image
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactToEdit = args.contactToEdit

        setupToolbar()
        populateFormIfEditing()

        binding.ivNewContactPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveContact()
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.title = if (contactToEdit == null) {
            getString(R.string.create_contact_label)
        } else {
            getString(R.string.edit_contact_label)
        }
    }

    private fun populateFormIfEditing() {
        contactToEdit?.let {
            binding.etName.setText(it.name)
            binding.etPhone.setText(it.phoneNumber)
            binding.etEmail.setText(it.email)
            it.photoUri?.let {
                uriString ->
                currentPhotoUri = uriString.toUri()
                binding.ivNewContactPhoto.setImageURI(currentPhotoUri)
                binding.tvAddPhotoLabel.visibility = View.GONE
            }
        }
    }

    private fun saveContact() {
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val email = binding.etEmail.text.toString()

        if (name.isBlank()) {
            binding.tilName.error = "Nama tidak boleh kosong"
            return
        } else {
            binding.tilName.error = null
        }

        if (contactToEdit == null) {
            val newContact = Contact(
                name = name, 
                phoneNumber = phone, 
                email = email, 
                birthday = "",
                photoUri = currentPhotoUri?.toString()
            )
            contactViewModel.insertContact(newContact)
            Toast.makeText(context, "Kontak $name disimpan", Toast.LENGTH_SHORT).show()
        } else {
            val updatedContact = contactToEdit!!.copy(
                name = name, 
                phoneNumber = phone, 
                email = email,
                photoUri = currentPhotoUri?.toString()
            )
            contactViewModel.updateContact(updatedContact)
            Toast.makeText(context, "Kontak $name diperbarui", Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
