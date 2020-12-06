package edu.uoc.pac3.twitch.streams

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import edu.uoc.pac3.R
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.streams.Stream
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.twitch.profile.ProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StreamsActivity : AppCompatActivity() {

    private val TAG = "StreamsActivity"
    private val network = Network.createHttpClient(this)
    private lateinit var adapter: StreamsListAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var streamsTwitch: StreamsResponse? = null
    private var cursor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)
        // Init RecyclerView
        initRecyclerView()

        swipeRefresh = findViewById(R.id.swipeRefreshLayout)

        lifecycleScope.launch {
            whenStarted {
                // TODO: Get Streams
                streamsTwitch = withContext(Dispatchers.IO) {
                    TwitchApiService(network).getStreams(null)
                }
                // TODO: Get Tokens from Twitch
                if (streamsTwitch == null) {
                    Toast.makeText(applicationContext, R.string.error_streams, Toast.LENGTH_LONG).show()
                } else {
                    //primera carga de de streams
                    cursor = streamsTwitch!!.pagination?.cursor
                    streamsTwitch!!.data?.let { adapter.setStream(it, this@StreamsActivity) }

                }
            }
            //se desactiva swipeRefresh, solo se activará cuando haya llegado al final de la lista
            swipeRefresh.isRefreshing = false
            swipeRefresh.isEnabled = false
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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) ) {
                    lifecycleScope.launch {
                        whenStarted {
                            //se activa el swipeRefresh
                            swipeRefresh.isEnabled = true
                            swipeRefresh.isRefreshing = true

                            //se hace la peticion de los nuevos streams
                            streamsTwitch = withContext(Dispatchers.IO) {
                                TwitchApiService(network).getStreams(cursor)
                            }

                            if (streamsTwitch == null) {
                                Toast.makeText(applicationContext, R.string.error_streams, Toast.LENGTH_LONG).show()
                            } else {
                                //cargas posteriores de de streams
                                cursor = streamsTwitch!!.pagination?.cursor
                                streamsTwitch!!.data?.let { adapter.addStream(it) }
                            }
                        }
                        //luego de finalizar la carga de los streams se desactiva swipeRefresh
                        //Handler().postDelayed( {
                        swipeRefresh.isRefreshing = false
                        swipeRefresh.isEnabled = false
                        //}, 1500)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
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
            notifyDataSetChanged()
        }

        //se agregan los nuevos streams al listado
        fun addStream(streams: List<Stream>) {
            this.streams = this.streams + streams
            notifyItemRangeInserted(this.streams.size - streams.size, this.streams.size)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_streams_detail, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val stream = getStream(position)
            holder.titleView.text = stream.title
            holder.authorView.text = stream.userName

            val imageSize: String = R.dimen.stream_item_image_width.toString() + "x" + R.dimen.stream_item_image_height.toString()
            this.context?.let {
                Glide.with(it)
                        .load(stream.thumbnailUrl?.replace("{width}x{height}", imageSize))
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