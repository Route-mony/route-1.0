package com.beyondthehorizon.route.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.EditSendtomanyItemLayoutBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.EditSendToManyBottomSheetListener
import com.beyondthehorizon.route.models.contacts.MultiContactModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.edit_sendtomany_item_layout.view.*

class EditSendToManyBottomSheet(
    private val item: MultiContactModel,
    private val itemPos: Int,
    private val title: String,
    private val onEditSendToManyItem: EditSendToManyBottomSheetListener
) : BottomSheetDialogFragment() {
    private var _binding: EditSendtomanyItemLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditSendtomanyItemLayoutBinding.inflate(layoutInflater, container, false)
        binding.tvEditSendToManyTitle.text = title
        binding.username.text = item.routeUsername


        val view = binding.root
        val gson = Gson()
        val personData = arguments?.getString("personData")
        val itemPosition = arguments?.getString("itemPosition")
        val editPersonData = gson.fromJson(personData, MultiContactModel::class.java)
        v.tvEditSendToManyTitle.text = arguments?.getString("title")
        v.username.text = editPersonData.username
        v.contact.text = editPersonData.phoneNumber
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        if (editPersonData.isRouteUser()) {
            Glide.with(requireContext())
                .load(editPersonData.image)
                .centerCrop()
                .error(R.drawable.group416)
                .placeholder(R.drawable.group416)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(v.profile_image)
        } else {
            Glide.with(requireContext())
                .load(editPersonData.image)
                .centerCrop()
                .error(R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(v.profile_image)
        }
        //login button click of custom layout
        v.dialogAddBtn.setOnClickListener {
            //dismiss dialog
            //get text from EditTexts of custom layout
            val username = v.username.text.toString().trim()
            val contact = v.contact.text.toString().trim()
            val addAmount = v.addAmount.text.toString().trim()
            //set the input text in TextView

            if (addAmount.isEmpty()) {
                v.addAmount.error = "Cannot be empty"
                return@setOnClickListener
            }
            mListener.editSendToManyItem(
                MultiContactModel(
                    amount = addAmount,
                    id = editPersonData.id,
                    username = editPersonData.username,
                    phoneNumber = editPersonData.phoneNumber,
                    image = editPersonData.image,
                    isRoute = editPersonData.isRoute,
                    isSelected = editPersonData.isSelected
                ), itemPosition!!.toInt()
            )
            dismiss()

        }
        return v;
    }
}