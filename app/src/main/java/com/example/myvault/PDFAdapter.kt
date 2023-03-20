package com.example.myvault

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

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

        holder.itemView.setOnClickListener {
            val intent= Intent(context,FullPDFViewer::class.java)
            intent.putExtra("Name",file.filename)
            intent.putExtra("Uri",file.fileUri)
            context.startActivity(intent)
        }
        holder.delButton.setOnClickListener {
            //Toast.makeText(context,"Delete action!!", Toast.LENGTH_SHORT).show()
            val builder:AlertDialog.Builder= AlertDialog.Builder(context)
            builder.setMessage("Do you want to delete ${file.filename} permanently??")
            builder.apply {
                setPositiveButton("Yes") { dialog, id ->
                    Toast.makeText(context, "Delete action!!", Toast.LENGTH_SHORT).show()
                        deleteFile(file)
                }
                setNegativeButton("Cancel") { dialog, id ->
                    Toast.makeText(context, "Cancelled!!", Toast.LENGTH_SHORT).show()
                }
            }
            val dialog:AlertDialog=builder.create()
            dialog.show()

        }
    }
    private fun deleteFile(file: UploadFile) {
        var dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        var cloudRef: StorageReference = Firebase.storage.reference
        var UserAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userUid = UserAuth.currentUser?.uid
        cloudRef.child("PDFs/${file.filename}").delete().addOnSuccessListener {
            Toast.makeText(context,"File Deleted Successfully!", Toast.LENGTH_SHORT).show()
            dbRef.child("UploadFiles").child(userUid!!).child("PDFs").addValueEventListener(object: ValueEventListener{
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

    class PDFViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var title:TextView=itemView.findViewById(R.id.PDFTitle)
        var delButton:ImageButton=itemView.findViewById(R.id.delButton)
    }
}



