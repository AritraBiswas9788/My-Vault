package com.example.myvault

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(val context: Context, val fileList: ArrayList<UploadFile>):RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.imageframe,parent,false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val file:UploadFile= fileList[position]
        holder.title.text=file.filename
        Glide.with(context).load(file.fileUri).into(holder.image)
    }
    class ImageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var image:ImageView=itemView.findViewById(R.id.imageRes)
        var title:TextView=itemView.findViewById(R.id.ImageTitle)
    }
}