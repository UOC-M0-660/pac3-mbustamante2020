package edu.uoc.pac3.data

import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.data.user.Users
import io.ktor.client.*
import io.ktor.client.request.*


/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient) {
    private val TAG = "TwitchApiService"

    /// Gets Access and Refresh Tokens on Twitch
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? {
        // TODO("Get Tokens from Twitch")
        return try {
            httpClient
                    .post<OAuthTokensResponse>(Endpoints.oauthToken) {
                        parameter("client_id", OAuthConstants.clientID)
                        parameter("client_secret", OAuthConstants.clientSecret)
                        parameter("code", authorizationCode)
                        parameter("grant_type", "authorization_code")
                        parameter("redirect_uri", OAuthConstants.redirectUri)
                    }
        } catch (cause: Throwable) {
            null
        }
    }

    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null): StreamsResponse? {
        return try {
            // TODO("Get Streams from Twitch")
            httpClient.get<StreamsResponse>(Endpoints.oauthStreams) {
                header("Client-Id", OAuthConstants.clientID)
                parameter("first", 5)

                // TODO("Support Pagination")
                if ( !cursor.isNullOrEmpty() ) {
                    parameter("after", cursor)
                }
            }
        } catch (cause: Throwable) {
            null
        }
    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getUser(): User? {
        // TODO("Get User from Twitch")
        val response = try {
            httpClient.get<Users>(Endpoints.oauthUser){
                header("Client-Id", OAuthConstants.clientID)
            }
        } catch (cause: Throwable) {
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
                header("Client-Id", OAuthConstants.clientID)
                parameter("description", description)
            }
        } catch (cause: Throwable) {
            null
        }
        return response?.data?.get(0)
    }
}