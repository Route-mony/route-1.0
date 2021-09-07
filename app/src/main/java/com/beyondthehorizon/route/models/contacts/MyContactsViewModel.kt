package com.beyondthehorizon.route.models.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

class MyContactsViewModel(
    context: Application,
    private val myContactsRepository: MyContactsRepository
) : AndroidViewModel(context) {

    var myContacts: LiveData<List<MultiContactModel>> = liveData {
        emit(myContactsRepository.fetchContacts())
    }
}
