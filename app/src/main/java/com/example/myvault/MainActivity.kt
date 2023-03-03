package com.example.myvault

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity()
{private lateinit var UserAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbRef=FirebaseDatabase.getInstance().getReference()
        UserAuth= FirebaseAuth.getInstance()
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