package com.beyondthehorizon.routeapp.views.transactions.main

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.models.ReceiptModel
import com.beyondthehorizon.routeapp.models.TransactionModel
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.settingsactivities.InviteFriendActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_transaction_details.*
import kotlinx.android.synthetic.main.activity_transaction_details.receipt_amount
import kotlinx.android.synthetic.main.upload_image_layout.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class TransactionDetailsActivity : AppCompatActivity() {

    val myRequestCode = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        val pref = getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        val editor = pref.edit()
        editor.remove(Constants.SHARE_RECEIPT_TO_ID)
        editor.remove(Constants.SHARE_RECEIPT_TITLE)
        editor.apply()
        val gson = Gson()
        val transactionModel = gson.fromJson<TransactionModel>(pref.getString(TRANSACTION_DETAILS, ""),
                TransactionModel::class.java!!)

        receipt_amount.text = transactionModel.withdrawn
        receipt_date.text = transactionModel.created_at
//        receipt_status.text = transactionModel.st
        username.text = transactionModel.details
        email.text = transactionModel.email
        destination.text = transactionModel.description

        var requestOptions = RequestOptions();
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16));
        Glide.with(this@TransactionDetailsActivity)
                .load(transactionModel.profile_image)
                .centerCrop()
                .error(R.drawable.ic_user)
                .placeholder(R.drawable.ic_user)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(profile_image)
        //upload image
        uploadReceipt.setOnClickListener {
            // start picker to get image for cropping and then use the image in cropping activity
            val intent = CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(this@TransactionDetailsActivity)
            startActivityForResult(intent, myRequestCode)
        }
        shareReceipt.setOnClickListener {
            if (receipt_title.text.toString().trim().isEmpty()) {
                Toast.makeText(this@TransactionDetailsActivity, "Enter Transaction Type", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent = Intent(this@TransactionDetailsActivity, InviteFriendActivity::class.java)
            editor.putString(Constants.SHARE_RECEIPT_TITLE, receipt_title.text.toString().trim())
            editor.apply()

            intent.putExtra("TYPE", "SHARE")
            startActivity(intent)
        }

    }

    fun prevPage(view: View) {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == myRequestCode && data != null) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.getUri()
                val actualImage = File(resultUri.getPath()!!)
                try {
                    val compressedImage = Compressor(this)
                            .setQuality(100)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToBitmap(actualImage)

                    val baos = ByteArrayOutputStream()
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val finalImage = baos.toByteArray()

                    showCustomDialog(finalImage, resultUri)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.getError()
            }
        }
    }


    private fun showCustomDialog(finalImage: ByteArray, resultUri: Uri) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(this).inflate(R.layout.upload_image_layout, viewGroup, false)
        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
        val progressBar = ProgressDialog(this@TransactionDetailsActivity)
        progressBar.setCanceledOnTouchOutside(false)
        dialogView.myImage11.setImageURI(resultUri)
        dialogView.saveImage11.setOnClickListener(View.OnClickListener {
            progressBar.setMessage("Please wait...")
            progressBar.show()
            try {
                val storage = Firebase.storage
                val storageReference = storage.reference
                val urL = username.text.toString() + "/" + System.currentTimeMillis()

//                val userToken = sharedpreferences.getString(UserToken, "empty")
                val filePath = storageReference.child(RECEIPTS)
                        .child("$urL.jpg")

                val uploadTask = filePath.putBytes(finalImage)

                uploadTask.addOnFailureListener(OnFailureListener {
                    progressBar.dismiss()
                    Toast.makeText(this@TransactionDetailsActivity, "Unable to upload image, try again later", Toast.LENGTH_LONG).show()
                }).addOnSuccessListener(OnSuccessListener<Any> {
                    filePath.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Uri> { uri ->

                        Log.d("TransactionDetails", "onSuccess: $uri")
                        imageLink.text = "receipt-image-attached"
                        val pref = getSharedPreferences(REG_APP_PREFERENCES, 0)
                        val editor = pref.edit()
                        editor.putString(Constants.SHARE_RECEIPT_TO_ID, uri.toString())
                        editor.apply()
                        progressBar.dismiss()
                        alertDialog.dismiss()
                    })
                })


            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        dialogView.deleteService11.setOnClickListener(View.OnClickListener { alertDialog.dismiss() })
    }
}
