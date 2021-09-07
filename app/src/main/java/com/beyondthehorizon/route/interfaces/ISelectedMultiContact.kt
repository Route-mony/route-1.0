package com.beyondthehorizon.route.interfaces

import com.beyondthehorizon.route.models.contacts.MultiContactModel

interface ISelectedMultiContact {
    fun selectedMultiContact(contact: MultiContactModel, pos: Int)
}