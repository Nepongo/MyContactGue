package com.fibonacci.mycontactgue.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fibonacci.mycontactgue.R
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.databinding.ItemContactBinding

class ContactAdapter(private var contactList: List<Contact>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bind(contact)
        holder.itemView.setOnClickListener {
            val action = ContactListFragmentDirections.actionContactListFragmentToContactDetailFragment(contact)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = contactList.size

    fun updateList(newList: List<Contact>) {
        contactList = newList
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.tvContactName.text = contact.name
            binding.tvContactNumber.text = contact.phoneNumber
            
            contact.photoUri?.let {
                binding.ivContactPhoto.setImageURI(it.toUri())
            } ?: binding.ivContactPhoto.setImageResource(R.drawable.ic_default_person)
        }
    }
}
