package com.example.myvault

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class FullPDFViewer : AppCompatActivity() {

    private lateinit var pdfFrame:com.github.barteksc.pdfviewer.PDFView
    private lateinit var fab:com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
    private lateinit var viewMode:com.google.android.material.button.MaterialButtonToggleGroup

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_pdfviewer)
        val name:String=intent.getStringExtra("Name")!!
        val fileUri:String=intent.getStringExtra("Uri")!!
        supportActionBar?.title =name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        pdfFrame=findViewById(R.id.PDFView)
        fab=findViewById(R.id.DownloadFab)
        viewMode=findViewById(R.id.ButtonGroup)
        viewMode.check(R.id.ScrollerMode)
        viewMode.isSelectionRequired=true
        loadPDFFileWithPDFMode("Scroller",fileUri)
        /*
        val executor=Executors.newSingleThreadExecutor()
        val handler=Handler(Looper.getMainLooper())

        executor.execute {
            var inputStream: InputStream? =null

            try {
                val url: URL = URL(fileUri)
                val urlConnection:HttpURLConnection=url.openConnection() as HttpURLConnection
                if(urlConnection.responseCode==200)
                    inputStream=BufferedInputStream(urlConnection.inputStream)
            }
            catch (error:IOException)
            {
                Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show()
            }
            handler.post {
                pdfFrame.fromStream(inputStream).load()
                viewMode.check(R.id.ScrollerMode)
            }

        }

         */
        fab.setOnClickListener {
            val downloader= FullImageViewer.AndroidDownloader(this)
            downloader.downloadFile(fileUri,name)
        }
        pdfFrame.setOnClickListener {
            if(fab.isShown) {
                viewMode.visibility=View.GONE
                fab.hide()
            }
            else {
                viewMode.visibility=View.VISIBLE
                fab.show()
            }
        }

        viewMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId){
                    R.id.ReaderMode -> loadPDFFileWithPDFMode("Reader",fileUri)
                    R.id.ScrollerMode -> loadPDFFileWithPDFMode("Scroller",fileUri)
                }
            } else
                if(viewMode.checkedButtonId== View.NO_ID) {
                    loadPDFFileWithPDFMode("Scroller",fileUri)
                }
        }
        //pdfFrame.fromUri(fileUri.toUri()).load()

    }
    private fun loadPDFFileWithPDFMode(viewmode:String,fileUri:String)
    { val executor=Executors.newSingleThreadExecutor()
        val handler=Handler(Looper.getMainLooper())

        executor.execute {
            var inputStream: InputStream? =null

            try {
                val url: URL = URL(fileUri)
                val urlConnection:HttpURLConnection=url.openConnection() as HttpURLConnection
                if(urlConnection.responseCode==200)
                    inputStream=BufferedInputStream(urlConnection.inputStream)
            }
            catch (error:IOException)
            {
                Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show()
            }
            handler.post {
                if(viewmode.equals("Reader"))
                {
                    Toast.makeText(this,"Reader Mode",Toast.LENGTH_SHORT).show()
                    pdfFrame.fromStream(inputStream)
                        .swipeHorizontal(true)
                        .scrollHandle(DefaultScrollHandle(this))
                        .pageSnap(true) // snap pages to screen boundaries
                        .pageFling(true) // make a fling change only a single page like ViewPager
                        .autoSpacing(true)
                        .load()

                }
                else
                {
                    Toast.makeText(this,"Reader Mode",Toast.LENGTH_SHORT).show()
                    pdfFrame.fromStream(inputStream)
                        .scrollHandle(DefaultScrollHandle(this))
                        .load()
                }
            }

        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}

/*
class RetrievePDFStream extends AsyncTask<String,Void,InputStream>{
    override protected InputStream doInBackground(String... strings) {
        InputStream inputStream=null;

        try{

            URL urlx=new URL(strings[0]);
            HttpURLConnection urlConnection=(HttpURLConnection) urlx.openConnection();
            if(urlConnection.getResponseCode()==200){
                inputStream=new BufferedInputStream(urlConnection.getInputStream());

            }
        }catch (IOException e){
            return null;
        }
        return inputStream;

    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        pdfView.fromStream(inputStream).load();
    }

 */