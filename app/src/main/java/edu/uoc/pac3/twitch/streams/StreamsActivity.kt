package edu.uoc.pac3.twitch.streams

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.streams.Stream
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.oauth.LoginActivity
import edu.uoc.pac3.twitch.profile.ProfileActivity
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class StreamsActivity : AppCompatActivity() {

    private val TAG = "StreamsActivity"
    private lateinit var adapter: StreamsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)
        // Init RecyclerView
        initRecyclerView()
        // TODO: Get Streams
        val network = Network.createHttpClient(this)
        val streamsTwitch = runBlocking {
            TwitchApiService(network).getStreams(null)
        }

        // TODO: Get Tokens from Twitch
        if( streamsTwitch == null ) {
            Log.i("OAuth 61", "Get Streams")
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            streamsTwitch.data?.let { adapter.setStream(it, this@StreamsActivity) }
        }
    }

    private fun initRecyclerView() {
        // TODO: Implement
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set Layout Manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = StreamsListAdapter(emptyList())
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(this@StreamsActivity, "Last", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class StreamsListAdapter(private var streams: List<Stream>) : RecyclerView.Adapter<StreamsListAdapter.ViewHolder>() {

        var context: Context? = null

        private fun getStream(position: Int): Stream {
            return streams[position]
        }

        fun setStream(streams: List<Stream>, context: Context) {
            this.streams = streams
            this.context = context
            // Reloads the RecyclerView with new adapter data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_steams_detail, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val stream = getStream(position)
            holder.titleView.text = stream.title
            holder.authorView.text = stream.userName

            var image = stream.thumbnailUrl.toString()
            image = Regex("\\{width\\}").replace(image, "250")
            image = Regex("\\{height\\}").replace(image, "200")

            this.context?.let {
                Glide.with(it)
                        .load(image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageView)
            }
        }

        // Returns total items in Adapter
        override fun getItemCount(): Int {
            return streams.size
        }

        // Holds an instance to the view for re-use
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleView: TextView = view.findViewById(R.id.title)
            val authorView: TextView = view.findViewById(R.id.author)
            val imageView: ImageView = view.findViewById(R.id.image_book)
        }
    }
}