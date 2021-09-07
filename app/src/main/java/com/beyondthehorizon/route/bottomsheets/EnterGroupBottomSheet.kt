package com.beyondthehorizon.route.bottomsheets

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beyondthehorizon.route.databinding.GroupNameItemBinding
import com.beyondthehorizon.route.interfaces.bottomsheets.OnGroupEntryListener
import com.beyondthehorizon.route.views.base.BaseBottomSheetFragment
import kotlinx.android.synthetic.main.group_name_item.view.*

class EnterGroupBottomSheet(private val onGroupEntryListener: OnGroupEntryListener) :
    BaseBottomSheetFragment() {
    private var _binding: GroupNameItemBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GroupNameItemBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.dialogButtonOKk.setOnClickListener {
            val groupName = binding.groupInput.text.toString()
            if (TextUtils.isEmpty(groupName.trim())) {
                Toast.makeText(requireActivity(), "Enter group name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            onGroupEntryListener.onCaptureGroupName(groupName)
            dismiss()
        }

        binding.dialogButtonNoo.setOnClickListener {
            dismiss()
        }
        return view
    }
}