package edu.uoc.pac3.data.oauth

import java.util.*
/**
 * Created by alex on 07/09/2020.
 */
object OAuthConstants {

    // TODO: Set OAuth2 Variables
    val uniqueState = UUID.randomUUID().toString()
    const val clientID = "93e9vxk1j9jauy58qlez0oqrrmlfa3"
    const val clientSecret = "tedrddnqnb4h3sqfv1f2ibybgn870l"
    const val redirectUri = "https://www.twitch.tv/"
    val scopes = listOf<String>("user:read:email", "user:edit")
}