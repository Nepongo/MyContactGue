package com.fibonacci.mycontactgue.ui

import android.app.DatePickerDialog
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
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.data.Profile
import com.fibonacci.mycontactgue.databinding.FragmentProfileBinding
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
    }

    private var currentPhotoUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it
            binding.ivProfilePhoto.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        contactViewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                showDisplayView(profile)
            } else {
                showFormView()
            }
        }

        binding.cvProfilePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.etProfileBirthday.setOnClickListener {
            showDatePicker()
        }

        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }

        binding.btnEditProfile.setOnClickListener {
            showFormView()
        }
    }

    private fun showDisplayView(profile: Profile) {
        binding.cardDisplay.visibility = View.VISIBLE
        binding.cardForm.visibility = View.GONE
        
        binding.tvProfileName.text = profile.name
        binding.tvProfilePhone.text = profile.phoneNumber
        binding.tvProfileEmail.text = profile.email
        binding.tvProfileBirthday.text = profile.birthday
        
        profile.photoUri?.let {
            binding.ivProfilePhoto.setImageURI(it.toUri())
        }
    }

    private fun showFormView() {
        binding.cardDisplay.visibility = View.GONE
        binding.cardForm.visibility = View.VISIBLE
        
        // Populate if data exists
        val currentProfile = contactViewModel.profile.value
        currentProfile?.let {
            binding.etProfileName.setText(it.name)
            binding.etProfilePhone.setText(it.phoneNumber)
            binding.etProfileEmail.setText(it.email)
            binding.etProfileBirthday.setText(it.birthday)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.etProfileBirthday.setText(date)
        }, year, month, day).show()
    }

    private fun saveProfile() {
        val name = binding.etProfileName.text.toString().trim()
        val phone = binding.etProfilePhone.text.toString().trim()
        val email = binding.etProfileEmail.text.toString().trim()
        val birthday = binding.etProfileBirthday.text.toString().trim()

        if (name.isEmpty()) {
            binding.tilProfileName.error = "Name required"
            return
        }

        val profile = Profile(
            name = name,
            phoneNumber = phone,
            email = email,
            birthday = birthday,
            photoUri = currentPhotoUri?.toString() ?: contactViewModel.profile.value?.photoUri
        )
        
        contactViewModel.saveProfile(profile)
        Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
