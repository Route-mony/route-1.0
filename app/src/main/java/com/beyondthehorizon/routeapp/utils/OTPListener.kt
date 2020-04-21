package com.beyondthehorizon.routeapp.utils

interface Common {
    interface OTPListener{
        fun onOTPReceived(otp:String)
    }
}
