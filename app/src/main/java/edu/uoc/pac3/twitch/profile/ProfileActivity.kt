package edu.uoc.pac3.twitch.profile

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import edu.uoc.pac3.LaunchActivity
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.oauth.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text
import java.lang.Exception


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
        val user = runBlocking {
            TwitchApiService(Network.createHttpClient(this@ProfileActivity)).getUser()
        }
        if( user == null ) {
            Log.i("OAuth ProfileActivity", "getUser")
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            loadUser(user)
        }
    }

    private fun updateUser() {
        //val network = Network.createHttpClient(this)  GlobalScope.launch(Dispatchers.Main)
        val description = findViewById<TextView>(R.id.userDescriptionEditText).text.toString()
        val user = runBlocking {
            TwitchApiService(Network.createHttpClient(this@ProfileActivity)).updateUserDescription(description)
        }
        if( user == null ) {
            Log.i("OAuth ProfileActivity", "updateUser ")
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            loadUser(user)
        }
    }

    private fun loadUser(user: User?) {
        findViewById<TextView>(R.id.userNameTextView).text = user?.userName
        findViewById<TextView>(R.id.userDescriptionEditText).text = user?.description
        findViewById<TextView>(R.id.viewsText).text = user?.viewCount.toString()

        var image = user?.userImageUrl.toString()
        image = Regex("\\{height\\}|\\{width\\}").replace(image, "200")

        Glide.with(this@ProfileActivity)  //2
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(findViewById<ImageView>(R.id.imageView))
    }

    private fun logout() {
        try {
            SessionManager(this).clearAccessToken()
            SessionManager(this).clearRefreshToken()
            (this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
        } catch (e: Exception) {
            Log.d("OAuth Exception", "Access Token: ${e.toString()}")
        }
        Log.d("OAuth logout", "Access Token: ${SessionManager(this).getAccessToken()}, Refresh Token: ${SessionManager(this).getRefreshToken()}")

        startActivity(Intent(this, LaunchActivity::class.java))
    }
}