package com.fibonacci.mycontactgue.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fibonacci.mycontactgue.data.CallLog
import com.fibonacci.mycontactgue.data.Contact
import com.fibonacci.mycontactgue.databinding.ItemCallLogBinding
import java.text.SimpleDateFormat
import java.util.*

class CallLogAdapter(
    private var callLogs: List<CallLog>,
    private var contacts: List<Contact> = emptyList()
) : RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val binding = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        holder.bind(callLogs[position])
    }

    override fun getItemCount(): Int = callLogs.size

    fun updateList(newCallLogs: List<CallLog>, newContacts: List<Contact>) {
        callLogs = newCallLogs
        contacts = newContacts
        notifyDataSetChanged()
    }

    inner class CallLogViewHolder(private val binding: ItemCallLogBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        fun bind(callLog: CallLog) {
            binding.tvCallerName.text = resolveContactName(callLog.phoneNumber)
            binding.tvCallNumber.text = callLog.phoneNumber
            binding.tvCallTime.text = dateFormat.format(Date(callLog.timestamp))
        }

        private fun resolveContactName(phoneNumber: String): String {
            val normalizedInput = phoneNumber.replace("[^0-9]".toRegex(), "")
            if (normalizedInput.isEmpty()) return phoneNumber

            val contact = contacts.find {
                val normalizedContact = it.phoneNumber.replace("[^0-9]".toRegex(), "")
                normalizedContact.endsWith(normalizedInput) || normalizedInput.endsWith(normalizedContact)
            }
            return contact?.name ?: phoneNumber
        }
    }
}