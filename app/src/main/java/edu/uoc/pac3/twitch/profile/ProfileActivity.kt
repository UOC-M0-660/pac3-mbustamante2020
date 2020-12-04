package edu.uoc.pac3.twitch.profile

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.webkit.CookieManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import edu.uoc.pac3.LaunchActivity
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.user.User
import kotlinx.coroutines.*


class ProfileActivity : AppCompatActivity() {

    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnDescription = findViewById<Button>(R.id.updateDescriptionButton)
        val btnLogout = findViewById<Button>(R.id.logoutButton)

        getUser()

        btnDescription.setOnClickListener {
            updateUser()
        }
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun getUser() {
        lifecycleScope.launch {
            whenStarted {
                val user = withContext(Dispatchers.IO) {
                    TwitchApiService(Network.createHttpClient(this@ProfileActivity)).getUser()
                }
                if (user == null) {
                    Toast.makeText(applicationContext, R.string.error_profile, Toast.LENGTH_LONG).show()
                } else {
                    loadUser(user)
                }
            }
        }
    }

    private fun updateUser() {
        lifecycleScope.launch {
            whenStarted {
                val description = findViewById<TextView>(R.id.userDescriptionEditText).text.toString()
                val user = withContext(Dispatchers.IO) {
                    TwitchApiService(Network.createHttpClient(this@ProfileActivity)).updateUserDescription(description)
                }

                if (user == null) {
                    Toast.makeText(applicationContext, R.string.error_profile_update, Toast.LENGTH_LONG).show()
                } else {
                    loadUser(user)
                }
            }
        }
    }

    private fun loadUser(user: User?) {
        findViewById<TextView>(R.id.userNameTextView).text = user?.userName
        findViewById<TextView>(R.id.userDescriptionEditText).text = user?.description
        findViewById<TextView>(R.id.viewsText).text = user?.viewCount.toString()

        val imageSize: String = R.dimen.profile_image_size.toString() + "x" + R.dimen.profile_image_size.toString()
        AsyncTask.execute {
            runOnUiThread {
                Glide.with(this@ProfileActivity)
                        .load(user?.userImageUrl?.replace("{width}x{height}", imageSize))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(findViewById<ImageView>(R.id.imageView))
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            async {
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()
            }
            async {
                SessionManager(this@ProfileActivity).clearAccessToken()
                SessionManager(this@ProfileActivity).clearRefreshToken()
            }
        }
        val intent = Intent(this, LaunchActivity::class.java)
        startActivity(intent)
        finish()
    }
}