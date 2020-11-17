package edu.uoc.pac3.data

import android.content.Context

/**
 * Created by alex on 06/09/2020.
 */

class SessionManager(private val context: Context) {

    private val PREFS_NAME = "session"

    fun isUserAvailable(): Boolean {
        // TODO: Implement
        if( !getAccessToken().isNullOrEmpty() && !getRefreshToken().isNullOrEmpty() ) {
            return true
        }
        return false
    }

    fun getAccessToken(): String? {
        // TODO: Implement
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return null
        return sharedPref.getString("accessToken", null)
    }

    fun saveAccessToken(accessToken: String) {
        // TODO("Save Access Token")
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("accessToken", accessToken)
            apply()
        }
    }

    fun clearAccessToken() {
        // TODO("Clear Access Token")
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().remove("accessToken").apply()
    }

    fun getRefreshToken(): String? {
        // TODO("Get Refresh Token")
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return null
        return sharedPref.getString("refreshToken", null)
    }

    fun saveRefreshToken(refreshToken: String) {
        // TODO("Save Refresh Token")
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("refreshToken", refreshToken)
            apply()
        }
    }

    fun clearRefreshToken() {
        // TODO("Clear Refresh Token")
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().remove("refreshToken").apply()
    }

}