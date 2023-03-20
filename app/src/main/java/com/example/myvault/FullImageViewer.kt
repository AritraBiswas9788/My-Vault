package com.example.myvault

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FullImageViewer : AppCompatActivity() {

    private lateinit var fab:com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
    private lateinit var image:ImageView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image_viewer)
        val name:String=intent.getStringExtra("Name")!!
        val fileUri:String=intent.getStringExtra("Uri")!!
        supportActionBar?.title =name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab=findViewById(R.id.DownloadFab)
        image=findViewById(R.id.ImageScreen)

        image.setOnClickListener {
            if(fab.isShown)
                fab.hide()
            else
                fab.show()
        }

        Glide.with(this).load(fileUri).into(image)
        fab.setOnClickListener {
            val downloader= AndroidDownloader(this)
            downloader.downloadFile(fileUri,name)

        }



    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    interface Downloader
    {
        fun downloadFile(url:String,name: String):Long
    }

    class AndroidDownloader(context: Context,):Downloader{

        @RequiresApi(Build.VERSION_CODES.M)
        private val downloadManager=context.getSystemService(DownloadManager::class.java)
        @RequiresApi(Build.VERSION_CODES.M)
        override fun downloadFile(url: String, name: String): Long {
            val request = DownloadManager.Request(url.toUri())
                .setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.substring(name.lastIndexOf('.')+1)))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(name)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name)
            return downloadManager.enqueue(request)

        }

    }
    class downloadCompletedReceiver: BroadcastReceiver(){
       // private lateinit var downloadManager:DownloadManager
        //@RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            //downloadManager = context!!.getSystemService(DownloadManager::class.java)
            if(intent?.action == "android.intent.action.DOWNLOAD_COMPETE")
            {
                val id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1L)
                //val query =DownloadManager.Query()
                if(id!=-1L)
                    Toast.makeText(context,"DOWNLOAD WITH ID $id FINISHED",Toast.LENGTH_SHORT).show()
            }
        }

    }
}