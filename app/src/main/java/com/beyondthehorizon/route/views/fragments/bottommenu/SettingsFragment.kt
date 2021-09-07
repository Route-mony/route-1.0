package com.beyondthehorizon.route.views.fragments.bottommenu

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.FragmentSettingsBinding
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.Constants.BALANCE_CHECK
import com.beyondthehorizon.route.utils.Constants.USER_PROFILE
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.auth.LoginActivity
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.services.common.AmountFragment
import com.beyondthehorizon.route.views.settingsactivities.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import io.kommunicate.KmConversationBuilder
import io.kommunicate.Kommunicate
import io.kommunicate.callbacks.KmCallback
import io.kommunicate.users.KMUser

class SettingsFragment : BaseFragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileResponse: ProfileResponse
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        profileResponse = SharedPref.getData(
            requireContext(),
            USER_PROFILE,
            ProfileResponse::class.java
        ) as ProfileResponse
        Kommunicate.init(requireContext(), "ba520ce1b1256908b973b5f89a451913");

        var requestOptions = RequestOptions();
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16));
        Glide.with(requireContext())
            .load(profileResponse.getProfilePic())
            .centerCrop()
            .error(R.drawable.ic_user_home_page)
            .placeholder(R.drawable.ic_user_home_page)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .skipMemoryCache(true)
            .apply(requestOptions)
            .into(binding.profilePic)
        binding.userName.text = profileResponse.getFullName()
        binding.inviteFriends.setOnClickListener {
            val intent = Intent(requireContext(), InviteFriendActivity::class.java)
            intent.putExtra("TYPE", "Invite")
            startActivity(intent)
        }

        binding.pinAndPass.setOnClickListener {
            startNewActivity(PasswordAndPinActivity::class.java)
        }
        binding.paymentMethods.setOnClickListener {
            transactionData.process = 0
            replaceFragment(AmountFragment(transactionData))
        }
        binding.termsAndConditions.setOnClickListener {
            startActivity(Intent(requireContext(), TermsOfUseActivity::class.java))
        }

        binding.hd.setOnClickListener {
            startNewActivity(UserProfileActivity::class.java)
        }

        binding.downLoadReceipt.setOnClickListener {
            startNewActivity(DownloadStatementActivity::class.java)
        }

        // Show balalance
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            SharedPref.save(requireContext(), BALANCE_CHECK, isChecked)
        }
        binding.switch1.isChecked = SharedPref.getBoolean(requireContext(), BALANCE_CHECK)

        binding.logOut.setOnClickListener {
            SharedPref.clear(requireContext())
            startNewActivity(LoginActivity::class.java)
        }

        //Support
        binding.supportHelp.setOnClickListener {
            val prog = ProgressDialog(requireContext())
            prog.setMessage("Loading...")
            prog.setCanceledOnTouchOutside(false)
            prog.show()
            val user = KMUser();
            user.userId = profileResponse.getUserId()
            user.imageLink = profileResponse.getProfilePic()
            user.displayName = profileResponse.getFullName()
            KmConversationBuilder(requireContext())
                .setWithPreChat(false)
                .setKmUser(user)
                .launchConversation(object : KmCallback {
                    override fun onSuccess(message: Any) {
                        Log.d("Conversation", "Success : $message")
                        prog.dismiss()
                    }

                    override fun onFailure(error: Any) {
                        Log.d("Conversation", "Failure : $error")
                    }
                })
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}