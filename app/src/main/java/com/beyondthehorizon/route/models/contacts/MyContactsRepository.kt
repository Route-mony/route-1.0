package com.beyondthehorizon.route.models.contacts

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MyContactsRepository(
    private val source: MyContactsDataSource,
    private val myDispatcher: CoroutineDispatcher
) {

    suspend fun fetchContacts(): List<MultiContactModel> {
        return withContext(myDispatcher) {
            source.fetchContacts()
        }
    }
}
