package com.beyondthehorizon.routeapp.views.multicontactschoice.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.bottomsheets.EditSendToManyBottomSheet
import com.beyondthehorizon.routeapp.models.BulkyRequestModel
import kotlinx.android.synthetic.main.activity_send_to_many.*

class SendToManyActivity : AppCompatActivity(), EditSendToManyBottomSheet.EditSendToManyBottomSheetListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_to_many)

        addFab.setOnClickListener {
            val editSendToManyBottomSheet = EditSendToManyBottomSheet();
            editSendToManyBottomSheet.show(supportFragmentManager, "Edit Contact");
        }
    }

    fun prevPage(view: View) {
        onBackPressed()
    }

    override fun editSendToManyItem(arrayList: ArrayList<BulkyRequestModel>, arrayListJson2: ArrayList<String>) {
//        TODO("Not yet implemented")
    }
}
