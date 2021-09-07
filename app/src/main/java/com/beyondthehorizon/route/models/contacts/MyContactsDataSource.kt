package com.beyondthehorizon.route.models.contacts

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.Constants.USER_PROFILE
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.utils.Utils

class MyContactsDataSource(
    private val contentResolver: ContentResolver,
    private val context: Context
) {
    val utils = Utils(context)
    fun fetchContacts(): List<MultiContactModel> {
        val result: MutableList<MultiContactModel> = mutableListOf()
        val myPhone = (SharedPref.getData(
            context,
            USER_PROFILE,
            ProfileResponse::class.java
        ) as ProfileResponse).getPhone()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
            ),
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        cursor?.let {
            cursor.moveToFirst()
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val loadedContacts = mutableMapOf<String, Int>()
            while (!cursor.isAfterLast) {
                if (Utils.isPhoneNumberValid(cursor.getString(phoneIndex), "KE")) {
                    val phone = Utils.getFormattedPhoneNumber(cursor.getString(phoneIndex), "KE")
                    if (!loadedContacts.containsKey(phone) && phone != myPhone) {
                        result.add(
                            MultiContactModel(
                                id = "",
                                phoneNumber = phone,
                                username = cursor.getString(nameIndex),
//                            image = cursor.getString(idIndex).toContactImageUri(),
                                image = "",
                                isRoute = "False",
                                isSelected = "False",
                                amount = "0"
                            )
                        ) //add the item
                        loadedContacts[phone] = 1
                    }
                }
                cursor.moveToNext()
            }
            cursor.close()
        }
        return result.toList()
    }

}

fun String.toContactImageUri() = Uri.withAppendedPath(
    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, this.toLong()),
    ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
).toString()
