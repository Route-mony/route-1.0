package com.beyondthehorizon.route.interfaces.bottomsheets

import com.beyondthehorizon.route.models.contacts.MultiContactModel

interface EditSendToManyBottomSheetListener {
    fun onSelectGroupItemForEdit(contact: MultiContactModel, pos: Int)
    fun onGroupItemEdited(contact: MultiContactModel, pos: Int)
}