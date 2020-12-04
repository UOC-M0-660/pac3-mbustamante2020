package edu.uoc.pac3.oauth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.twitch.streams.StreamsActivity
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OAuthActivity : AppCompatActivity() {

    private val TAG = "OAuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        launchOAuthAuthorization()
    }

    fun buildOAuthUri(): Uri {
        // TODO: Create URI
        return Uri.parse(Endpoints.oauthAuthorize)
                .buildUpon()
                .appendQueryParameter("client_id", OAuthConstants.clientID)
                .appendQueryParameter("redirect_uri", OAuthConstants.redirectUri)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope", OAuthConstants.scopes.joinToString(separator = " "))
                .appendQueryParameter("state", OAuthConstants.uniqueState)
                .build()
    }

    private fun launchOAuthAuthorization() {
        //  Create URI
        val uri = buildOAuthUri()

        // TODO: Set webView Redirect Listener
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.let {
                    // Check if this url is our OAuth redirect, otherwise ignore it
                    if (request.url.toString().startsWith(OAuthConstants.redirectUri)) {
                        // To prevent CSRF attacks, check that we got the same state value we sent, otherwise ignore it
                        val responseState = request.url.getQueryParameter("state")
                        if (responseState == OAuthConstants.uniqueState) {
                            // This is our request, obtain the code!
                            request.url.getQueryParameter("code")?.let { code ->
                                // Got it!
                                onAuthorizationCodeRetrieved(code)
                                return true
                            } ?: run {
                                // User cancelled the login flow
                                loginIntent()
                                return true
                            }
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        // Load OAuth Uri
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(uri.toString())
    }

    private fun loginIntent() {
        Toast.makeText(applicationContext, R.string.error_oauth, Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun streamsIntent() {
        startActivity(Intent(this@OAuthActivity, StreamsActivity::class.java))
        finish()
    }

    // Call this method after obtaining the authorization code
    // on the WebView to obtain the tokens
    private fun onAuthorizationCodeRetrieved(authorizationCode: String) {
        //CoroutineScope(Dispatchers.Main).launch {
        lifecycleScope.launch {
            whenStarted {
                // Show Loading Indicator
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.VISIBLE
                }

                // TODO: Create Twitch Service
                val network = Network.createHttpClient(this@OAuthActivity)

                // TODO: Get Tokens from Twitch
                val tokenTwitch: OAuthTokensResponse? = withContext(Dispatchers.IO) {
                    TwitchApiService(network).getTokens(authorizationCode)
                }

                // TODO: Save access token and refresh token using the SessionManager class
                if (tokenTwitch != null) {
                    SessionManager(this@OAuthActivity).saveAccessToken(tokenTwitch.accessToken)
                    SessionManager(this@OAuthActivity).saveRefreshToken(tokenTwitch.refreshToken.toString())
                    streamsIntent()
                } else {
                    loginIntent()
                }
            }
        }
    }
}