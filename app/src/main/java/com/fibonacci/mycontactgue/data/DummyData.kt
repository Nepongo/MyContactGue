package com.fibonacci.mycontactgue.data

object DummyData {
    fun getContacts(): List<Contact> {
        return listOf(
            Contact(1, "Aditya S. N.", "+91 98756 12345", "aditdoaibu@gmail.com", "11th November"),
            Contact(2, "Dudur Penguasa Dunia Bawah", "+91 98756 12345", "kingdudur@gmail.com", "18th March"),
            Contact(3, "Adam", "+1 123 456 7890", "adam@work.com", "1st January"),
            Contact(4, "Alice", "+1 234 567 8901", "alice@work.com", "2nd February"),
            Contact(5, "Ava", "+1 345 678 9012", "ava@friend.com", "3rd March"),
            Contact(6, "Ben", "+1 456 789 0123", "ben@friend.com", "4th April"),
            Contact(7, "Bella", "+1 567 890 1234", "bella@friend.com", "5th May"),
            Contact(8, "Broke", "+44 1234 567890", "broke@work.com", "6th June"),
            Contact(9, "Clary", "+44 2345 678901", "clary@work.com", "7th July"),
            Contact(10, "Clara", "+44 3456 789012", "clara@work.com", "8th August"),
        )
    }
}
