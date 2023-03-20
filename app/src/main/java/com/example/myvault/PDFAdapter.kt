package com.example.myvault

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PDFAdapter(val context: Context, val fileList: ArrayList<UploadFile>): RecyclerView.Adapter<PDFAdapter.PDFViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.pdfframe,parent,false)
        return PDFViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        val file:UploadFile=fileList[position]
        holder.title.text=file.filename
    }
    class PDFViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var title:TextView=itemView.findViewById(R.id.PDFTitle)
    }
}



