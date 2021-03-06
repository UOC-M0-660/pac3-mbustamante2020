package edu.uoc.pac3.data.network

import android.content.Context
import android.util.Log
import com.moczul.ok2curl.CurlInterceptor
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthFeature
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json


/**
 * Created by alex on 07/09/2020.
 */
object Network {

    private const val TAG = "Network"

    fun createHttpClient(context: Context): HttpClient {
        return HttpClient(OkHttp) {
            // TODO: Setup HttpClient
            // Json
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
            // Logging
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Ktor---", message)
                    }
                }
                level = LogLevel.ALL
            }
            // Timeout
            install(HttpTimeout) {
                requestTimeoutMillis = 15000L
                connectTimeoutMillis = 15000L
                socketTimeoutMillis = 15000L
            }

            install(OAuthFeature) {
                getToken = { SessionManager(context).getAccessToken().toString() }
                refreshToken = {
                    val response: OAuthTokensResponse? = createHttpClient(context)
                            .post<OAuthTokensResponse>(Endpoints.oauthToken) {
                                parameter("client_id", OAuthConstants.clientID)
                                parameter("client_secret", OAuthConstants.clientSecret)
                                parameter("refresh_token", SessionManager(context).getRefreshToken().toString())
                                parameter("grant_type", "refresh_token")
                            }
                    SessionManager(context).saveAccessToken(response?.accessToken.toString())
                }
            }

            // Apply to All Requests
            defaultRequest {
                // Content Type
                if (this.method != HttpMethod.Get) contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
            // Optional OkHttp Interceptors
            engine {
                addInterceptor(CurlInterceptor { Log.v("Curl", it) })
            }
        }
    }

    //2 import
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
}