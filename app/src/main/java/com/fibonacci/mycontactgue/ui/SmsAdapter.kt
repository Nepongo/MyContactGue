package com.fibonacci.mycontactgue.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fibonacci.mycontactgue.databinding.ItemSmsBinding

data class SmsMessage(
    val sender: String, 
    val content: String, 
    val time: String,
    val phoneNumber: String? = null // To store the raw number for navigation
)

class SmsAdapter(
    private val messages: List<SmsMessage>,
    private val onItemClick: (SmsMessage) -> Unit
) : RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val binding = ItemSmsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
        holder.itemView.setOnClickListener { onItemClick(message) }
    }

    override fun getItemCount(): Int = messages.size

    inner class SmsViewHolder(private val binding: ItemSmsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: SmsMessage) {
            binding.tvSmsSender.text = message.sender
            binding.tvSmsContent.text = message.content
            binding.tvSmsTime.text = message.time
        }
    }
}