package com.beyondthehorizon.route.utils

import android.content.Context
import com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES
import com.beyondthehorizon.route.utils.Constants.USER_TOKEN
import com.beyondthehorizon.route.views.base.BaseActivity
import com.google.gson.Gson

open class SharedPref(baseActivity: BaseActivity) {
    companion object {
        fun save(context: Context, key: String, value: Any) {
            val pref =
                context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE).edit()
            when (value) {
                is String -> {
                    with(pref) { putString(key, value) }
                }
                is Int -> {
                    with(pref) { putInt(key, value) }
                }
                is Boolean -> {
                    with(pref) { putBoolean(key, value) }
                }
                else -> {
                    val data = Gson().toJson(value)
                    with(pref) { putString(key, data) }
                }
            }
            pref.apply()
        }

        fun getString(context: Context, key: String): String? {
            val pref = context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE)
            return pref.getString(key, "")
        }

        fun getInt(context: Context, key: String): Int {
            val pref = context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE)
            return pref.getInt(key, -1)
        }

        fun getBoolean(context: Context, key: String): Boolean {
            val pref = context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE)
            return pref.getBoolean(key, false)
        }

        fun getData(context: Context, key: String, clazz: Class<*>): Any? {
            val pref = context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE)
            return Gson().fromJson(pref.getString(key, null), clazz)
        }

        fun getToken(context: Context): String {
            val pref = context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE)
            return String.format("Bearer %s", pref.getString(USER_TOKEN, ""));
        }

        fun clear(context: Context) {
            val editor =
                context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE).edit()
            editor.clear()
            editor.apply()
        }

        fun remove(context: Context, key: String) {
            val editor =
                context.getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE).edit()
            editor.remove(key)
            editor.apply()
        }
    }
}