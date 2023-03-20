package com.example.myvault

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity()
     {private lateinit var UserAuth: FirebaseAuth
      private lateinit var dbRef: DatabaseReference
      private lateinit var cloudRef:StorageReference
      private lateinit var layoutTabs:TabLayout
      private lateinit var viewPager: ViewPager2
      private lateinit var fragmentAdapter: FragmentPageAdapter
      private lateinit var fab: com.google.android.material.floatingactionbutton.FloatingActionButton
      private lateinit var bar:BottomAppBar
      private var file: Uri? = null
         private val imageLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                 result ->
             if (result.resultCode== RESULT_OK) {
                 result.data?.data.let {
                     file=it
                 }
                 showUploadDialog("image")
             }
             else
                 Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
         }
         private val pdfLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                 result ->
             if (result.resultCode== RESULT_OK) {
                 result.data?.data.let {
                     file=it
                 }
                 showUploadDialog("pdf")
             }
             else
                 Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
         }


    override fun onCreate(savedInstanceState: Bundle?) {
        //supportActionBar!!.setBackgroundDrawable( ColorDrawable(Color.parseColor("000000")))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbRef=FirebaseDatabase.getInstance().getReference()
        UserAuth= FirebaseAuth.getInstance()
        cloudRef=Firebase.storage.reference
        layoutTabs=findViewById(R.id.tabLayout)
        viewPager=findViewById(R.id.viewpager)
        fragmentAdapter= FragmentPageAdapter(supportFragmentManager,lifecycle)
        fab=findViewById(R.id.fab)
        bar=findViewById(R.id.bottomAppBar)
        layoutTabs.addTab(layoutTabs.newTab().setText("IMAGES"))
        layoutTabs.addTab(layoutTabs.newTab().setText("PDFs"))
        viewPager.adapter=fragmentAdapter
        layoutTabs.addOnTabSelectedListener(object :OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem= tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                layoutTabs.selectTab(layoutTabs.getTabAt(position))
            }
        })

        fab.setOnClickListener {
            val intent=Intent(Intent.ACTION_GET_CONTENT)
            showUploadTypeDialog(intent)
        }

    }
         private fun showUploadTypeDialog(intent: Intent) {
             val dialog: Dialog = Dialog(this)
             dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
             dialog.setContentView(R.layout.bottomuploadtype)
             val imgButton: ImageButton =dialog.findViewById(R.id.imgButton)
             val pdfButton: ImageButton =dialog.findViewById(R.id.pdfButton)
             dialog.show()
             dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
             dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             dialog.window?.attributes?.windowAnimations=R.style.DialogAnimation
             dialog.window?.setGravity(Gravity.BOTTOM)

             imgButton.setOnClickListener{
                 Toast.makeText(this,"IMAGE UPLOAD ACTIVITY", Toast.LENGTH_SHORT).show()
                 dialog.hide()
                 intent.type="image/*"
                 launchImagePickActivity(intent)

             }
             pdfButton.setOnClickListener {
                 Toast.makeText(this,"PDF UPLOAD ACTIVITY", Toast.LENGTH_SHORT).show()
                 dialog.hide()
                 intent.type="application/pdf"
                 launchPdfPickActivity(intent)
             }

         }
         public fun hideBar()
         {
             bar.performHide()
             fab.hide()
         }
         public fun showBar()
         {
             bar.performShow()
             fab.show()
         }
         private fun launchImagePickActivity(intent: Intent) {

             imageLauncher.launch(intent)

         }
         private fun launchPdfPickActivity(intent: Intent) {

             pdfLauncher.launch(intent)

         }
         private fun showUploadDialog(uploadType:String)
         {
             val uploadDialog: Dialog = Dialog(this)
             uploadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
             uploadDialog.setContentView(R.layout.bottomuploadlayout)
             val imgFrame: ImageView =uploadDialog.findViewById(R.id.imageFrame)
             val pdfFrame:com.github.barteksc.pdfviewer.PDFView=uploadDialog.findViewById(R.id.pdfFrame)
             val button: Button =uploadDialog.findViewById(R.id.uploadButton)
             val fileField: EditText =uploadDialog.findViewById(R.id.fileField)
             val progressBar:ProgressBar=uploadDialog.findViewById(R.id.progressBar)
             val title:TextView=uploadDialog.findViewById(R.id.title)
             uploadDialog.show()
             uploadDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
             uploadDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             uploadDialog.window?.attributes?.windowAnimations=R.style.DialogAnimation
             uploadDialog.window?.setGravity(Gravity.BOTTOM)
             var filename= getFileName(this, file!!)
             filename= filename!!.substring(0, filename.lastIndexOf('.'))
             fileField.setText(filename,TextView.BufferType.EDITABLE)
             if (uploadType.equals("image")) {
                 pdfFrame.visibility=View.GONE
                 imgFrame.visibility=View.VISIBLE
                 imgFrame.setImageURI(file)
             }
             else {
                   pdfFrame.visibility=View.VISIBLE
                   imgFrame.visibility=View.GONE
                   title.text = "Upload PDF"
                   pdfFrame.fromUri(file).load()
                  }
             button.setOnClickListener {
                 filename=fileField.text.toString().trim()
                 filename=makeProperFilename(filename!!)
                 if(filename!!.isNotEmpty()) {
                     uploadToDatabase(filename!!,uploadType,progressBar,uploadDialog)
                     //
                 }
                 else
                     Toast.makeText(this,"File-Name cannot empty!",Toast.LENGTH_SHORT).show()
             }
         }
         private fun getFileName(context: Context,uri: Uri):String?
         {
             val filename:String?
             val cursor=context.contentResolver.query(uri,null,null,null,null)
             cursor?.moveToFirst()
             filename= cursor?.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
             cursor?.close()
             return filename
         }

         private fun makeProperFilename(filename: String):String
         {   var name= "$filename "
             name=name.substring(0, name.indexOf(' '))
             val extension:String?=MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(file!!))
             return "$name.$extension"
         }
         private fun uploadToDatabase(filename:String,filetype:String,progressBar: ProgressBar,uploadDialog:Dialog)
         {
             try{
                 file.let {
                     if (filetype.equals("image")) {
                         cloudRef.child("Images/$filename").putFile(file!!).addOnCompleteListener {
                             Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
                             uploadDialog.hide()
                             cloudRef.child("Images/$filename").downloadUrl.addOnSuccessListener { uri ->
                                 noteToDataBase(UploadFile(filename,uri.toString()),filetype)
                             }
                         //noteToDataBase(UploadFile(filename,cloudRef.child("Images/$filename").downloadUrl.toString()),filetype)
                         }.addOnFailureListener {
                             Toast.makeText(this, "Error on Upload", Toast.LENGTH_SHORT).show()
                         }.addOnProgressListener { taskSnapShot ->
                             val progress: Double = (100.0*(taskSnapShot.bytesTransferred))/taskSnapShot.totalByteCount
                             progressBar.progress = progress.toInt()
                         }
                     }
                     else
                     {
                         cloudRef.child("PDFs/$filename").putFile(file!!).addOnCompleteListener {
                             Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
                             uploadDialog.hide()
                             cloudRef.child("PDFs/$filename").downloadUrl.addOnSuccessListener { uri ->
                                 noteToDataBase(UploadFile(filename,uri.toString()),filetype)
                             }
                         }.addOnFailureListener {
                             Toast.makeText(this, "Error on Upload", Toast.LENGTH_SHORT).show()
                         }.addOnProgressListener { taskSnapShot ->
                             val progress: Double = (100.0*(taskSnapShot.bytesTransferred))/taskSnapShot.totalByteCount
                             progressBar.progress = progress.toInt()
                         }
                     }
                 }
             }
             catch(e:Exception)
             {
                 Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show()
             }
         }
         private fun noteToDataBase(upload:UploadFile,uploadType: String)
         {
             val userUid = UserAuth.currentUser?.uid
             if(uploadType.equals("image")) {
                 val key=dbRef.child("UploadFiles").child(userUid!!).child("Images").push().key
                 dbRef.child("UploadFiles").child(userUid).child("Images").child(key!!).setValue(upload)
             }
             else {
                 val key=dbRef.child("UploadFiles").child(userUid!!).child("PDFs").push().key
                 dbRef.child("UploadFiles").child(userUid).child("PDFs").child(key!!).setValue(upload)
             }
         }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.LogOut)
        {
            //Logout logic
            UserAuth.signOut()
            val prefs=getSharedPreferences("UserData", MODE_PRIVATE)
            val editor=prefs.edit()
            editor.putString("UserUID","UID")
            editor.commit()
            val intent= Intent(this@MainActivity,launch_screen::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return false
    }
}