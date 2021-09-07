package com.beyondthehorizon.route.interfaces

import com.beyondthehorizon.route.models.Contact

interface ISelectedContact {
    fun selectedContact(contact: Contact)
}