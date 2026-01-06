package com.fibonacci.mycontactgue.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fibonacci.mycontactgue.databinding.FragmentRemindersBinding

class RemindersFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        val dummyReminders = listOf(
            Reminder("Call David", "Today at 5:00 PM"),
            Reminder("Meeting with team", "Tomorrow at 10:00 AM"),
            Reminder("Buy groceries", "Today at 6:30 PM"),
            Reminder("Call Mom", "Sunday at 11:00 AM")
        )

        val reminderAdapter = ReminderAdapter(dummyReminders)
        binding.rvReminders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
