package edu.uoc.pac3.data.oauth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.util.*

/**
 * Copyright 2020, Kurt Renzo Acosta, All rights reserved.
 *
 * @author Kurt Renzo Acosta
 * @since 04/08/2020
 */

class OAuthFeature(
        private val getToken: suspend () -> String,
        private val refreshToken: suspend () -> Unit
) {
    class Config {
        lateinit var getToken: suspend () -> String
        lateinit var refreshToken: suspend () -> Unit
    }

    companion object Feature : HttpClientFeature<Config, OAuthFeature> {
        override val key: AttributeKey<OAuthFeature> = AttributeKey("OAuth")

        override fun prepare(block: Config.() -> Unit): OAuthFeature {
            val config = Config().apply(block)
            return OAuthFeature(config.getToken, config.refreshToken)
        }

        private val RefreshKey = "Ktor-OAuth-Refresh"

        override fun install(feature: OAuthFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                // Add Refresh Header for handling infinite loop on 401s
                context.headers[RefreshKey] = context.headers.contains("Authorization").toString()

                // Add Authorization Header
                context.headers["Authorization"] = "Bearer ${feature.getToken()}"

                proceed()
            }

            scope.receivePipeline.intercept(HttpReceivePipeline.After) {
                /*
                * Inicialmente estaba subject.status == HttpStatusCode.Unauthorized, estos valores entregan
                * los siguiente valores [401 ] == [401 Unauthorized], para solucionar esto se dejó de esta forma
                * subject.status.value == HttpStatusCode.Unauthorized.value
                * */
                // Request is unauthorized
                if (subject.status.value == HttpStatusCode.Unauthorized.value && context.request.headers[RefreshKey] != true.toString()) {
                    try {
                        // Refresh the Token
                        feature.refreshToken()

                        // Retry the request
                        val call = scope.requestPipeline.execute(
                                HttpRequestBuilder().takeFrom(context.request),
                                EmptyContent
                        ) as HttpClientCall

                        // Proceed with the new request
                        proceedWith(call.response)

                        return@intercept
                    } catch (exception: Exception) {
                        // If refresh fails, proceed as 401
                    }
                }
                // Proceed as normal request
                proceedWith(subject)
            }
        }
    }
}