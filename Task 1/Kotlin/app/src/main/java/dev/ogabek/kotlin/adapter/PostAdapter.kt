package dev.ogabek.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.ogabek.kotlin.activity.MainActivity
import dev.ogabek.kotlin.R
import dev.ogabek.kotlin.model.Post

class PostAdapter(var activity: MainActivity, var items: ArrayList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_list, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post: Post = items[position]
        if (holder is PostViewHolder) {
            Glide.with(activity).load(post.img).placeholder(R.drawable.loading01).into(holder.image)
            holder.tv_title.text = post.title!!.toUpperCase()
            holder.tv_body.text = post.body
        }
    }

    inner class PostViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var tv_title: TextView = view.findViewById(R.id.tv_title)
        var tv_body: TextView = view.findViewById(R.id.tv_body)
        val image : ImageView = view.findViewById(R.id.iv_main)
    }

    init {
        this.activity = activity
        this.items = items
    }
}