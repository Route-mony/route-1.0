package com.beyondthehorizon.routeapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import com.beyondthehorizon.routeapp.utils.Common.OTPListener
import java.util.regex.Matcher
import java.util.regex.Pattern


open class SMSListener : BroadcastReceiver() {

    private var mListener: OTPListener? = null
    private lateinit var p: Pattern


    override fun onReceive(context: Context, intent: Intent) {

        var data: Bundle? = intent.extras

        var pdus = arrayOfNulls<Any>(0)

        p = Pattern.compile("(|^)\\d{6}")

        if (data != null) {
            pdus = (data["pdus"] as Array<Any?>?)!! // the pdus key will contain the newly received SMS
        }

        if (pdus != null) {
            for (pdu in pdus) { // loop through and pick up the SMS of interest
                val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdu as ByteArray?)
                var messageBody = smsMessage.messageBody
                val m: Matcher = p.matcher(messageBody)
                if (m.find()) {
                    mListener?.onOTPReceived(m.group(0))
                    break
                }
            }
        }
    }

    fun bindListener(listener: OTPListener) {
        mListener = listener
    }

    fun unbindListener() {
        mListener = null
    }
}