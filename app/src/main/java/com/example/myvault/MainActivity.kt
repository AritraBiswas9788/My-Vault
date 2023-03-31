package com.example.myvault

import android.app.AlertDialog
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
import kotlin.system.exitProcess

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
                showBar()
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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            showUploadTypeDialog(intent)
        }

        bar.replaceMenu(R.menu.bottom_action_menu)
        bar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    //Toast.makeText(this, "Profile Activity", Toast.LENGTH_SHORT)
                        //.show()
                    val intent =Intent(this,profileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.Storage -> {
                    //Toast.makeText(this, "Storage Activity", Toast.LENGTH_SHORT)
                      //  .show()

                    val intent =Intent(this,StorageAnalyzer::class.java)
                    startActivity(intent)
                    true
                }
                R.id.LogOut -> {
                    //Toast.makeText(this, "LogOut Activity", Toast.LENGTH_SHORT)
                        //.show()
                    logUserOut()
                    true
                }
                R.id.Exit -> {
                    val builder: AlertDialog.Builder= AlertDialog.Builder(this)
                    builder.setMessage("Do you want to exit the application?")
                    builder.setTitle("EXIT APPLICATION")
                    builder.apply {
                        setPositiveButton("YES") { dialog, id ->
                            finishAffinity()
                            exitProcess(0)
                        }
                        setNegativeButton("CANCEL"){dialog, id ->
                            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val dialog: AlertDialog =builder.create()
                    dialog.show()
                    true
                }
                else -> false
            }
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
                 //Toast.makeText(this,"IMAGE UPLOAD ACTIVITY", Toast.LENGTH_SHORT).show()
                 dialog.hide()
                 intent.type="image/*"
                 launchImagePickActivity(intent)

             }
             pdfButton.setOnClickListener {
                 //Toast.makeText(this,"PDF UPLOAD ACTIVITY", Toast.LENGTH_SHORT).show()
                 dialog.hide()
                 intent.type="application/pdf"
                 launchPdfPickActivity(intent)
             }

         }
         public fun hideBar()
         {if(fab.isShown) {
             bar.performHide()
             fab.hide()
         }
         }
         public fun showBar()
         {if(!fab.isShown) {
             bar.performShow()
             fab.show()
         }
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
             val spinner:ProgressBar=uploadDialog.findViewById(R.id.spinner)
             spinner.visibility=View.GONE
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
                 val userUid = UserAuth.currentUser?.uid
                 var totalMemory:Long= 0L
                 button.isEnabled=false
                 button.visibility=View.GONE
                 spinner.visibility=View.VISIBLE

                 dbRef.child("User").child(userUid!!).child("Storage").get()
                     .addOnSuccessListener { snap ->
                         totalMemory= snap.child("totalMemory").value.toString().toLong()
                         if (getFileSize(this, file!!) < totalMemory) {
                             filename = fileField.text.toString().trim()
                             filename = makeProperFilename(filename!!)
                             if (filename!!.isNotEmpty()) {
                                 uploadToDatabase(filename!!, uploadType, progressBar, uploadDialog)
                             } else
                                 Toast.makeText(this, "File-Name cannot empty!", Toast.LENGTH_SHORT)
                                     .show()
                         } else {
                             createAlertDialog("The size of the file exceeds the Cloud-memory available to you")
                             uploadDialog.hide()
                         }
                     }.addOnFailureListener {
                         Toast.makeText(this, "Data Fetch Failed", Toast.LENGTH_SHORT).show()
                     }
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
         private fun getFileSize(context: Context,uri: Uri):Long
         {
             val fileSize: Long
             val cursor=context.contentResolver.query(uri,null,null,null,null)
             cursor?.moveToFirst()
             fileSize= cursor?.getString(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))!!.toLong()
             cursor.close()
             //Toast.makeText(this, "File-Size: $fileSize", Toast.LENGTH_SHORT).show()
             return fileSize
         }
         private fun createAlertDialog(message:String)
         {

             val builder: AlertDialog.Builder= AlertDialog.Builder(this)
             builder.setMessage(message)
             builder.setTitle("Upload error")
             builder.apply {
                 setPositiveButton("OK") { dialog, id ->
                 }
             }
             val dialog: AlertDialog =builder.create()
             dialog.show()
         }

         /*
         private fun getTotalMemoryLeft():Long
         {val userUid = UserAuth.currentUser?.uid
             var ret:Long= 0L
             dbRef.child("User").child(userUid!!).child("Storage").get()
                 .addOnSuccessListener{ snap ->
                     //val data =snap.child("totalMemory").value.toString()
                     //ret=data.toLong()
                     val data = StorageData(
                         snap.child("imageMemory").value.toString(),
                         snap.child("pdfMemory").value.toString(),
                         snap.child("totalMemory").value.toString()
                     )
                     Toast.makeText(this, "mem in: ${data.totalMemory!!}", Toast.LENGTH_SHORT).show()
                     //snap.ref.removeValue()
                     //val imSize = data.imageMemory!!.toLong()
                     ret = data.totalMemory!!.toLong()
                     //val pdfSize = data.pdfMemory!!.toLong() + byteSize
                 }.addOnFailureListener {
                     Toast.makeText(this, "Data Fetch Failed", Toast.LENGTH_SHORT).show()
                 }
             Toast.makeText(this, "mem ret: $ret", Toast.LENGTH_SHORT).show()
             return ret
         }
          */
         private fun makeProperFilename(filename: String):String
         {   //var name= "$filename "
             //name=name.substring(0, name.indexOf(' '))
             val extension:String?=MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(file!!))
             return "$filename.$extension"
         }
         private fun uploadToDatabase(
             filename: String,
             filetype: String,
             progressBar: ProgressBar,
             uploadDialog: Dialog
         )
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
                             uploadDialog.hide()
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
                 storageManager(upload,uploadType)
             }
             else {
                 val key=dbRef.child("UploadFiles").child(userUid!!).child("PDFs").push().key
                 dbRef.child("UploadFiles").child(userUid).child("PDFs").child(key!!).setValue(upload)
                 storageManager(upload,uploadType)
             }
         }
         private fun storageManager(upload:UploadFile,uploadType: String) {
             val userUid = UserAuth.currentUser?.uid
             if (uploadType.equals("image")) {
                 cloudRef.child("Images/${upload.filename}").metadata.addOnSuccessListener {
                     val byteSize: Long = it.sizeBytes
                     var data: StorageData
                     dbRef.child("User").child(userUid!!).child("Storage").get()
                         .addOnSuccessListener { snap ->
                             data = StorageData(
                                 snap.child("imageMemory").value.toString(),
                                 snap.child("pdfMemory").value.toString(),
                                 snap.child("totalMemory").value.toString()
                             )
                             snap.ref.removeValue()
                             val imSize = data.imageMemory!!.toLong() + byteSize
                             val total = data.totalMemory!!.toLong() - byteSize
                             val pdfSize = data.pdfMemory!!.toLong()
                             dbRef.child("User").child(userUid!!).child("Storage").setValue(
                                 StorageData(
                                     imSize.toString(),
                                     pdfSize.toString(),
                                     total.toString()
                                 )
                             )
                         }
                 }.addOnFailureListener {
                     Toast.makeText(this, "Meta Data Failed", Toast.LENGTH_SHORT).show()
                 }
             }
             else
             {
                 cloudRef.child("PDFs/${upload.filename}").metadata.addOnSuccessListener {
                     val byteSize: Long = it.sizeBytes
                     var data: StorageData
                     dbRef.child("User").child(userUid!!).child("Storage").get()
                         .addOnSuccessListener { snap ->
                             data = StorageData(
                                 snap.child("imageMemory").value.toString(),
                                 snap.child("pdfMemory").value.toString(),
                                 snap.child("totalMemory").value.toString()
                             )
                             snap.ref.removeValue()
                             val imSize = data.imageMemory!!.toLong()
                             val total = data.totalMemory!!.toLong() - byteSize
                             val pdfSize = data.pdfMemory!!.toLong() + byteSize
                             dbRef.child("User").child(userUid!!).child("Storage").setValue(
                                 StorageData(
                                     imSize.toString(),
                                     pdfSize.toString(),
                                     total.toString()
                                 )
                             )
                         }
                 }.addOnFailureListener {
                     Toast.makeText(this, "Meta Data Failed", Toast.LENGTH_SHORT).show()
                 }
             }

         }
         private fun logUserOut()
         {
             val builder: AlertDialog.Builder= AlertDialog.Builder(this)
             builder.setMessage("Do you want to log-out ?")
             builder.setTitle("LOG-OUT")
             builder.apply {
                 setPositiveButton("YES") { dialog, id ->
                     UserAuth.signOut()
                     val prefs=getSharedPreferences("UserData", MODE_PRIVATE)
                     val editor=prefs.edit()
                     editor.putString("UserUID","UID")
                     editor.commit()
                     val intent= Intent(this@MainActivity,launch_screen::class.java)
                     startActivity(intent)
                     finish()
                 }
                 setNegativeButton("CANCEL"){dialog, id ->
                     Toast.makeText(context, "Log-Out Cancelled", Toast.LENGTH_SHORT).show()
                 }
             }
             val dialog: AlertDialog =builder.create()
             dialog.show()
         }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.LogOut)
        {
            logUserOut()
            return true
        }
        if(item.itemId==R.id.Profile)
        {val intent =Intent(this,profileActivity::class.java)
            startActivity(intent)
            return true
        }
        if(item.itemId==R.id.Storage)
        {
            val intent =Intent(this,StorageAnalyzer::class.java)
            startActivity(intent)
            return true
        }
        if(item.itemId==R.id.Exit)
        {
            val builder: AlertDialog.Builder= AlertDialog.Builder(this)
            builder.setMessage("Do you want to exit the application?")
            builder.setTitle("EXIT APPLICATION")
            builder.apply {
                setPositiveButton("YES") { dialog, id ->
                    finishAffinity()
                    exitProcess(0)
                }
                setNegativeButton("CANCEL"){dialog, id ->
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
            val dialog: AlertDialog =builder.create()
            dialog.show()
            return true
        }
        return false
    }
}