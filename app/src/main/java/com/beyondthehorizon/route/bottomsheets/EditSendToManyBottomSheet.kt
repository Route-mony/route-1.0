package com.beyondthehorizon.route.bottomsheets

import android.os.Bundle
import android.text.TextUtils
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
        binding.contact.text = item.phoneNumber
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

        if (item.isRouteUser()) {
            Glide.with(requireContext())
                .load(item.image)
                .centerCrop()
                .error(R.drawable.group416)
                .placeholder(R.drawable.group416)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(binding.profileImage)
        } else {
            Glide.with(requireContext())
                .load(item.image)
                .centerCrop()
                .error(R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(binding.profileImage)
        }
        binding.dialogAddBtn.setOnClickListener {
            if (TextUtils.isEmpty(binding.addAmount.text)) {
                binding.addAmount.error = "Enter amount"
                return@setOnClickListener
            }
            onEditSendToManyItem.onSelectGroupItemForEdit(
                MultiContactModel(
                    amount = binding.addAmount.text.toString().trim(),
                    id = item.id,
                    username = item.username,
                    phoneNumber = item.phoneNumber,
                    image = item.image,
                    isRoute = item.isRoute,
                    isSelected = item.isSelected
                ),
                itemPos
            )
            dismiss()
        }
        return view
    }
}