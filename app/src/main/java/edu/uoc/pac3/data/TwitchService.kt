package edu.uoc.pac3.data

import android.content.Context
import android.util.Log
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.streams.Stream
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.data.user.Users
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import okhttp3.Credentials.basic


/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient) {
    private val TAG = "TwitchApiService"

    /// Gets Access and Refresh Tokens on Twitch
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? {
        // TODO("Get Tokens from Twitch")
        return httpClient
                    .post<OAuthTokensResponse>(Endpoints.oauthToken) {
                        parameter("client_id", OAuthConstants.clientID)
                        parameter("client_secret", OAuthConstants.clientSecret)
                        parameter("code", authorizationCode)
                        parameter("grant_type", "authorization_code")
                        parameter("redirect_uri", OAuthConstants.redirectUri)
                    }
    }

    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null): StreamsResponse? {
        // TODO("Get Streams from Twitch")



        val response = try {

            if( cursor.isNullOrEmpty() ) {
                httpClient.get<StreamsResponse>(Endpoints.oauthStreams) {
                    parameter("first", 5)
                }
            } else {
                httpClient.get<StreamsResponse>(Endpoints.oauthStreams) {
                    parameter("first", 5)
                    parameter("after", cursor)
                }
            }

        } catch (e: ResponseException) {
            Log.i("OAuth error", e.response?.headers.toString())
            Log.i("OAuth status", e.response?.status.toString())
            null
        }


        // TODO("Support Pagination")





        return response
    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getUser(): User? {
        // TODO("Get User from Twitch")
        val response = try {
            httpClient.get<Users>(Endpoints.oauthUser)
        } catch (e: ResponseException) {
            null
        }
        return response?.data?.get(0)
    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun updateUserDescription(description: String): User? {
        // TODO("Update User Description on Twitch")
        val response = try {
            httpClient.put<Users>(Endpoints.oauthUser) {
                parameter("description", description)
            }
        } catch (e: ResponseException) {
            null
        }
        return response?.data?.get(0)
    }
/*
    suspend fun getNewAccessToken(refreshToken: String): OAuthTokensResponse? {
        return httpClient
                .post<OAuthTokensResponse>(Endpoints.oauthToken) {
                    parameter("client_id", OAuthConstants.clientID)
                    parameter("client_secret", OAuthConstants.clientSecret)
                    parameter("refresh_token", refreshToken)
                    parameter("grant_type", "refresh_token")
                }
    }*/
}