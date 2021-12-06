package kr.ac.myungji.quickunderroute

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, default: String?): String? {
        return prefs.getString(key, default)
    }

    fun setString(key: String, str: String?) {
        prefs.edit().putString(key, str).apply()
    }

    fun getInt(key: String, default: Int): Int {
        return prefs.getInt(key, default)
    }

    fun setInt(key: String, str: Int?) {
        if (str != null) {
            prefs.edit().putInt(key, str).apply()
        }
    }
}
