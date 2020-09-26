package com.beyondthehorizon.route.views.settingsactivities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.utils.Utils
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.auth.LoginActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.nav_bar_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class UserProfileActivity : AppCompatActivity() {

    private val TAG = "UserProfileActivity"
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    val myRequestCode = 201
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        getProfile()

        pref = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref!!.edit()

        saveUpdate.setOnClickListener {
            updateProfile()
        }

        updateImage.setOnClickListener {
            // start picker to get image for cropping and then use the image in cropping activity
            val intent = CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(this@UserProfileActivity)
            startActivityForResult(intent, myRequestCode)
        }
        back.setOnClickListener {
            onBackPressed()
        }


        btn_home.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun updateProfile() {
        val pref: SharedPreferences = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        val token = "Bearer " + pref.getString(USER_TOKEN, "")
        val progressDialog = ProgressDialog(this@UserProfileActivity)
        if(TextUtils.isEmpty(userNameTxt.text)){
            userNameTxt.error = "Username cannot be empty!"
            return
        }
        if(TextUtils.isEmpty(phone.text)){
            phone.error = "Phone number cannot be empty!"
            return
        }

        if(Utils.isPhoneNumberValid(phone.text.toString(), "KE")) {
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            updateUserProfile(this@UserProfileActivity, token,
                    phone.text.toString(),
                    userNameTxt.text.toString())
                    .setCallback { _, result ->
                        progressDialog.dismiss()
                        Toast.makeText(this@UserProfileActivity, "Updated successfully", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "updateUserProfileSettings: " + result!!)
                    }
        }
        else{
            phone.error = "Invalid phone number!"
        }
    }

    private fun getProfile() {

        val pref: SharedPreferences = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        val editor: SharedPreferences.Editor
        editor = pref.edit()
        val token = "Bearer " + pref.getString(USER_TOKEN, "")

        Log.d(TAG, "getProfile: $token")
        val progressDialog = ProgressDialog(this@UserProfileActivity)
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        getUserProfile(this@UserProfileActivity, token)
                .setCallback { e, result ->
                    progressDialog.dismiss()
                    Log.e(TAG, "getUserProfileSettings: " + result!!)

                    if (result != null) {

                        if (result.get("status").toString().contains("failed")) {
                            editor.clear()
                            editor.apply()
                            startActivity(Intent(this@UserProfileActivity, LoginActivity::class.java))
                        } else if (result.get("status").toString().contains("success")) {

                            val username = result.get("data").asJsonObject.get("username").asString
                            val fname = result.get("data").asJsonObject.get("first_name").asString
                            val lname = result.get("data").asJsonObject.get("last_name").asString
                            val emailString = result.get("data").asJsonObject.get("email").asString
                            val phone_number = result.get("data").asJsonObject.get("phone_number").asString
                            val image = result.get("data").asJsonObject.get("image").asString

                            phone.setText(phone_number)
                            userNameTxt.setText(username)
                            email.setText(emailString)
                            fullNames.setText("${fname} ${lname}")
                            var requestOptions = RequestOptions();
                            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16));
                            Glide.with(this@UserProfileActivity)
                                    .load(image)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.ic_user_home_page)
                                    .placeholder(R.drawable.ic_user_home_page)
                                    .apply(requestOptions)
                                    .into(profile_pic)

                            gender.setText("None")
                            dob.setText("0-00-1900")

                        }
                    } else {
                        val snackbar = Snackbar
                                .make(RL1, "Unable to load data ", Snackbar.LENGTH_LONG)
                        snackbar.setAction("Try again") {
                            if (pref.getString(USER_TOKEN, "")!!.isEmpty()) {
                                editor.putString(LOGGED_IN, "false")
                                editor.apply()
                                val intent = Intent(this@UserProfileActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                getProfile()
                            }
                        }
                        snackbar.show()
                    }
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == myRequestCode && data != null) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.getUri()
                val actualImage = File(resultUri.getPath()!!)
                val progressBar = ProgressDialog(this@UserProfileActivity)
                progressBar.setCanceledOnTouchOutside(false)
                try {
                    val compressedImage = Compressor(this)
                            .setQuality(100)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToBitmap(actualImage)

                    val baos = ByteArrayOutputStream()
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val finalImage = baos.toByteArray()

//                    showCustomDialog(finalImage, resultUri)
                    progressBar.setMessage("Please wait...")
                    progressBar.show()
                    try {
                        val storage = Firebase.storage
                        val storageReference = storage.reference

                        val pref: SharedPreferences = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0)
                        val token = "Bearer " + pref.getString(USER_TOKEN, "")


                        val urL = userNameTxt.text.toString().trim()
                        val filePath = storageReference.child(PROFILE_IMAGES)
                                .child("$urL.jpg")
                        val uploadTask = filePath.putBytes(finalImage)

                        uploadTask.addOnFailureListener(OnFailureListener {
                            progressBar.dismiss()
                            Toast.makeText(this@UserProfileActivity, "Unable to upload image, try again later", Toast.LENGTH_LONG).show()
                        }).addOnSuccessListener(OnSuccessListener<Any> {
                            filePath.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Uri> { uri ->

                                updateUserProfileImage(this@UserProfileActivity, token,
                                        uri.toString())
                                        .setCallback { _, result ->
                                            Glide.with(this@UserProfileActivity)
                                                    .load(uri.toString())
                                                    .centerCrop()
                                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                    .skipMemoryCache(true)
                                                    .error(R.drawable.ic_user_home_page)
                                                    .placeholder(R.drawable.ic_user_home_page)
                                                    .into(profile_pic)
                                            editor!!.putString("ProfileImage", uri.toString())
                                            editor!!.apply()
                                            progressBar.dismiss()
                                            Toast.makeText(this@UserProfileActivity, "Profile Picture updated successfully", Toast.LENGTH_LONG).show()

                                        }
                            })
                        })


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.getError()
            }
        }
    }
}
