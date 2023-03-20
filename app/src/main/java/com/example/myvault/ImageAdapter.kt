package com.example.myvault

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

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
        holder.itemView.setOnClickListener {
            val intent=Intent(context,FullImageViewer::class.java)
            intent.putExtra("Name",file.filename)
            intent.putExtra("Uri",file.fileUri)
            context.startActivity(intent)
        }
        holder.delButton.setOnClickListener {
            val builder: AlertDialog.Builder= AlertDialog.Builder(context)
            builder.setMessage("Do you want to delete ${file.filename} permanently??")
            builder.apply {
                setPositiveButton("Yes") { dialog, id ->
                    //Toast.makeText(context, "Delete action!!", Toast.LENGTH_SHORT).show()
                    deleteFile(file)
                }
                setNegativeButton("Cancel") { dialog, id ->
                    Toast.makeText(context, "Cancelled!!", Toast.LENGTH_SHORT).show()
                }
            }
            val dialog: AlertDialog =builder.create()
            dialog.show()

        }
    }
    private fun deleteFile(file: UploadFile) {
        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        val cloudRef: StorageReference = Firebase.storage.reference
        val UserAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userUid = UserAuth.currentUser?.uid
        cloudRef.child("Images/${file.filename}").delete().addOnSuccessListener {
            Toast.makeText(context,"File Deleted Successfully!", Toast.LENGTH_SHORT).show()
            dbRef.child("UploadFiles").child(userUid!!).child("Images").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshot in snapshot.children)
                    {
                        if(postSnapshot.child("fileUri").value.toString().equals(file.fileUri))
                            postSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to Delete File for $it", Toast.LENGTH_SHORT).show()
            }
    }
    class ImageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var image:ImageView=itemView.findViewById(R.id.imageRes)
        var title:TextView=itemView.findViewById(R.id.ImageTitle)
        var delButton:ImageButton=itemView.findViewById(R.id.delButton)
    }
}