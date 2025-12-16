package com.fibonacci.mycontactgue.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.fibonacci.mycontactgue.ContactsApplication
import com.fibonacci.mycontactgue.R
import com.fibonacci.mycontactgue.databinding.FragmentContactDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ContactDetailFragment : Fragment() {

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ContactDetailFragmentArgs by navArgs()

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((activity?.application as ContactsApplication).repository)
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
        binding.toolbar.title = args.contact.name

        val contact = args.contact

        binding.tvDetailName.text = contact.name
        binding.tvDetailPhone.text = contact.phoneNumber
        binding.tvDetailEmail.text = contact.email
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
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${args.contact.name}?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Delete") { _, _ ->
                contactViewModel.deleteContact(args.contact)
                Toast.makeText(context, "${args.contact.name} deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
